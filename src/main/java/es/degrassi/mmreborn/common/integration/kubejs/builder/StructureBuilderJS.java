package es.degrassi.mmreborn.common.integration.kubejs.builder;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureBuilderJS {
  private final Structure.Builder builder = Structure.Builder.start();
  private List<List<String>> pattern;
  private Map<Character, BlockIngredient> keys;

  public static StructureBuilderJS create() {
    return new StructureBuilderJS();
  }

  public StructureBuilderJS pattern(List<List<String>> pattern) {
    this.pattern = pattern;
    return this;
  }

  public StructureBuilderJS keys(Map<Character, JsonElement> keys) {
    this.keys = new HashMap<>();
    keys.forEach((character, s) -> this.keys.put(character, BlockIngredient.CODEC.read(JsonOps.INSTANCE, s).getOrThrow()));
    return this;
  }

  public Structure build() {
    for (List<String> levels : pattern)
      builder.aisle(levels.toArray(new String[0]));
    for (Map.Entry<Character, BlockIngredient> key : keys.entrySet())
      builder.where(key.getKey(), key.getValue());
    return builder.build(pattern, keys);
  }
}
