package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.controller.ComponentMapper;
import es.degrassi.mmreborn.api.crafting.ComponentNotFoundException;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.NbtSyncable;
import es.degrassi.mmreborn.api.network.syncable.StringSyncable;
import es.degrassi.mmreborn.client.model.ControllerBakedModel;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.Sounds;
import es.degrassi.mmreborn.common.manager.ComponentManager;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessor;
import es.degrassi.mmreborn.common.manager.crafting.MachineStatus;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import es.degrassi.mmreborn.common.util.SoundManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineControllerEntity extends BlockEntityRestrictedTick implements ComponentMapper, ISyncableStuff {
  @Setter
  private CraftingStatus craftingStatus = CraftingStatus.MISSING_STRUCTURE;
  private boolean isPaused = false;

  private ResourceLocation id = DynamicMachine.DUMMY.getRegistryName();

  private MachineStatus status = MachineStatus.IDLE;
  private Component errorMessage = Component.empty();

  private final ComponentManager componentManager;

  private final MachineProcessor processor;

  private int lastFocus;

  private SoundManager soundManager;

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
    componentManager = new ComponentManager(this);

    processor = new MachineProcessor(this);
  }

  public void setStatus(MachineStatus status, Component message) {
    if (this.status != status) {
      this.componentManager.getFoundComponentsList().forEach(component -> component.onStatusChanged(this.status, status, message));
      this.status = status;
      this.errorMessage = message;
      setCraftingStatus(craftingByMachine(status));
      setRequestModelUpdate(true);
      setChanged();
      if (this.getLevel() instanceof ServerLevel level) {
        BlockPos pos = this.getBlockPos();
        level.updateNeighborsAt(pos, this.getBlockState().getBlock());
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), new SUpdateCraftingStatusPacket(this.status, pos));
      }
    }
  }

  private CraftingStatus craftingByMachine(MachineStatus status) {
    return switch (status) {
      case IDLE -> CraftingStatus.NO_RECIPE;
      case PAUSED -> craftingStatus;
      case ERRORED -> CraftingStatus.failure(errorMessage);
      case RUNNING -> CraftingStatus.working();
      case MISSING_STRUCTURE -> CraftingStatus.MISSING_STRUCTURE;
    };
  }

  public void setStatus(MachineStatus status) {
    this.setStatus(status, Component.empty());
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
    setPaused(RedstoneHelper.getReceivingRedstone(this) > 0);
  }

  public void setPaused(boolean paused) {
    this.isPaused = paused;
    if (paused)
      setStatus(MachineStatus.PAUSED);
    if (!getLevel().isClientSide) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
          new SSyncPauseStatePacket(isPaused, getBlockPos()));
    }
  }

  @Override
  public void doClientTick() {
    if(soundManager == null)
      soundManager = new SoundManager(getBlockPos());
    if(!getFoundMachine().getAmbientSound(status).getLocation().equals(soundManager.getSoundID())) {
      if(getFoundMachine().getAmbientSound(status) == Sounds.DEFAULT.ambientSound())
        soundManager.setSound(null);
      else
        soundManager.setSound(getFoundMachine().getAmbientSound(status));
    }

    if (!soundManager.isPlaying())
      soundManager.play();
  }

  @Override
  public void doRestrictedTick() {
    pause();
    checkStructure();

    if (status.isMissingStructure()) {
      processor.reset();
      componentManager.reset();
      return;
    }

    componentManager.updateComponents();

    if (isPaused()) return;

    try {
      processor.tick();
    } catch (ComponentNotFoundException e) {
      ModularMachineryReborn.LOGGER.error(e.getMessage());
    }
  }

  @Override
  public void setRemoved() {
    if(this.level != null && this.level.isClientSide() && this.soundManager != null)
      this.soundManager.stop();
    super.setRemoved();
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
    if (level.getGameTime() % MMRConfig.get().checkStructureTicks.get() == 0) {
      if (this.getFoundMachine() != DynamicMachine.DUMMY) {
        if (!getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
          distributeCasingColor(true);
          setStatus(MachineStatus.MISSING_STRUCTURE);
        } else {
          distributeCasingColor(false);
          componentManager.updateComponents();
          if (!status.isCrafting()) {
            setStatus(MachineStatus.IDLE);
          } else {
            setStatus(status);
          }
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
    if (te instanceof ColorableMachineEntity entity) {
      entity.setMachineColor(color);
      te.setChanged();
    }
  }

  public float getCurrentActiveRecipeProgress() {
    return processor.core().getCurrentActiveRecipeProgress();
  }

  public boolean hasActiveRecipe() {
    return status == MachineStatus.RUNNING;
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
    this.craftingStatus = CraftingStatus.deserialize(compound.getCompound("status"), pRegistries);
    this.id = ResourceLocation.parse(compound.getString("machine"));
    processor.deserialize(compound.getCompound("craftingManager"));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.put("status", this.craftingStatus.serializeNBT(pRegistries));
    compound.putString("machine", id.toString());
    compound.put("craftingManager", processor.serialize());
    compound.put("componentManager", componentManager.serializeNBT(pRegistries));
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

  public List<ModifierReplacement> getFoundModifiers() {
    return componentManager.getFoundModifiersList();
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    if (this.getLevel() == null)
      return;
    if (this.processor instanceof ISyncableStuff syncableProcessor)
      syncableProcessor.getStuffToSync(container);
    RegistryAccess registries = this.getLevel().registryAccess();
    container.accept(IntegerSyncable.create(() -> lastFocus, i -> lastFocus = i));
    container.accept(NbtSyncable.create(() -> craftingStatus.serializeNBT(getLevel().registryAccess()), s -> craftingStatus = CraftingStatus.deserialize(s, getLevel().registryAccess())));
    container.accept(StringSyncable.create(() -> this.status.toString(), status -> this.status = MachineStatus.value(status)));
    container.accept(StringSyncable.create(() -> Component.Serializer.toJson(this.errorMessage, registries), errorMessage -> this.errorMessage = Component.Serializer.fromJson(errorMessage, registries)));
  }

  public SoundType getInteractionSound() {
    return getFoundMachine().getInteractionSound(status);
  }
}
