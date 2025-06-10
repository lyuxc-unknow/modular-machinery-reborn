package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockInputBus;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
public class InputBusItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final ItemBusSize type;

  public InputBusItem(BlockInputBus block, ItemBusSize type) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_inputbus_" + type.getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/inputbus_" + type.getSerializedName()))
    );
    this.type = type;
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_inputbus_" + type.getSerializedName());
  }
}
