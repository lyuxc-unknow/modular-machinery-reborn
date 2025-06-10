package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockEnergyHatch;
import es.degrassi.mmreborn.common.block.BlockEnergyInputHatch;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@Getter
public class EnergyHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private final EnergyHatchSize type;
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");

  public EnergyHatchItem(BlockEnergyHatch block, EnergyHatchSize type) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_energy" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/energy" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
    );
    this.type = type;
  }

  private static IOType fromBlock(Block block) {
    return block instanceof BlockEnergyInputHatch ? IOType.INPUT : IOType.OUTPUT;
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_energy" + fromBlock(this.getBlock()).getSerializedName() + "hatch_" + type.getSerializedName());
  }
}
