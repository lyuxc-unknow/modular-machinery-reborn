package es.degrassi.mmreborn.common.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineModelLocation {
  public static final MachineModelLocation DEFAULT = new MachineModelLocation(
      ModularMachineryReborn.rl("default/controller").toString(),
      null,
      null,
      ModularMachineryReborn.rl("default/controller"),
      null
  );

  @HideFromJS
  public static final NamedCodec<MachineModelLocation> CODEC = NamedCodec.STRING.comapFlatMap(s -> {
    try {
      return DataResult.success(MachineModelLocation.of(s));
    } catch (ResourceLocationException e) {
      return DataResult.error(e::getMessage);
    }
  }, MachineModelLocation::toString, "Model location");

  @HideFromJS
  private final String loc;
  @Nullable
  @HideFromJS
  private final BlockState state;
  @Nullable Item item;
  @Nullable
  @HideFromJS
  private final ResourceLocation id;
  @Nullable
  @HideFromJS
  private final String properties;

  public static MachineModelLocation of(String loc) {
    BlockState state = null;
    try {
      state = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(loc), false).blockState();
    } catch (CommandSyntaxException ignored) {
    }
    ResourceLocation id;
    String properties = null;
    if (loc.contains("#")) {
      id = ResourceLocation.parse(loc.substring(0, loc.indexOf("#")));
      properties = loc.substring(loc.indexOf("#") + 1);
    } else
      id = ResourceLocation.parse(loc);

    Item item = null;
    if (BuiltInRegistries.ITEM.containsKey(id))
      item = BuiltInRegistries.ITEM.get(id);

    return new MachineModelLocation(loc, state, item, id, properties);
  }

  @HideFromJS
  private MachineModelLocation(String loc, @Nullable BlockState state, @Nullable Item item, @Nullable ResourceLocation id, @Nullable String properties) {
    this.loc = loc;
    this.state = state;
    this.item = item;
    this.id = id;
    this.properties = properties;
  }

  @Nullable
  @HideFromJS
  public BlockState getState() {
    return this.state;
  }

  @Nullable
  @HideFromJS
  public Item getItem() {
    return this.item;
  }

  @Nullable
  @HideFromJS
  public ResourceLocation getLoc() {
    return this.id;
  }

  @Nullable
  @HideFromJS
  public String getProperties() {
    return this.properties;
  }

  @Override
  @HideFromJS
  public String toString() {
    return this.loc;
  }
}
