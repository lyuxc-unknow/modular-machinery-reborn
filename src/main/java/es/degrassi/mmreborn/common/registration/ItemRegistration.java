package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockCasing.CasingType;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.item.BiomeReaderItem;
import es.degrassi.mmreborn.common.item.CasingItem;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.item.DimensionalDetectorItem;
import es.degrassi.mmreborn.common.item.EnergyHatchItem;
import es.degrassi.mmreborn.common.item.FluidHatchItem;
import es.degrassi.mmreborn.common.item.InputBusItem;
import es.degrassi.mmreborn.common.item.ItemBlueprint;
import es.degrassi.mmreborn.common.item.ItemModularium;
import es.degrassi.mmreborn.common.item.OutputBusItem;
import es.degrassi.mmreborn.common.item.StructureCreatorItem;
import es.degrassi.mmreborn.common.item.TimeCounterItem;
import es.degrassi.mmreborn.common.item.WeatherSensorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistration {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModularMachineryReborn.MODID);

  public static final DeferredItem<ItemBlueprint> BLUEPRINT = ITEMS.register("blueprint", ItemBlueprint::new);
  public static final DeferredItem<ItemModularium> MODULARIUM = ITEMS.register("modularium", ItemModularium::new);
  
  public static final DeferredItem<StructureCreatorItem> STRUCTURE_CREATOR_ITEM  = ITEMS.register("structure_creator", () -> new StructureCreatorItem(new Item.Properties().stacksTo(1)));

  public static final DeferredItem<CasingItem> CASING_PLAIN = ITEMS.register("casing_" + CasingType.PLAIN.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_PLAIN.get()));
  public static final DeferredItem<CasingItem> CASING_VENT = ITEMS.register("casing_" + CasingType.VENT.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_VENT.get()));
  public static final DeferredItem<CasingItem> CASING_FIREBOX = ITEMS.register("casing_" + CasingType.FIREBOX.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_FIREBOX.get()));
  public static final DeferredItem<CasingItem> CASING_GEARBOX = ITEMS.register("casing_" + CasingType.GEARBOX.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_GEARBOX.get()));
  public static final DeferredItem<CasingItem> CASING_REINFORCED = ITEMS.register("casing_" + CasingType.REINFORCED.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_REINFORCED.get()));
  public static final DeferredItem<CasingItem> CASING_CIRCUITRY = ITEMS.register("casing_" + CasingType.CIRCUITRY.getSerializedName(), () -> new CasingItem(BlockRegistration.CASING_CIRCUITRY.get()));

  public static final DeferredItem<BlockItem> CONTROLLER = ITEMS.register("controller", ControllerItem::new);

  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_TINY = ITEMS.register("energyinputhatch_" + EnergyHatchSize.TINY.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(), EnergyHatchSize.TINY));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_SMALL = ITEMS.register("energyinputhatch_" + EnergyHatchSize.SMALL.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(), EnergyHatchSize.SMALL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_NORMAL = ITEMS.register("energyinputhatch_" + EnergyHatchSize.NORMAL.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(), EnergyHatchSize.NORMAL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_REINFORCED = ITEMS.register("energyinputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(), EnergyHatchSize.REINFORCED));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_BIG = ITEMS.register("energyinputhatch_" + EnergyHatchSize.BIG.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(), EnergyHatchSize.BIG));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_HUGE = ITEMS.register("energyinputhatch_" + EnergyHatchSize.HUGE.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(), EnergyHatchSize.HUGE));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_LUDICROUS = ITEMS.register("energyinputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(), EnergyHatchSize.LUDICROUS));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_ULTIMATE = ITEMS.register("energyinputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(), EnergyHatchSize.ULTIMATE));

  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_TINY = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.TINY.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(), EnergyHatchSize.TINY));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_SMALL = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.SMALL.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(), EnergyHatchSize.SMALL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_NORMAL = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.NORMAL.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(), EnergyHatchSize.NORMAL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_REINFORCED = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(), EnergyHatchSize.REINFORCED));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_BIG = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.BIG.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(), EnergyHatchSize.BIG));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_HUGE = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.HUGE.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(), EnergyHatchSize.HUGE));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_LUDICROUS = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(), EnergyHatchSize.LUDICROUS));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_ULTIMATE = ITEMS.register("energyoutputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName(),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(), EnergyHatchSize.ULTIMATE));

  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_TINY = ITEMS.register("inputbus_" + ItemBusSize.TINY.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_TINY.get(), ItemBusSize.TINY));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_SMALL = ITEMS.register("inputbus_" + ItemBusSize.SMALL.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_SMALL.get(), ItemBusSize.SMALL));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_NORMAL = ITEMS.register("inputbus_" + ItemBusSize.NORMAL.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(), ItemBusSize.NORMAL));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_REINFORCED = ITEMS.register("inputbus_" + ItemBusSize.REINFORCED.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(), ItemBusSize.REINFORCED));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_BIG = ITEMS.register("inputbus_" + ItemBusSize.BIG.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_BIG.get(), ItemBusSize.BIG));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_HUGE = ITEMS.register("inputbus_" + ItemBusSize.HUGE.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_HUGE.get(), ItemBusSize.HUGE));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_LUDICROUS = ITEMS.register("inputbus_" + ItemBusSize.LUDICROUS.getSerializedName(),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get(), ItemBusSize.LUDICROUS));

  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_TINY = ITEMS.register("outputbus_" + ItemBusSize.TINY.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(), ItemBusSize.TINY));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_SMALL = ITEMS.register("outputbus_" + ItemBusSize.SMALL.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(), ItemBusSize.SMALL));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_NORMAL = ITEMS.register("outputbus_" + ItemBusSize.NORMAL.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(), ItemBusSize.NORMAL));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_REINFORCED = ITEMS.register("outputbus_" + ItemBusSize.REINFORCED.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(), ItemBusSize.REINFORCED));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_BIG = ITEMS.register("outputbus_" + ItemBusSize.BIG.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(), ItemBusSize.BIG));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_HUGE = ITEMS.register("outputbus_" + ItemBusSize.HUGE.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(), ItemBusSize.HUGE));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_LUDICROUS = ITEMS.register("outputbus_" + ItemBusSize.LUDICROUS.getSerializedName(),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(), ItemBusSize.LUDICROUS));

  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_TINY = ITEMS.register("fluidinputhatch_" + FluidHatchSize.TINY.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_TINY.get(), FluidHatchSize.TINY));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_SMALL = ITEMS.register("fluidinputhatch_" + FluidHatchSize.SMALL.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(), FluidHatchSize.SMALL));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_NORMAL = ITEMS.register("fluidinputhatch_" + FluidHatchSize.NORMAL.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(), FluidHatchSize.NORMAL));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_REINFORCED = ITEMS.register("fluidinputhatch_" + FluidHatchSize.REINFORCED.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(), FluidHatchSize.REINFORCED));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_BIG = ITEMS.register("fluidinputhatch_" + FluidHatchSize.BIG.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_BIG.get(), FluidHatchSize.BIG));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_HUGE = ITEMS.register("fluidinputhatch_" + FluidHatchSize.HUGE.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(), FluidHatchSize.HUGE));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_LUDICROUS = ITEMS.register("fluidinputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(), FluidHatchSize.LUDICROUS));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_VACUUM = ITEMS.register("fluidinputhatch_" + FluidHatchSize.VACUUM.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get(), FluidHatchSize.VACUUM));

  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_TINY = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.TINY.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(), FluidHatchSize.TINY));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_SMALL = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.SMALL.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(), FluidHatchSize.SMALL));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_NORMAL = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.NORMAL.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(), FluidHatchSize.NORMAL));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_REINFORCED = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.REINFORCED.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(), FluidHatchSize.REINFORCED));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_BIG = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.BIG.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(), FluidHatchSize.BIG));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_HUGE = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.HUGE.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(), FluidHatchSize.HUGE));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_LUDICROUS = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(), FluidHatchSize.LUDICROUS));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_VACUUM = ITEMS.register("fluidoutputhatch_" + FluidHatchSize.VACUUM.getSerializedName(),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(), FluidHatchSize.VACUUM));

  public static final DeferredItem<DimensionalDetectorItem> DIMENSIONAL_DETECTOR = ITEMS.register("dimensional_detector",
    () -> new DimensionalDetectorItem(BlockRegistration.DIMENSIONAL_DETECTOR.get()));
  public static final DeferredItem<BiomeReaderItem> BIOME_READER = ITEMS.register("biome_reader",
    () -> new BiomeReaderItem(BlockRegistration.BIOME_READER.get()));
  public static final DeferredItem<WeatherSensorItem> WEATHER_SENSOR = ITEMS.register("weather_sensor",
    () -> new WeatherSensorItem(BlockRegistration.WEATHER_SENSOR.get()));
  public static final DeferredItem<TimeCounterItem> TIME_COUNTER = ITEMS.register("time_counter",
    () -> new TimeCounterItem(BlockRegistration.TIME_COUNTER.get()));


  public static void register(final IEventBus bus) {
    ITEMS.register(bus);
  }
}
