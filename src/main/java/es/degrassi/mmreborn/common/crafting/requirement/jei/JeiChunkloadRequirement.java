package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementChunkload;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;

import java.util.List;

public class JeiChunkloadRequirement extends JeiComponent<Integer, RequirementChunkload> {
  public JeiChunkloadRequirement(RequirementChunkload requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<Integer> ingredients() {
    return List.of(requirement.radius());
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .addItemStack(ItemRegistration.CHUNKLOADER.toStack())
        .addRichTooltipCallback((view, tooltip) -> {
          tooltip.clear();
          tooltip.add(Component.translatable(
              "modular_machinery_reborn.jei.ingredient.chunkload",
              requirement.radius()
          ));
        });
  }
}
