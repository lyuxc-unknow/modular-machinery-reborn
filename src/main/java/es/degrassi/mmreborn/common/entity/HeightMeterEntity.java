package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.client.model.hatch.HatchBakedModel;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TextureableMachineEntity;
import es.degrassi.mmreborn.common.machine.MachineHatchType;
import es.degrassi.mmreborn.common.machine.component.HeightComponent;
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

@Getter
@Setter
@MethodsReturnNonnullByDefault
public class HeightMeterEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<HeightComponent>, ControllerAccessible, TextureableMachineEntity {
  @Nullable
  private BlockPos controllerPos;

  private ResourceLocation baseTexture;
  private ResourceLocation overlayTexture;
  @Getter
  private static final ResourceLocation defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_height_meter");
  @Getter
  private static final ResourceLocation defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  public HeightMeterEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.HEIGHT_METER.get(), pos, blockState);
    this.overlayTexture = defaultOverlayTexture;
  }

  @Override
  public HeightComponent provideComponent() {
    return new HeightComponent();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    if (controllerPos != null)
      nbt.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      nbt.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      nbt.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    if (nbt.contains("controllerPos")) {
      controllerPos = BlockPos.of(nbt.getLong("controllerPos"));
    }
    if (nbt.contains("baseTexture")) {
      setBaseTexture(ResourceLocation.parse(nbt.getString("baseTexture")));
    }
    if (nbt.contains("overlayTexture")) {
      setOverlayTexture(ResourceLocation.parse(nbt.getString("overlayTexture")));
    }
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
  public ResourceLocation getMachineOverlayTexture() {
    return overlayTexture;
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

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public MachineHatchType getHatchType() {
    return MachineHatchType.HEIGHT_METER;
  }
}
