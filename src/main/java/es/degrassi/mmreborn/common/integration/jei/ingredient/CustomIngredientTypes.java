package es.degrassi.mmreborn.common.integration.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientType;

public class CustomIngredientTypes {
  public static final IIngredientType<Long> LONG = () -> Long.class;
  // TODO: change on ars addon
  public static final IIngredientType<Integer> INTEGER = () -> Integer.class;
}
