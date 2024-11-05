package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

public interface IJeiRequirement<C, T extends ComponentRequirement<C, T>> {
  NamedCodec<JeiPositionedRequirement> POSITION_CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("x").forGetter(JeiPositionedRequirement::x),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("y").forGetter(JeiPositionedRequirement::y)
  ).apply(instance, JeiPositionedRequirement::new), "Jei Position");

  void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses);

  T getRequirement();

  default JeiPositionedRequirement getPosition() {
    return getRequirement().getPosition();
  }

  record JeiPositionedRequirement(int x, int y) {}
}
