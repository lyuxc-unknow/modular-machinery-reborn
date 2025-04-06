package es.degrassi.mmreborn.common.integration.jei.ingredient;

import es.degrassi.mmreborn.ModularMachineryReborn;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IntegerIngredientHelper implements IIngredientHelper<Integer> {

  @Override
  public IIngredientType<Integer> getIngredientType() {
    return CustomIngredientTypes.INTEGER;
  }

  @Override
  public String getDisplayName(Integer long_) {
    return Component.translatable("modular_machinery_reborn.jei.ingredient.int", long_).getString();
  }

  //Safe to remove
  @SuppressWarnings("removal")
  @Override
  public String getUniqueId(Integer long_, UidContext context) {
    return long_.toString();
  }

  @Override
  public Object getUid(Integer long_, UidContext context) {
    return long_.toString();
  }

  @Override
  public Integer copyIngredient(Integer long_) {
    return Integer.valueOf(long_.toString());
  }

  @Override
  public String getErrorInfo(@Nullable Integer long_) {
    return "";
  }

  @Override
  public ResourceLocation getResourceLocation(Integer ingredient) {
    return ModularMachineryReborn.rl("integer");
  }
}
