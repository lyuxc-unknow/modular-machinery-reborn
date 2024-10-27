package es.degrassi.mmreborn.common.integration.kubejs;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import es.degrassi.mmreborn.common.integration.kubejs.builder.MachineBuilderJS;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KubeJSIntegration {
  public static MachineLocation getMachineLocation(Resource resource, String packName, ResourceLocation id) {
    try(PackResources pack = resource.source()) {
      if(pack instanceof KubeFileResourcePack)
        return MachineLocation.fromKubeJS(id, packName);
      else if(pack instanceof VirtualDataPack)
        return MachineLocation.fromKubeJSScript(id, packName);
      return MachineLocation.fromDefault(id, packName);
    }
  }

  public static Map<ResourceLocation, DynamicMachine> collectMachines() {
    ScriptType.SERVER.console.info("Collecting Modular Machinery Reborn machines from JS scripts.");

    MachineBuilderJS.MachineKubeEvent event = new MachineBuilderJS.MachineKubeEvent();
    MMRKubeJSPlugin.MACHINES.post(event);

    Map<ResourceLocation, DynamicMachine> machines = new LinkedHashMap<>();
    try {
      event.getBuilders().forEach(builder -> {
        DynamicMachine machine = builder.build();
        machines.put(machine.getRegistryName(), machine);
      });
    } catch (Exception e) {
      ScriptType.SERVER.console.warn("Couldn't build machine upgrade", e);
    }
    ScriptType.SERVER.console.infof("Successfully added %s Modular Machines ", event.getBuilders().size());
    return machines;
  }
}
