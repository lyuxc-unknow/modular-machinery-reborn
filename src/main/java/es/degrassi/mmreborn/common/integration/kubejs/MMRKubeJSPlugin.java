package es.degrassi.mmreborn.common.integration.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;

public class MMRKubeJSPlugin implements KubeJSPlugin {
  @Override
  public void registerRecipeSchemas(RecipeSchemaRegistry event) {
    event.register(RecipeRegistration.RECIPE_TYPE.getId(), ModularMachineryRebornRecipeSchemas.CUSTOM_MACHINE);
  }

  @Override
  public void clearCaches() {
    MachineRecipeBuilderJS.IDS.clear();
  }
}
