package es.degrassi.mmreborn.client.model;

import es.degrassi.mmreborn.common.item.ControllerItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ControllerOverrideList extends ItemOverrides {

  @Override
  public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
    if (!(model instanceof ControllerBakedModel machineModel))
      return super.resolve(model, stack, level, entity, seed);
    return ControllerItem.getMachine(stack).map(machineModel::getMachineItemModel).orElse(model);
  }
}
