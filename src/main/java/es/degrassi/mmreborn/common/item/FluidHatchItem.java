package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockExperienceHatch;
import es.degrassi.mmreborn.common.block.BlockExperienceInputHatch;
import es.degrassi.mmreborn.common.block.BlockFluidHatch;
import es.degrassi.mmreborn.common.block.BlockFluidInputHatch;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@Getter
public class FluidHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final FluidHatchSize type;

  public FluidHatchItem(BlockFluidHatch block, FluidHatchSize type) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_fluid" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/fluid" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
    );
    this.type = type;
  }

  private static IOType fromBlock(Block block) {
    return block instanceof BlockFluidInputHatch ? IOType.INPUT : IOType.OUTPUT;
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_fluid" + fromBlock(getBlock()).getSerializedName() + "hatch_" + type.getSerializedName());
  }
}
