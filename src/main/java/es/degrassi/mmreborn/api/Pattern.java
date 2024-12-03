package es.degrassi.mmreborn.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class Pattern {
  private final List<List<String>> strings;
  private final Map<Character, BlockIngredient> keys;

  private final Map<BlockPos, BlockIngredient> pattern;
  private final Map<BlockPos, BlockIngredient> pattern_north;
  private final Map<BlockPos, BlockIngredient> pattern_south;
  private final Map<BlockPos, BlockIngredient> pattern_east;
  private final Map<BlockPos, BlockIngredient> pattern_west;

  public Pattern(Map<BlockPos, BlockIngredient> pattern, List<List<String>> strings, Map<Character, BlockIngredient> keys) {
    this.pattern = pattern;
    this.strings = strings;
    this.keys = keys;
    this.pattern_north = pattern;
    this.pattern_south = rotate(Rotation.CLOCKWISE_180);
    this.pattern_west = rotate(Rotation.COUNTERCLOCKWISE_90);
    this.pattern_east = rotate(Rotation.CLOCKWISE_90);
  }

  public Map<BlockPos, BlockIngredient> get(Direction direction) {
    return switch (direction) {
      case WEST -> pattern_west;
      case EAST -> pattern_east;
      case SOUTH -> pattern_south;
      default -> pattern_north;
    };
  }

  private Map<BlockPos, BlockIngredient> rotate(Rotation rotation) {
    Map<BlockPos, BlockIngredient> rotated = new HashMap<>();
    pattern.forEach((pos, ingredient) -> rotated.put(pos.rotate(rotation), ingredient.copyWithRotation(rotation)));
//    pattern.forEach((pos, ingredient) -> rotated.put(pos.rotate(rotation),
//        new BlockIngredient(ingredient.getAll().stream().map(ing -> ing.rotate(rotation)).toList())));
    return rotated;
  }

  public List<List<String>> asList() {
    return strings;
  }

  public Map<Character, BlockIngredient> asMap() {
    return keys;
  }

  public boolean match(LevelReader world, BlockPos machinePos, Direction machineFacing) {
    Map<BlockPos, BlockIngredient> blocks = get(machineFacing);
    BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
    for (BlockPos pos : blocks.keySet()) {
      BlockIngredient ingredient = blocks.get(pos);
      worldPos.set(pos.getX() + machinePos.getX(), pos.getY() + machinePos.getY(), pos.getZ() + machinePos.getZ());
      BlockInWorld info = new BlockInWorld(world, worldPos, false);
      if (ingredient.getAll().stream().noneMatch(state -> state.test(info)))
        return false;
    }
    return true;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    JsonArray keysList = new JsonArray();
    strings.forEach(list -> {
      JsonArray keyKeyList = new JsonArray();
      list.forEach(keyKeyList::add);
      keysList.add(keyKeyList);
    });
    json.add("strings", keysList);
    JsonObject keys = new JsonObject();
    this.keys.forEach((key, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      keys.add(key + "", states);
    });
    json.add("keys", keys);
    JsonObject pattern = new JsonObject();
    this.pattern.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern.add(pos.toString(), states);
    });
    json.add("pattern", pattern);
    JsonObject pattern_north = new JsonObject();
    this.pattern_north.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_north.add(pos.toString(), states);
    });
    json.add("pattern_north", pattern_north);
    JsonObject pattern_south = new JsonObject();
    this.pattern_south.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_south.add(pos.toString(), states);
    });
    json.add("pattern_south", pattern_south);
    JsonObject pattern_east = new JsonObject();
    this.pattern_east.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_east.add(pos.toString(), states);
    });
    json.add("pattern_east", pattern_east);
    JsonObject pattern_west = new JsonObject();
    this.pattern_west.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_west.add(pos.toString(), states);
    });
    json.add("pattern_west", pattern_west);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
