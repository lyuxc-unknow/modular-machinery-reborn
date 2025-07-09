package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockDurabilityHatch;
import es.degrassi.mmreborn.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
public class DurabilityHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final ItemDurabilityHatchSize type;

  public DurabilityHatchItem(BlockDurabilityHatch block, ItemDurabilityHatchSize type) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_durabilityhatch_" + type.getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/durabilityhatch_" + type.getSerializedName()))
    );
    this.type = type;
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_durabilityhatch_" + type.getSerializedName());
  }
}
