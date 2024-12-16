package es.degrassi.mmreborn.common.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.controller.ComponentMapper;
import es.degrassi.mmreborn.client.model.ControllerBakedModel;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.CraftingCheckResult;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateRecipePacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineControllerEntity extends BlockEntityRestrictedTick implements ComponentMapper {
  private CraftingStatus craftingStatus = CraftingStatus.MISSING_STRUCTURE;

  private ResourceLocation id = DynamicMachine.DUMMY.getRegistryName();
  @Nullable
  private ActiveMachineRecipe activeRecipe = null;

  private int recipeTicks = -1;
  private int ticksToUpdateComponent = 0;
  private boolean structureChecked = false;

  private final List<MachineComponent<?>> foundComponents = Lists.newArrayList();

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
  }

  @Override
  public ModelData getModelData() {
    return ModelData.builder()
        .with(ControllerBakedModel.MACHINE, getFoundMachine())
        .build();
  }

  @Override
  public void doRestrictedTick() {
    if (getBlockState().getAnalogOutputSignal(getLevel(), getBlockPos()) > 0) {
      return;
    }

    checkStructure();

    if (craftingStatus.isMissingStructure()) return;

    updateComponents();

    if (this.activeRecipe == null) {
      if (level.getGameTime() % MMRConfig.get().checkRecipeTicks.get() == 0) {
        searchAndUpdateRecipe();
      }
    } else if (this.recipeTicks > -1) {
      if (!this.activeRecipe.isInitialized()) this.activeRecipe.init();
      if (this.activeRecipe.getHolder() == null && !this.craftingStatus.isCrafting() && !this.craftingStatus.isFailure()) {
        this.setActiveRecipe(null);
        this.setRecipeTicks(-1);
        this.setCraftingStatus(CraftingStatus.NO_RECIPE);
        setChanged();
      } else if (this.activeRecipe.getHolder() != null) {
        useRecipe();
      }
    }
    setChanged();
  }

  public void setRecipeTicks(int recipeTicks) {
    setRequestModelUpdate(true);
    setChanged();
    this.recipeTicks = recipeTicks;
    if (getLevel() instanceof ServerLevel l && activeRecipe != null)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateRecipePacket(activeRecipe.getHolder().id(), recipeTicks, getBlockPos()));
  }

  private void useRecipe() {
    RecipeCraftingContext context = this.getFoundMachine().createContext(this.activeRecipe, this, this.foundComponents);
    if (context == null) return;
    if (activeRecipe == null) return;
    this.setCraftingStatus(this.activeRecipe.tick(context)); //handle energy IO and tick progression

    if (this.activeRecipe.getRecipe().doesCancelRecipeOnPerTickFailure() && this.craftingStatus.isFailure()) {
      this.activeRecipe = null;
      setRecipeTicks(-1);
    } else if (this.activeRecipe.isCompleted(context) &&
        !context.canStartCrafting(req -> req.getActionType() == IOType.OUTPUT).isFailure()) {
      this.activeRecipe.complete(context);
      setRecipeTicks(-1);
      setCraftingStatus(CraftingStatus.NO_RECIPE);
      context = this.getFoundMachine().createContext(this.activeRecipe, this, this.foundComponents);
      if (context == null) {
        this.activeRecipe = null;
        setRecipeTicks(-1);
        setCraftingStatus(CraftingStatus.NO_RECIPE);
        return;
      }
      CraftingCheckResult result = context.canStartCrafting();
      if (result.isFailure()) {
        this.activeRecipe = null;
        setCraftingStatus(CraftingStatus.failure(Iterables.getFirst(result.getUnlocalizedErrorMessages(), "")));
      } else {
        this.activeRecipe.start(context);
      }
    }
    setChanged();
  }

  private void searchAndUpdateRecipe() {
    setChanged();
    if (getLevel() == null) return;
    List<RecipeHolder<MachineRecipe>> availableRecipes =
        getLevel()
            .getRecipeManager()
            .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
            .stream()
            .filter(recipe -> recipe.value().getOwningMachineIdentifier() != null)
            .filter(recipe -> recipe.value().getOwningMachineIdentifier().equals(this.getId()))
            .toList();

    RecipeHolder<MachineRecipe> highestValidity = null;
    CraftingCheckResult highestValidityResult = null;
    float validity = 0F;

    for (RecipeHolder<MachineRecipe> recipe : availableRecipes) {
      ActiveMachineRecipe aRecipe = new ActiveMachineRecipe(recipe, this);
      RecipeCraftingContext context = this.getFoundMachine().createContext(aRecipe, this, this.foundComponents);
      if (context == null) continue;
      CraftingCheckResult result = context.canStartCrafting();
      if (!result.isFailure()) {
        this.activeRecipe = aRecipe;
        setRecipeTicks(0);
        this.activeRecipe.start(context);
        break;
      } else if (highestValidity == null ||
          (result.getValidity() >= 0.5F && result.getValidity() > validity)) {
        highestValidity = recipe;
        highestValidityResult = result;
        validity = result.getValidity();
      }
    }

    if (this.activeRecipe == null) {
      setRecipeTicks(-1);
      if (highestValidity != null && validity >= .5) {
        setCraftingStatus(CraftingStatus.failure(
            Iterables.getFirst(highestValidityResult.getUnlocalizedErrorMessages(), "")));
      }
    } else {
      setCraftingStatus(CraftingStatus.working());
    }
  }

  public void setCraftingStatus(CraftingStatus status) {
    this.craftingStatus = status;
    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateCraftingStatusPacket(status, getBlockPos()));
    setRequestModelUpdate(true);
    setChanged();
  }

  public void setMachine(ResourceLocation machine) {
    this.id = machine;
    distributeCasingColor(false, getBlockPos());
    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SMachineUpdatePacket(id, getBlockPos()));
    setRequestModelUpdate(true);
    setChanged();
  }

  private void checkStructure() {
    if (ticksToUpdateComponent % MMRConfig.get().checkRecipeTicks.get() == 0) {
      setCraftingStatus(CraftingStatus.NO_RECIPE);
      ticksToUpdateComponent = 1;
    }
    if (level.getGameTime() % MMRConfig.get().checkStructureTicks.get() == 0) {
      this.getFoundMachine();
      if (this.getFoundMachine() != DynamicMachine.DUMMY) {
        if (!getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
          distributeCasingColor(true);
          this.activeRecipe = null;
          setRecipeTicks(-1);
          setCraftingStatus(CraftingStatus.MISSING_STRUCTURE);
          structureChecked = false;
        } else {
          distributeCasingColor(false);
        }
        setRequestModelUpdate(true);
        setChanged();
      }
    }
    ticksToUpdateComponent++;
  }

  public void distributeCasingColor(boolean default_, BlockPos... poss) {
    int color = default_ ? Config.machineColor : getFoundMachine().getMachineColor();
    for (BlockPos pos : poss) {
      if (getLevel().getBlockEntity(pos) instanceof MachineControllerEntity entity)
        tryColorize(pos, entity.getFoundMachine().getMachineColor());
      else
        tryColorize(this.getBlockPos().offset(pos), color);
    }
    tryColorize(getBlockPos(), getFoundMachine().getMachineColor());
  }

  public void distributeCasingColor(boolean default_) {
    if (ModularMachineryReborn.MACHINES.get(id) != DynamicMachine.DUMMY) {
      distributeCasingColor(default_, getFoundMachine().getPattern().getBlocks(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet().toArray(BlockPos[]::new));
    } else {
      getBlueprintMachine();
      if (!getBlueprintMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
        BlockPos[] blockPos = getBlueprintMachine().getPattern().getBlocks(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet().toArray(BlockPos[]::new);
        distributeCasingColor(true, blockPos);
        for (BlockPos pos : blockPos) {
          if (getLevel().getBlockEntity(getBlockPos().offset(pos)) instanceof BlockEntitySynchronized entity) {
            entity.setInStructure(false);
            entity.setChanged();
          }
        }
      }
    }
  }

  private void tryColorize(BlockPos pos, int color) {
    BlockEntity te = this.getLevel().getBlockEntity(pos);
    if (te instanceof ColorableMachineComponentEntity entity) {
      entity.setMachineColor(color);
      entity.setChanged();
    }
  }

  private void updateComponents() {
    if (getFoundMachine() == DynamicMachine.DUMMY) return;
    if (level.getGameTime() % 20 == 0) {
      this.foundComponents.clear();
      for (BlockPos potentialPosition : this.getFoundMachine().getPattern().getBlocks(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet()) {
        BlockPos realPos = getBlockPos().offset(potentialPosition);
        BlockEntity te = getLevel().getBlockEntity(realPos);
        if (te instanceof MachineComponentEntity entity) {
          MachineComponent<?> component = entity.provideComponent();
          if (component != null) {
            this.foundComponents.add(component);
          }
        }
      }
    }
    setChanged();
  }

  public float getCurrentActiveRecipeProgress() {
    if (this.activeRecipe == null || this.recipeTicks < 0 || this.getActiveRecipe().getRecipe() == null) return 0F;
    float maxTick = this.activeRecipe.getRecipe().getRecipeTotalTickTime();
    return Mth.clamp(this.recipeTicks / maxTick, 0F, 1F);
  }

  public boolean hasActiveRecipe() {
    return this.activeRecipe != null && this.activeRecipe.getHolder() != null && this.activeRecipe.getRecipe() != null && this.recipeTicks > -1;
  }

  public DynamicMachine getFoundMachine() {
    return ModularMachineryReborn.MACHINES.getOrDefault(id, DynamicMachine.DUMMY);
  }

  public DynamicMachine getBlueprintMachine() {
    return getFoundMachine();
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);

    this.craftingStatus = CraftingStatus.deserialize(compound.getCompound("status"));

    this.id = ResourceLocation.parse(compound.getString("machine"));

    if (compound.contains("tick", Tag.TAG_INT))
      this.recipeTicks = compound.getInt("tick");

    if (compound.contains("recipe")) {
      CompoundTag tag = compound.getCompound("recipe");
      this.activeRecipe = new ActiveMachineRecipe(tag, this);
    } else {
      this.activeRecipe = null;
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.put("status", this.craftingStatus.serializeNBT());
    compound.putString("machine", id.toString());
    compound.putInt("tick", this.recipeTicks);
    if (this.activeRecipe != null) {
      compound.put("recipe", this.activeRecipe.serialize());
    }
  }

  public void refreshClientData() {
    requestModelDataUpdate();
  }

  @Override
  public Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    Map<BlockPos, MachineComponent<?>> map = new LinkedHashMap<>();
    for(BlockPos potentialPosition : getFoundMachine().getPattern().getBlocks(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet()) {
      BlockPos realPos = this.getBlockPos().offset(potentialPosition);
      BlockEntity te = this.getLevel().getBlockEntity(realPos);
      if (te instanceof MachineComponentEntity entity) {
        MachineComponent<?> component = entity.provideComponent();
        if (component != null) {
          map.put(realPos, component);
        }
      }
    }
    return map;
  }
}
