package es.degrassi.mmreborn.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.registration.BlockRegistration;

import java.util.concurrent.CompletableFuture;

import es.degrassi.mmreborn.common.util.Mods;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMRBlockTagProvider extends BlockTagsProvider {
  public MMRBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, ModularMachineryReborn.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    tag(MMRTags.Blocks.ENERGY_INPUT)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get()
        );
    tag(MMRTags.Blocks.ENERGY_OUTPUT)
        .add(
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get()
        );
    tag(MMRTags.Blocks.FLUID_INPUT)
        .add(
            BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get()
        );
    tag(MMRTags.Blocks.FLUID_OUTPUT)
        .add(
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get()
        );
    tag(MMRTags.Blocks.INPUT_BUS)
        .add(
            BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get()
        );
    tag(MMRTags.Blocks.OUTPUT_BUS)
        .add(
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get()
        );
    tag(MMRTags.Blocks.CASINGS)
        .add(
            BlockRegistration.CASING_PLAIN.get(),
            BlockRegistration.CASING_VENT.get(),
            BlockRegistration.CASING_FIREBOX.get(),
            BlockRegistration.CASING_GEARBOX.get(),
            BlockRegistration.CASING_REINFORCED.get(),
            BlockRegistration.CASING_CIRCUITRY.get()
        );
    tag(MMRTags.Blocks.ALL_CASINGS)
        .addTag(MMRTags.Blocks.CASINGS)
        .addTag(MMRTags.Blocks.INPUT_BUS)
        .addTag(MMRTags.Blocks.OUTPUT_BUS)
        .addTag(MMRTags.Blocks.ENERGY_INPUT)
        .addTag(MMRTags.Blocks.ENERGY_OUTPUT)
        .addTag(MMRTags.Blocks.FLUID_INPUT)
        .addTag(MMRTags.Blocks.FLUID_OUTPUT)
        .add(BlockRegistration.BIOME_READER.get())
        .add(BlockRegistration.DIMENSIONAL_DETECTOR.get())
        .add(BlockRegistration.WEATHER_SENSOR.get());

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.get());

    tag(BlockTags.NEEDS_STONE_TOOL)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.get());
  }
}
