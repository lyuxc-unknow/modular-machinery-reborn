package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.client.model.hatch.HatchBakedModel;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TextureableMachineEntity;
import es.degrassi.mmreborn.common.machine.MachineHatchType;
import es.degrassi.mmreborn.common.machine.component.ParallelComponent;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ParallelHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<ParallelComponent>, ControllerAccessible, TextureableMachineEntity {
  @Nullable
  private BlockPos controllerPos;
  protected ParallelHatchSize size;
  private ParallelComponent component;
  private ResourceLocation baseTexture;
  private ResourceLocation overlayTexture;
  private final ResourceLocation defaultOverlayTexture;
  private static final ResourceLocation defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  public ParallelHatchEntity(BlockPos pos, BlockState state, ParallelHatchSize size) {
    super(EntityRegistration.PARALLEL_HATCH.get(), pos, state);
    this.size = size;
    this.component = new ParallelComponent(size);
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_parallel_hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
  }

  public ParallelHatchEntity(BlockPos pos, BlockState state) {
    this(pos, state, ParallelHatchSize.BASIC);
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  public void setCores(int cores) {
    this.component.setCores(cores, getLevel(), getBlockPos());
    if (getController() != null)
      getController().getProcessor().updateActiveCores(cores);
  }

  public int getCores() {
    return this.component.getContainerProvider();
  }

  public int getMaxCores() {
    return size.max;
  }

  @Override
  public ParallelComponent provideComponent() {
    return component;
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);
    size = ParallelHatchSize.value(compound.getString("size"));
    this.component = new ParallelComponent(size);
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }
    if (compound.contains("cores")) {
      this.setCores(compound.getInt("cores"));
    }
    if (compound.contains("baseTexture")) {
      setMachineBaseTexture(ResourceLocation.parse(compound.getString("baseTexture")));
    }
    if (compound.contains("overlayTexture")) {
      setMachineOverlayTexture(ResourceLocation.parse(compound.getString("overlayTexture")));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.putString("size", size.getSerializedName());
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
    compound.putInt("cores", component.getContainerProvider());
    compound.putInt("maxCores", getMaxCores());
    if (baseTexture != null)
      compound.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      compound.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  public ModelData getModelData() {
    ModelData.Builder builder = getModelDataBuilder("orientable");
    builder.with(HatchBakedModel.BASE_TEXTURE, baseTexture)
        .with(HatchBakedModel.BASE_TEXTURE_NAME, "bg_all");
    builder.with(HatchBakedModel.OVERLAY_TEXTURE, overlayTexture)
        .with(HatchBakedModel.OVERLAY_TEXTURE_NAME, "ov_front");
    return builder.build();
  }

  @Override
  public ResourceLocation getMachineBaseTexture() {
    return baseTexture;
  }

  @Override
  public void setMachineBaseTexture(ResourceLocation newTexture) {
    setChanged();
    this.baseTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineTexturePacket(baseTexture, true, getBlockPos()));
    }
  }

  @Override
  public ResourceLocation getMachineOverlayTexture() {
    return overlayTexture;
  }

  @Override
  public void setMachineOverlayTexture(ResourceLocation newTexture) {
    setChanged();
    this.overlayTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineTexturePacket(overlayTexture, false, getBlockPos()));
    }
  }

  @Override
  public MachineHatchType getHatchType() {
    return MachineHatchType.BIOME_READER;
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }
}
