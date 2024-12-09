package es.degrassi.mmreborn.common.integration.jei;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.integration.jei.ingredient.DummyIngredientRenderer;
import es.degrassi.mmreborn.common.integration.jei.ingredient.EnergyIngredientHelper;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@JeiPlugin
public class MMRJeiPlugin implements IModPlugin {
  public static final ResourceLocation PLUGIN_ID = ModularMachineryReborn.rl("jei_plugin");
  private static final Map<DynamicMachine, MMRRecipeCategory> recipeCategories = new HashMap<>();
  public static IJeiHelpers jeiHelpers;

  public static String getCategoryStringFor(DynamicMachine machine) {
    return "modularmachineryreborn.recipes." + machine.getRegistryName().getPath();
  }

  public static MMRRecipeCategory getCategory(DynamicMachine machine) {
    return recipeCategories.get(machine);
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(CustomIngredientTypes.ENERGY, new ArrayList<>(), new EnergyIngredientHelper(), new DummyIngredientRenderer<>(), NamedCodec.LONG.codec());
  }

  @Override
  @SuppressWarnings("removal")
  public void registerItemSubtypes(ISubtypeRegistration registration) {
    registration.registerSubtypeInterpreter(ItemRegistration.CONTROLLER.get(), (stack, context) -> {
      AtomicReference<String> toReturn = new AtomicReference<>(null);
      ControllerItem.getMachine(stack).ifPresent(machine -> {
        toReturn.set(machine.getRegistryName().toString());
      });
      return toReturn.get();
    });
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    if (jeiHelpers == null) jeiHelpers = registration.getJeiHelpers();
    ModularMachineryReborn.MACHINES.values().forEach(machine -> {
      MMRRecipeCategory recipe = new MMRRecipeCategory(machine);
      recipeCategories.put(machine, recipe);
      registration.addRecipeCategories(recipe);
    });
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    if (jeiHelpers == null) jeiHelpers = registration.getJeiHelpers();
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine == null || machine == DynamicMachine.DUMMY) continue;
      ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
      stack.set(Registration.MACHINE_DATA, machine.getRegistryName());
      registration.addRecipeCatalyst(stack, getCategory(machine).getRecipeType());
      registration.addRecipeCatalyst(ItemRegistration.BLUEPRINT.get(), getCategory(machine).getRecipeType());
    }
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine == null) continue;
      registration.addRecipes(getCategory(machine).getRecipeType(),
          Optional.ofNullable(Minecraft.getInstance().level)
              .map(ClientLevel::getRecipeManager)
              .map(r -> r.getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get()))
              .map(list -> list
                  .stream()
                  .map(RecipeHolder::value)
                  .map(MachineRecipe::copy)
                  .filter(recipe -> Objects.requireNonNull(recipe.getOwningMachine()).getRegistryName().equals(machine.getRegistryName()))
                  .toList()
              )
              .orElse(List.of())
      );
    }
  }

  @Override
  public @NotNull ResourceLocation getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    jeiHelpers = jeiRuntime.getJeiHelpers();
  }

  public static void reloadMachines(Map<ResourceLocation, DynamicMachine> machines) {
    machines.forEach((id, machine) -> {
      MMRRecipeCategory category = recipeCategories.get(machine);
      if(category != null)
        category.updateMachine(machine);
    });
  }
}
