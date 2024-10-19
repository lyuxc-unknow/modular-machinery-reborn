package es.degrassi.mmreborn.common.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
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
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MachineControllerEntity extends BlockEntityRestrictedTick {
  public static final int BLUEPRINT_SLOT = 0;
  public static final int ACCELERATOR_SLOT = 1;

  private CraftingStatus craftingStatus = CraftingStatus.MISSING_STRUCTURE;

  private ResourceLocation id = DynamicMachine.DUMMY.getRegistryName();
  private IOInventory inventory;
  private ActiveMachineRecipe activeRecipe = null;

  private int recipeTicks = 0;

  private final List<MachineComponent<?>> foundComponents = Lists.newArrayList();

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
    this.inventory = buildInventory();
    this.inventory.setStackLimit(1, BLUEPRINT_SLOT);
  }

  private IOInventory buildInventory() {
    return new IOInventory(this,
      new int[]{ },
      new int[]{ }
    );//.setMiscSlots(BLUEPRINT_SLOT, ACCELERATOR_SLOT);
  }

  @Override
  public void doRestrictedTick() {
    if (getBlockState().getAnalogOutputSignal(getLevel(), getBlockPos()) > 0) {
      return;
    }

    checkStructure();
    updateComponents();

    if (this.activeRecipe == null) {
      if (this.ticksExisted % MMRConfig.get().general.checkRecipeTicks == 0) {
        searchAndUpdateRecipe();
      }
    } else {
      useRecipe();
    }
    setChanged();
  }

  private void useRecipe() {
    RecipeCraftingContext context = this.getFoundMachine().createContext(this.activeRecipe, this, this.foundComponents);
    this.craftingStatus = this.activeRecipe.tick(this, context); //handle energy IO and tick progression

    if (this.activeRecipe.getRecipe().doesCancelRecipeOnPerTickFailure() && !this.craftingStatus.isCrafting()) {
      this.activeRecipe = null;
    } else if (this.activeRecipe.isCompleted(this, context) &&
      !context.canStartCrafting(req -> req.getActionType() == IOType.OUTPUT).isFailure()) {
      this.activeRecipe.complete(context);
      this.activeRecipe.reset();
      context = this.getFoundMachine().createContext(this.activeRecipe, this, this.foundComponents);
      RecipeCraftingContext.CraftingCheckResult result = context.canStartCrafting();
      if (result.isFailure()) {
        this.activeRecipe = null;
        searchAndUpdateRecipe();
      } else {
        this.activeRecipe.start(context);
        setCraftingStatus(CraftingStatus.working());
      }
    }
    setChanged();
  }

  private void searchAndUpdateRecipe() {
    if (getLevel() == null) return;
    List<MachineRecipe> availableRecipes =
      getLevel()
        .getRecipeManager()
        .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
        .stream()
        .map(RecipeHolder::value)
        .filter(recipe -> recipe.getOwningMachine() != null)
        .filter(recipe -> recipe.getOwningMachine().equals(this.getFoundMachine()))
        .toList();

    MachineRecipe highestValidity = null;
    RecipeCraftingContext.CraftingCheckResult highestValidityResult = null;
    float validity = 0F;

    for (MachineRecipe recipe : availableRecipes) {
      ActiveMachineRecipe aRecipe = new ActiveMachineRecipe(recipe, this);
      RecipeCraftingContext context = this.getFoundMachine().createContext(aRecipe, this, this.foundComponents);
      RecipeCraftingContext.CraftingCheckResult result = context.canStartCrafting();
      if (!result.isFailure()) {
        this.activeRecipe = aRecipe;
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
      if (highestValidity != null) {
        setCraftingStatus(CraftingStatus.failure(
          Iterables.getFirst(highestValidityResult.getUnlocalizedErrorMessages(), "")));
      } else {
        setCraftingStatus(CraftingStatus.NO_RECIPE);
      }
    } else {
      setCraftingStatus(CraftingStatus.working());
    }
    setChanged();
  }

  public void set(CraftingStatus status, boolean recipe) {
    setCraftingStatus(status);
    if (recipe) activeRecipe = null;
    setRequestModelUpdate(true);
    setChanged();
  }

  public void setCraftingStatus(CraftingStatus status) {
    setRequestModelUpdate(true);
    setChanged();
    this.craftingStatus = status;
    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateCraftingStatusPacket(status, getBlockPos()));
  }

  public void setMachine(ResourceLocation machine) {
    setRequestModelUpdate(true);
    setChanged();
    this.id = machine;
    distributeCasingColor(false, getBlockPos());
    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SMachineUpdatePacket(id, getBlockPos()));
  }

  private void checkStructure() {
    if (ticksExisted % MMRConfig.get().general.checkStructureTicks == 0) {
      if (this.getFoundMachine() != null && this.getFoundMachine() != DynamicMachine.DUMMY) {
        if (!getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
          distributeCasingColor(true);
          this.activeRecipe = null;
          setCraftingStatus(CraftingStatus.MISSING_STRUCTURE);
        } else {
          distributeCasingColor(false);
          setCraftingStatus(CraftingStatus.NO_RECIPE);
        }
        setRequestModelUpdate(true);
        setChanged();
      }
    }
  }

  public void distributeCasingColor(boolean default_, BlockPos... poss) {
    int color = default_ ? Config.machineColor : getFoundMachine().getMachineColor();
    for (BlockPos pos : poss) {
      tryColorize(this.getBlockPos().offset(pos), color);
    }
    tryColorize(getBlockPos(), getFoundMachine().getMachineColor());
  }

  public void distributeCasingColor(boolean default_) {
    if (ModularMachineryReborn.MACHINES.get(id) != DynamicMachine.DUMMY) {
      distributeCasingColor(default_, getFoundMachine().getPattern().getBlocks(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet().toArray(BlockPos[]::new));
    } else {
      if (getBlueprintMachine() != null && !getBlueprintMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
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
    if (getFoundMachine() == null) return;
    if (ticksExisted % 20 == 0) {
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
  }

  public float getCurrentActiveRecipeProgress() {
    if (activeRecipe == null) return 0F;
    float maxTick = activeRecipe.getRecipe().getRecipeTotalTickTime();
    return Mth.clamp(recipeTicks / maxTick, 0F, 1F);
  }

  public boolean hasActiveRecipe() {
    return this.activeRecipe != null;
  }

  @Nullable
  public DynamicMachine getFoundMachine() {
    return ModularMachineryReborn.MACHINES.get(id);
  }

  @Nullable
  public DynamicMachine getBlueprintMachine() {
    return getFoundMachine();
  }

  @Override
  public void readCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.readCustomNBT(compound, pRegistries);

    this.inventory = IOInventory.deserialize(this, compound.getCompound("items"), pRegistries);
    this.inventory.setStackLimit(1, BLUEPRINT_SLOT);

    this.craftingStatus = CraftingStatus.deserialize(compound.getCompound("status"));

    this.id = ResourceLocation.parse(compound.getString("machine"));

    if (compound.contains("tick", Tag.TAG_INT))
      this.recipeTicks = compound.getInt("tick");

    if (compound.contains("recipe")) {
      CompoundTag tag = compound.getCompound("recipe");
      ActiveMachineRecipe recipe = new ActiveMachineRecipe(tag, this);
      if (recipe.getRecipe() == null) {
        MMRLogger.INSTANCE.info("Couldn't find recipe named {} for controller at {}", tag.getString("recipeId"), getBlockPos());
        this.activeRecipe = null;
      } else {
        this.activeRecipe = recipe;
      }
    } else {
      this.activeRecipe = null;
    }
  }

  @Override
  public void writeCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.writeCustomNBT(compound, pRegistries);
    compound.put("items", this.inventory.writeNBT(pRegistries));
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

  public static class CraftingStatus {

    public static final CraftingStatus SUCCESS = new CraftingStatus(Type.CRAFTING, "");
    public static final CraftingStatus MISSING_STRUCTURE = new CraftingStatus(Type.MISSING_STRUCTURE, "");
    public static final CraftingStatus NO_RECIPE = new CraftingStatus(Type.NO_RECIPE, "");

    @Getter
    private final Type status;
    private final String unlocMessage;

    private CraftingStatus(Type status, String unlocMessage) {
      this.status = status;
      this.unlocMessage = unlocMessage;
    }

    public String getUnlocMessage() {
      return !unlocMessage.isEmpty() ? unlocMessage : this.status.getUnlocalizedDescription();
    }

    public boolean isCrafting() {
      return this.status == Type.CRAFTING;
    }

    public static CraftingStatus working() {
      return SUCCESS;
    }

    public static CraftingStatus failure(String unlocMessage) {
      return new CraftingStatus(Type.NO_RECIPE, unlocMessage);
    }

    public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putString("type", this.status.getSerializedName());
      tag.putString("message", this.unlocMessage);
      return tag;
    }

    public static CraftingStatus deserialize(CompoundTag tag) {
      Type type = Type.fromString(tag.getString("type"));
      String unlocMessage = tag.getString("message");
      return new CraftingStatus(type, unlocMessage);
    }

    public static CraftingStatus of(Type type, String message) {
      return new CraftingStatus(type, message);
    }

    public static CraftingStatus of(String type, String message) {
      return new CraftingStatus(Type.fromString(type), message);
    }
  }

  public enum Type implements StringRepresentable {
    MISSING_STRUCTURE,
    NO_RECIPE,
    CRAFTING;

    public static Type fromString(String value) {
      return switch (value.toLowerCase(Locale.ROOT)) {
        case "missing_structure" -> MISSING_STRUCTURE;
        case "crafting" -> CRAFTING;
        default -> NO_RECIPE;
      };
    }

    public String getUnlocalizedDescription() {
      return "gui.controller.status." + getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
}
