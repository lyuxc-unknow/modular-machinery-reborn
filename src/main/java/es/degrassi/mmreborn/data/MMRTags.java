package es.degrassi.mmreborn.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MMRTags {
  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return BlockTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) : ModularMachineryReborn.rl(name));
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return ItemTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) : ModularMachineryReborn.rl(name));
  }

  private static class Tag<T> {
    private final TagKey<T> tag;
    protected Tag(TagKey<T> tag) {
      this.tag = tag;
    }

    public TagKey<T> get() {
      return tag;
    }
  }

  public static class Blocks extends Tag<Block> {
    public static final TagKey<Block> ENERGY_INPUT = new Blocks(false, "energyinputhatch").get();
    public static final TagKey<Block> INPUT_BUS = new Blocks(false, "inputbus").get();
    public static final TagKey<Block> FLUID_INPUT = new Blocks(false, "fluidinputhatch").get();
    public static final TagKey<Block> CHEMICAL_INPUT = new Blocks(false, "chemicalinputhatch").get();
    public static final TagKey<Block> ENERGY_OUTPUT = new Blocks(false, "energyoutputhatch").get();
    public static final TagKey<Block> FLUID_OUTPUT = new Blocks(false, "chemicaloutputhatch").get();
    public static final TagKey<Block> CHEMICAL_OUTPUT = new Blocks(false, "fluidoutputhatch").get();
    public static final TagKey<Block> OUTPUT_BUS = new Blocks(false, "outputbus").get();
    public static final TagKey<Block> CASINGS = new Blocks(false, "casing").get();
    public static final TagKey<Block> ALL_CASINGS = new Blocks(false, "all_casing").get();

    private Blocks(boolean isNeoForge, String name) {
      super(blockTag(name, isNeoForge));
    }
  }

  public static class Items extends Tag<Item> {
    private Items(boolean isNeoForge, String name) {
      super(itemTag(name, isNeoForge));
    }
  }
}
