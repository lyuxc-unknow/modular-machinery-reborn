package es.degrassi.mmreborn.common.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import es.degrassi.mmreborn.common.integration.kubejs.builder.MachineBuilderJS.MachineKubeEvent;
import es.degrassi.mmreborn.common.integration.kubejs.builder.ModifierBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.builder.StructureBuilderJS;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.Sounds;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import es.degrassi.mmreborn.common.util.MachineModelLocation;

public class MMRKubeJSPlugin implements KubeJSPlugin {
  public static final EventGroup MMR_EVENTS = EventGroup.of("MMREvents");
  public static final EventHandler MACHINES = MMR_EVENTS.server("machines", () -> MachineKubeEvent.class);

  @Override
  public void registerRecipeSchemas(RecipeSchemaRegistry event) {
    event.register(RecipeRegistration.RECIPE_TYPE.getId(), ModularMachineryRebornRecipeSchemas.CUSTOM_MACHINE);
  }

  @Override
  public void registerBindings(BindingRegistry registry) {
    registry.add("MMRStructureBuilder", StructureBuilderJS.class);
    registry.add("MMRModifierReplacement", ModifierBuilderJS.class);
    registry.add("MMRRecipeModifier", ModifierBuilderJS.RecipeModifierBuilderJS.class);
    registry.add("ControllerModel", MachineModelLocation.class);
  }

  @Override
  public void beforeScriptsLoaded(ScriptManager manager) {
    MachineRecipeBuilderJS.IDS.clear();
  }

  @Override
  public void registerEvents(EventGroupRegistry registry) {
    registry.register(MMR_EVENTS);
  }

  @Override
  public void registerTypeWrappers(TypeWrapperRegistry registry) {
    registry.register(IntRange.class, IntRange::of);
    registry.registerCodec(Sounds.class, Sounds.CODEC.codec());
  }
}
