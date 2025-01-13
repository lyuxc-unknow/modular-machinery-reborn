package es.degrassi.mmreborn.common.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeDecorator;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.integration.almostunified.RecipeIndicator;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.almostunified.AlmostUnifiedAdapter;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@EmiEntrypoint
public class MMREmiPlugin implements EmiPlugin {
  @Override
  public void register(EmiRegistry registry) {
    EmiStack controller = EmiStack.of(ItemRegistration.CONTROLLER);

    registry.setDefaultComparison(controller, Comparison.compareData(stack -> stack.get(Registration.MACHINE_DATA.get())));

    registry.addEmiStack(controller);

    RecipeManager manager = registry.getRecipeManager();
    List<RecipeHolder<MachineRecipe>> recipes = manager.getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get());

    ModularMachineryReborn.MACHINES.forEach((id, machine) -> {
      ItemStack is = new ItemStack(ItemRegistration.CONTROLLER.get());
      is.set(Registration.MACHINE_DATA.get(), id);
      EmiStack stack = EmiStack.of(is);
      EmiRecipeCategory category = new EmiRecipeCategory(id, stack) {
        @Override
        public Component getName() {
          return Component.literal(machine.getLocalizedName());
        }
      };
      registry.addCategory(category);
      registry.addWorkstation(category, EmiStack.of(ItemRegistration.BLUEPRINT.get()));
      registry.addWorkstation(category, stack);
      recipes.stream()
          .filter(recipe -> recipe.value().getOwningMachine() != null)
          .filter(recipe -> recipe.value().getOwningMachine().getRegistryName().equals(id))
          .forEach(recipe -> registry.addRecipe(new MMREmiRecipe(category, recipe)));
      registry.addRecipeDecorator(category, new IndicatorDecorator());
    });

    registry.removeEmiStacks(stack -> {
      ResourceLocation machineId = stack.getItemStack().getComponents().get(Registration.MACHINE_DATA.get());
      return stack.isEqual(controller) && (machineId == null || machineId.toString().equals(ControllerItem.DUMMY.toString()));
    });
  }

  /**
   * This decorator is adapted from AlmostUnified  <a href="https://github.com/AlmostReliable/almostunified/blob/1.21.1/Common/src/main/java/com/almostreliable/unified/compat/viewer/AlmostEMI.java">AlmostEMI$IndicatorDecorator</a>
   */
  private static class IndicatorDecorator implements EmiRecipeDecorator {

    @Override
    public void decorateRecipe(EmiRecipe recipe, WidgetHolder widgets) {
      var recipeId = recipe.getId();
      if (recipeId == null) return;

      if (recipe instanceof MMREmiRecipe r) {
        int pX = recipe.getDisplayWidth() - 5;
        int pY = recipe.getDisplayHeight() - 3;
        int size = RecipeIndicator.RENDER_SIZE - 1;
        var link = r.getRecipe();
        if (!AlmostUnifiedAdapter.isRecipeModified(link)) return;

        widgets.addDrawable(0, 0, 0, 0, (guiGraphics, mX, mY, delta) ->
            RecipeIndicator.renderIndicator(guiGraphics, pX, pY, size));
        widgets.addTooltipText(RecipeIndicator.constructTooltip(link), pX, pY, size, size);
      }
    }
  }
}
