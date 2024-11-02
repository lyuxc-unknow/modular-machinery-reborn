package es.degrassi.mmreborn.common.integration.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientType;

public class CustomIngredientTypes {
  public static final IIngredientType<Long> ENERGY = () -> Long.class;
  public static final IIngredientType<Integer> SOURCE = () -> Integer.class;
}
