package es.degrassi.mmreborn.common.integration.jei.ingredient;

import es.degrassi.mmreborn.ModularMachineryReborn;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class LongIngredientHelper implements IIngredientHelper<Long> {

  @Override
  public IIngredientType<Long> getIngredientType() {
    return CustomIngredientTypes.LONG;
  }

  @Override
  public String getDisplayName(Long long_) {
    return Component.translatable("modular_machinery_reborn.jei.ingredient.long", long_).getString();
  }

  //Safe to remove
  @SuppressWarnings("removal")
  @Override
  public String getUniqueId(Long long_, UidContext context) {
    return long_.toString();
  }

  @Override
  public Object getUid(Long long_, UidContext context) {
    return long_.toString();
  }

  @Override
  public Long copyIngredient(Long long_) {
    return Long.valueOf(long_.toString());
  }

  @Override
  public String getErrorInfo(@Nullable Long long_) {
    return "";
  }

  @Override
  public ResourceLocation getResourceLocation(Long ingredient) {
    return ModularMachineryReborn.rl("long");
  }
}
