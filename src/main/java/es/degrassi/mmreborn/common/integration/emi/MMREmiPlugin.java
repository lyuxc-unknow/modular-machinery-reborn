package es.degrassi.mmreborn.common.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
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
      recipes.stream().filter(recipe -> recipe.value().getOwningMachine() != null).filter(recipe -> recipe.value().getOwningMachine().getRegistryName().equals(id)).forEach(recipe -> registry.addRecipe(new MMREmiRecipe(category, recipe)));
    });

    registry.removeEmiStacks(stack -> {
      ResourceLocation machineId = stack.getItemStack().getComponents().get(Registration.MACHINE_DATA.get());
      return stack.isEqual(controller) && (machineId == null || machineId.toString().equals(ControllerItem.DUMMY.toString()));
    });
  }
}
