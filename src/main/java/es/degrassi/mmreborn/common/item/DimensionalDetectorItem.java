package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockDimensionDetector;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import net.minecraft.resources.ResourceLocation;

public class DimensionalDetectorItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private static final ResourceLocation OVERLAY_TEXTURE = ModularMachineryReborn.rl("block/overlay_dimensional_detector");

  public DimensionalDetectorItem(BlockDimensionDetector block) {
    super(block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, OVERLAY_TEXTURE)
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/dimensional_detector"))
    );
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return OVERLAY_TEXTURE;
  }
}
