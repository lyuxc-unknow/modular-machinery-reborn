package es.degrassi.mmreborn.api;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Structure {
  public static final NamedCodec<Structure> CODEC = NamedCodec.record(structure -> structure.group(
      NamedCodec.STRING.listOf().listOf().fieldOf("pattern").forGetter(s -> s.pattern.asList()),
      NamedCodec.unboundedMap(DefaultCodecs.CHARACTER, BlockIngredient.CODEC, "Map<Character, Block>").fieldOf("keys").forGetter(s -> s.pattern.asMap())
  ).apply(structure, Structure::makeStructure), "Structure");

  public static final Structure EMPTY = new Structure(Map.of(), List.of(List.of("m")), Map.of());

  private static Structure makeStructure(List<List<String>> pattern, Map<Character, BlockIngredient> keys) {
    Structure.Builder builder = Structure.Builder.start();
    for (List<String> levels : pattern)
      builder.aisle(levels.toArray(new String[0]));
    for (Map.Entry<Character, BlockIngredient> key : keys.entrySet())
      builder.where(key.getKey(), key.getValue());
    return builder.build(pattern, keys);
  }

  public static void place(DynamicMachine machine, BlockPos controllerPos, Level level, boolean isCreative, ServerPlayer player, boolean withModifiers) {
    Structure structure = machine.getPattern();
    BlockState blockState = level.getBlockState(controllerPos);
    Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    Map<BlockPos, BlockIngredient> blocks = withModifiers ? structure.getBlocks(facing) : structure.getBlocksFiltered(facing);
    BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
    blockSearch:
    for (BlockPos pos : blocks.keySet()) {
      BlockIngredient ingredient = blocks.get(pos);
      if (
          ingredient.equals(BlockIngredient.AIR) ||
          ingredient.equals(BlockIngredient.ANY)
      ) {
        continue;
      } else if (ingredient.getAll().stream().anyMatch(state ->
          state.equals(PartialBlockState.AIR) ||
              state.equals(PartialBlockState.ANY) ||
              state.getBlockState().isAir()
      )) {
        ingredient = new BlockIngredient(ingredient.getTags(), ingredient.uniqueStates().filter(state ->
            !state.equals(PartialBlockState.AIR) &&
                !state.equals(PartialBlockState.ANY) &&
                !state.getBlockState().isAir()
        ).toList());
      }
      if (ingredient.getAll().isEmpty()) continue;
      worldPos.set(pos.getX() + controllerPos.getX(), pos.getY() + controllerPos.getY(), pos.getZ() + controllerPos.getZ());
      BlockInWorld info = new BlockInWorld(level, worldPos, false);
      if (!info.getState().isAir() && ingredient.getAll().stream().noneMatch(state -> state.test(info))) {
        if (isCreative) level.destroyBlock(worldPos, false);
        player.sendSystemMessage(
            Component.translatable(
                "mmr.place.non_air",
                info.getState().getBlock().getName(),
                "X:" + worldPos.getX() + " Y:" + worldPos.getY() + " Z:" + worldPos.getZ()
            )
        );
      }
      if (!isCreative) {
        boolean placed = false;
        if (!info.getState().isAir()) continue;
        for (PartialBlockState state : ingredient.getAll()) {
          if (state.equals(PartialBlockState.AIR) || state.equals(PartialBlockState.ANY)) continue blockSearch;
          ItemStack blockToRemove2 = new ItemStack(state.getBlockState().getBlock());
          if (player.getInventory().contains(blockToRemove2)) {
            int slot = player.getInventory().findSlotMatchingItem(blockToRemove2);
            player.getInventory().removeItem(slot, 1);
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.slotsChanged(player.getInventory());
            setBlock(level, worldPos, state);
            placed = true;
            break;
          }
        }
        if (!placed)
          player.sendSystemMessage(
              Component.translatable(
                  "mmr.place.no_item",
                  ingredient.getString(),
                  "X:" + worldPos.getX() + " Y:" + worldPos.getY() + " Z:" + worldPos.getZ(),
                  ingredient.getString()
              )
          );
        continue;
      }
      if (worldPos.equals(controllerPos)) continue;
      setBlock(level, worldPos, ingredient.getAll().get((int) (Math.random() * ingredient.getAll().size())));
    }
  }

  public static void breakStructure(DynamicMachine machine, BlockPos controllerPos, Level level, ServerPlayer player) {
    boolean isCreative = player.isCreative();
    Direction direction = level.getBlockState(controllerPos).getValue(BlockStateProperties.HORIZONTAL_FACING);
    Map<BlockPos, BlockIngredient> blocks = machine.getPattern().getBlocks(direction);
    BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
    for (BlockPos pos : blocks.keySet()) {
      BlockIngredient ingredient = blocks.get(pos);
      worldPos.set(pos.getX() + controllerPos.getX(), pos.getY() + controllerPos.getY(),
          pos.getZ() + controllerPos.getZ());
      BlockInWorld info = new BlockInWorld(level, worldPos, false);
      if (info.getState().isAir()) continue;
      if (info.getEntity() instanceof MachineControllerEntity) continue;
      if (ingredient.getAll().stream().noneMatch(state -> state.test(info))) continue;
      level.destroyBlock(worldPos.immutable(), !isCreative);
    }
  }

  private static void setBlock(Level world, BlockPos pos, PartialBlockState state) {
    world.setBlockAndUpdate(pos, state.getBlockState());
    BlockEntity tile = world.getBlockEntity(pos);
    if (tile != null && state.getNbt() != null && !state.getNbt().isEmpty()) {
      CompoundTag nbt = state.getNbt().copy();
      nbt.putInt("x", pos.getX());
      nbt.putInt("y", pos.getY());
      nbt.putInt("z", pos.getZ());
      tile.loadWithComponents(nbt, world.registryAccess());
    }
  }

  private final Pattern pattern;

  public Structure(Map<BlockPos, BlockIngredient> blocks, List<List<String>> pattern, Map<Character, BlockIngredient> keys) {
    this.pattern = new Pattern(blocks, pattern, keys);
  }

  public Map<BlockPos, BlockIngredient> getBlocks(Direction direction) {
    return pattern.get(direction);
  }

  public Map<BlockPos, BlockIngredient> getBlocksFiltered(Direction direction) {
    return pattern.getFiltered(direction);
  }

  public boolean match(LevelReader world, BlockPos machinePos, Direction machineFacing) {
    return pattern.match(world, machinePos, machineFacing);
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.add("pattern", pattern.asJson());
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public static class Builder {

    private static final Joiner COMMA_JOIN = Joiner.on(",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, BlockIngredient> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;

    private Builder() {
      this.symbolMap.put(' ', BlockIngredient.ANY);
      this.symbolMap.put('m', BlockIngredient.MACHINE);
    }

    /**
     * Adds a single aisle to this pattern, going in the y-axis. (so multiple calls to this will increase the y-size by
     * 1)
     */
    public Builder aisle(String... aisle) {
      if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
        if (this.depth.isEmpty()) {
          this.aisleHeight = aisle.length;
          this.rowWidth = aisle[0].length();
        }

        if (aisle.length != this.aisleHeight) {
          throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
        } else {
          for (String s : aisle) {
            if (s.length() != this.rowWidth) {
              throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s.length() + ")");
            }

            for (char c0 : s.toCharArray()) {
              if (!this.symbolMap.containsKey(c0)) {
                this.symbolMap.put(c0, null);
              }
            }
          }

          this.depth.add(aisle);
          return this;
        }
      } else {
        throw new IllegalArgumentException("Empty pattern for aisle");
      }
    }

    public static Builder start() {
      return new Builder();
    }

    public Builder where(char symbol, BlockIngredient blockMatcher) {
      this.symbolMap.put(symbol, blockMatcher);
      return this;
    }

    public Structure build(List<List<String>> pattern, Map<Character, BlockIngredient> keys) {
      this.checkMissingPredicates();
      BlockPos machinePos = this.getMachinePos();
      Map<BlockPos, BlockIngredient> blocks = new HashMap<>();
      for (int i = 0; i < this.depth.size(); ++i) {
        for (int j = 0; j < this.aisleHeight; ++j) {
          for (int k = 0; k < this.rowWidth; ++k) {
            blocks.put(new BlockPos(k - machinePos.getX(), i - machinePos.getY(), j - machinePos.getZ()), this.symbolMap.get((this.depth.get(i))[j].charAt(k)));
          }
        }
      }
      return new Structure(blocks, pattern, keys);
    }

    private BlockPos getMachinePos() {
      BlockPos machinePos = null;
      for (int i = 0; i < this.depth.size(); ++i) {
        for (int j = 0; j < this.aisleHeight; ++j) {
          for (int k = 0; k < this.rowWidth; ++k) {
            if ((this.depth.get(i))[j].charAt(k) == 'm')
              if (machinePos == null)
                machinePos = new BlockPos(k, i, j);
              else
                throw new IllegalStateException("The structure pattern need exactly one 'm' character to defined the machine position, several found !");
          }
        }
      }
      if (machinePos != null)
        return machinePos;
      throw new IllegalStateException("You need to define the machine position in the structure with character 'm'");
    }

    private void checkMissingPredicates() {
      List<Character> list = Lists.newArrayList();

      for (Map.Entry<Character, BlockIngredient> entry : this.symbolMap.entrySet()) {
        if (entry.getValue() == null) {
          list.add(entry.getKey());
        }
      }

      if (!list.isEmpty()) {
        throw new IllegalStateException("Blocks for character(s) " + COMMA_JOIN.join(list) + " are missing");
      }
    }

    public JsonObject asJson() {
      JsonObject json = new JsonObject();
      json.addProperty("aisleHeight", aisleHeight);
      json.addProperty("rowWidth", rowWidth);
      JsonArray depth = new JsonArray();
      this.depth.forEach(array -> {
        JsonArray newOne = new JsonArray();
        Arrays.asList(array).forEach(newOne::add);
        depth.add(newOne);
      });
      json.add("depth", depth);
      JsonObject symbols = new JsonObject();
      this.symbolMap.forEach((key, value) -> symbols.add(String.valueOf(key), value.asJson()));
      json.add("symbolMap", symbols);
      return json;
    }

    @Override
    public String toString() {
      return asJson().toString();
    }
  }
}
