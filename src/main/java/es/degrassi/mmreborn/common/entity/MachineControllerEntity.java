package es.degrassi.mmreborn.common.entity;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.controller.ComponentMapper;
import es.degrassi.mmreborn.client.model.ControllerBakedModel;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.ComponentManager;
import es.degrassi.mmreborn.common.manager.CraftingManager;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateRecipePacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineControllerEntity extends BlockEntityRestrictedTick implements ComponentMapper {
  private CraftingStatus craftingStatus = CraftingStatus.MISSING_STRUCTURE;
  private boolean isPaused = false;

  private ResourceLocation id = DynamicMachine.DUMMY.getRegistryName();
  @Nullable
  private ActiveMachineRecipe activeRecipe = null;

  private int recipeTicks = -1;
  private int ticksToUpdateComponent = 0;

  private final List<MachineComponent<?>> foundComponents = Lists.newArrayList();
  private final CraftingManager craftingManager;
  private final ComponentManager componentManager;

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
    craftingManager = new CraftingManager(this);
    componentManager = new ComponentManager(this);
  }

  @Override
  public ModelData getModelData() {
    return ModelData.builder()
        .with(ControllerBakedModel.MACHINE, getFoundMachine())
        .build();
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void pause() {
    this.isPaused = RedstoneHelper.getReceivingRedstone(this) > 0;
    if (!getLevel().isClientSide) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
          new SSyncPauseStatePacket(isPaused, getBlockPos()));
    }
  }

  @Override
  public void doRestrictedTick() {
    pause();
    checkStructure();

    if (isPaused()) {
      craftingManager.pause();
      return;
    }
    craftingManager.resume();
    if (craftingStatus.isMissingStructure()) {
      craftingManager.reset();
      componentManager.reset();
      return;
    }
    componentManager.updateComponents();
    craftingManager.serverTick();
    setChanged();
  }

  public void setRecipeTicks(int recipeTicks) {
    setRequestModelUpdate(true);
    setChanged();
    this.recipeTicks = recipeTicks;
    if (getLevel() instanceof ServerLevel l && activeRecipe != null)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateRecipePacket(activeRecipe.getHolder().id(), recipeTicks, getBlockPos()));
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
    ticksToUpdateComponent++;
    if (level.getGameTime() % MMRConfig.get().checkStructureTicks.get() == 0) {
      if (this.getFoundMachine() != DynamicMachine.DUMMY) {
        if (!getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
          distributeCasingColor(true);
          this.activeRecipe = null;
          setRecipeTicks(-1);
          setCraftingStatus(CraftingStatus.MISSING_STRUCTURE);
        } else {
          distributeCasingColor(false);
        }
        setRequestModelUpdate(true);
        setChanged();
      }
    }
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

  public float getCurrentActiveRecipeProgress() {
    return craftingManager.getCurrentActiveRecipeProgress();
  }

  public boolean hasActiveRecipe() {
    return craftingManager.hasActiveRecipe();
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
    craftingManager.deserializeNBT(pRegistries, compound.getCompound("craftingManager"));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.put("status", this.craftingStatus.serializeNBT());
    compound.putString("machine", id.toString());
    compound.put("craftingManager", craftingManager.serializeNBT(pRegistries));
  }

  public void refreshClientData() {
    requestModelDataUpdate();
  }

  public List<MachineComponent<?>> getFoundComponents() {
    return componentManager.getFoundComponentsList();
  }

  @Override
  public Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    return componentManager.getFoundComponentsMap();
  }
}
