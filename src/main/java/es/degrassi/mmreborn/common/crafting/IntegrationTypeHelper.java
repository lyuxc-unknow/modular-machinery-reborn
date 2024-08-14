package es.degrassi.mmreborn.common.crafting;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;

public class IntegrationTypeHelper {

  public static void filterModIdComponents() {
    List<DeferredHolder<ComponentType, ?>> removableTypes = new ArrayList<>();
    for (DeferredHolder<ComponentType, ?> type : ComponentRegistration.MACHINE_COMPONENTS.getEntries()) {
      String modid = type.get().requiresModid();
      if (modid != null && !ModList.get().isLoaded(modid)) {
        MMRLogger.INSTANCE.info("[Modular Machinery Reborn] Removing componentType {} because {} is not loaded!", type, modid);
        removableTypes.add(type);
      }
    }
    removableTypes.forEach(type -> ComponentRegistration.MACHINE_COMPONENTS.getEntries().remove(type));
  }

  public static void filterModIdRequirementTypes() {
    List<DeferredHolder<RequirementType<?>, ?>> removableTypes = new ArrayList<>();
    for (DeferredHolder<RequirementType<?>, ?> type : RequirementTypeRegistration.MACHINE_REQUIREMENTS.getEntries()) {
      String modid = type.get().requiresModid();
      if (modid != null && !ModList.get().isLoaded(modid)) {
        MMRLogger.INSTANCE.info("[Modular Machinery Reborn] Removing requirementType {} because {} is not loaded!", type, modid);
        removableTypes.add(type);
      }
    }
    removableTypes.forEach(type -> RequirementTypeRegistration.MACHINE_REQUIREMENTS.getEntries().remove(type));
  }

  public static RequirementType<?> searchRequirementType(String name) {
    for (RequirementType<?> type : RequirementTypeRegistration.MACHINE_REQUIREMENTS.getEntries().stream().map(DeferredHolder::get).toList()) {
      if (RequirementTypeRegistration.MACHINE_REQUIREMENTS.getEntries().stream().anyMatch(entry -> entry.get().requirementName().equalsIgnoreCase(name)))
        return type;
    }
    return null;
  }

}
