package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ItemHatch {
  ResourceLocation getDefaultBaseTexture();
  ResourceLocation getDefaultOverlayTexture();

  static @Nullable ResourceLocation getBaseTexture(ItemStack stack) {
    if (!stack.has(DataComponentRegistration.BASE_TEXTURE)) return null;
    return stack.get(DataComponentRegistration.BASE_TEXTURE);
  }

  static @Nullable ResourceLocation getOverlayTexture(ItemStack stack) {
    if (!stack.has(DataComponentRegistration.OVERLAY_TEXTURE)) return null;
    return stack.get(DataComponentRegistration.OVERLAY_TEXTURE);
  }

  static void resetToDefault(ItemStack stack) {
    resetBaseTexture(stack);
    resetOverlayTexture(stack);
  }

  static void resetBaseTexture(ItemStack stack) {
    if (stack.getItem() instanceof ItemHatch item)
      stack.set(DataComponentRegistration.BASE_TEXTURE, item.getDefaultBaseTexture());
  }

  static void resetOverlayTexture(ItemStack stack) {
    if (stack.getItem() instanceof ItemHatch item)
      stack.set(DataComponentRegistration.OVERLAY_TEXTURE, item.getDefaultOverlayTexture());
  }

  static void setBaseTexture(ItemStack stack, ResourceLocation texture) {
    stack.set(DataComponentRegistration.BASE_TEXTURE, texture);
  }

  static void setOverlayTexture(ItemStack stack, ResourceLocation texture) {
    stack.set(DataComponentRegistration.OVERLAY_TEXTURE, texture);
  }
}
