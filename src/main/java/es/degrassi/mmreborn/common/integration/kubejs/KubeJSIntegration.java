package es.degrassi.mmreborn.common.integration.kubejs;

import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import es.degrassi.mmreborn.common.machine.MachineLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;

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
}
