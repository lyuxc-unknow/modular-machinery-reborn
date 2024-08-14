package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineLocation;
import es.degrassi.mmreborn.common.util.CustomJsonReloadListener;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

public class RecipeJsonReloadListener extends CustomJsonReloadListener {

  private static final String MAIN_PACKNAME = "main";
  public static IContext context;

  public RecipeJsonReloadListener() {
    super("recipes");
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
//    MMRLogger.INSTANCE.info("Reading Modular Machinery Reborn Recipe...");
//
//    ModularMachineryReborn.RECIPES.clear();
//
//    context = this.getContext();
//
//    map.forEach((id, json) -> {
//      MachineLocation location = getMachineLocation(resourceManager, id);
//
//      //Check if the content of the json file is a json object
//      if (!json.isJsonObject()) {
//        MMRLogger.INSTANCE.error("Bad recipe JSON: {} must be a json object and not an array or primitive, skipping...", id);
//        return;
//      }
//      JsonObject object = json.getAsJsonObject();
//
//      if (object.has("type") && object.get("type").getAsString().equalsIgnoreCase("modular_machinery_reborn:recipe")) {
//        MMRLogger.INSTANCE.info("Parsing recipe json: {} in datapack: {}", id, location.getPackName());
//        DataResult<MachineRecipe.MachineRecipeBuilder> result = MachineRecipe.CODEC.read(JsonOps.INSTANCE, object);
//
//        if (result.result().isPresent()) {
//          MachineRecipe recipe = result.result().get().build(id);
//          DynamicMachine machine = recipe.getOwningMachine();
//          if (machine == null) return;
//          Map<ResourceLocation, MachineRecipe> r = ModularMachineryReborn.RECIPES.get(machine) != null ? ModularMachineryReborn.RECIPES.get(machine) : new HashMap<>();
//          if (!r.isEmpty()) {
//            if (ModularMachineryReborn.RECIPES.get(machine).containsKey(id)) {
//              MMRLogger.INSTANCE.error("A recipe with id: {} already exists, skipping...", id);
//              return;
//            }
//          }
//          r.put(id, recipe);
//          ModularMachineryReborn.RECIPES.put(machine, r);
//          MMRLogger.INSTANCE.info("Successfully parsed recipe json: {}", id);
//        } else if (result.error().isPresent()) {
//          MMRLogger.INSTANCE.error("Error while parsing recipe json: {}, skipping...\n{}", id, result.error().get().message());
//        } else {
//          MMRLogger.INSTANCE.warn("whats happening with dataResult? {}", result);
//        }
//      }
//    });
//    context = null;
//
//    MMRLogger.INSTANCE.info("Finished creating {} modular recipe.", ModularMachineryReborn.RECIPES.values().stream().reduce(Maps.newHashMap(), (acc, m) -> {
//      m.keySet().forEach(k -> acc.put(k, m.get(k)));
//      return acc;
//    }).size());
  }

  private MachineLocation getMachineLocation(ResourceManager resourceManager, ResourceLocation id) {
    ResourceLocation path = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath() + ".json");
    try {
      Resource res = resourceManager.getResourceOrThrow(path);
      String packName = res.sourcePackId();
      if (packName.equals(MAIN_PACKNAME))
        return MachineLocation.fromDefault(id, packName);
//            else if(packName.contains("KubeJS") && ModList.get().isLoaded("kubejs"))
//                return KubeJSIntegration.getMachineLocation(res, packName, id);
      else {
        try (PackResources pack = res.source()) {
          if (pack instanceof FilePackResources)
            return MachineLocation.fromDatapackZip(id, packName);
          else if (pack instanceof PathPackResources)
            return MachineLocation.fromDatapack(id, packName);
        }
      }
    } catch (IOException ignored) {
    }
    return MachineLocation.fromDefault(id, MAIN_PACKNAME);
  }
}