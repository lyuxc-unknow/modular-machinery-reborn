package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockBiomeReader;
import es.degrassi.mmreborn.common.block.BlockCasing;
import es.degrassi.mmreborn.common.block.BlockCasing.CasingType;
import es.degrassi.mmreborn.common.block.BlockChunkloader;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.block.BlockDimensionDetector;
import es.degrassi.mmreborn.common.block.BlockEnergyHatch;
import es.degrassi.mmreborn.common.block.BlockEnergyInputHatch;
import es.degrassi.mmreborn.common.block.BlockEnergyOutputHatch;
import es.degrassi.mmreborn.common.block.BlockFluidHatch;
import es.degrassi.mmreborn.common.block.BlockFluidInputHatch;
import es.degrassi.mmreborn.common.block.BlockFluidOutputHatch;
import es.degrassi.mmreborn.common.block.BlockInputBus;
import es.degrassi.mmreborn.common.block.BlockOutputBus;
import es.degrassi.mmreborn.common.block.BlockTimeCounter;
import es.degrassi.mmreborn.common.block.BlockWeatherSensor;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistration {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModularMachineryReborn.MODID);

  public static final DeferredBlock<BlockCasing> CASING_PLAIN = BLOCKS.register("casing_" + CasingType.PLAIN.getSerializedName(), BlockCasing::new);
  public static final DeferredBlock<BlockCasing> CASING_VENT = BLOCKS.register("casing_" + CasingType.VENT.getSerializedName(), BlockCasing::new);
  public static final DeferredBlock<BlockCasing> CASING_FIREBOX = BLOCKS.register("casing_" + CasingType.FIREBOX.getSerializedName(), BlockCasing::new);
  public static final DeferredBlock<BlockCasing> CASING_GEARBOX = BLOCKS.register("casing_" + CasingType.GEARBOX.getSerializedName(), BlockCasing::new);
  public static final DeferredBlock<BlockCasing> CASING_REINFORCED = BLOCKS.register("casing_" + CasingType.REINFORCED.getSerializedName(), BlockCasing::new);
  public static final DeferredBlock<BlockCasing> CASING_CIRCUITRY = BLOCKS.register("casing_" + CasingType.CIRCUITRY.getSerializedName(), BlockCasing::new);

  public static final DeferredBlock<BlockController> CONTROLLER = BLOCKS.register("controller", BlockController::new);

  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_TINY = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.TINY.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.TINY));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_SMALL = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.SMALL.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.SMALL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_NORMAL = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.NORMAL.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.NORMAL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_REINFORCED = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.REINFORCED));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_BIG = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.BIG.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.BIG));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_HUGE = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.HUGE.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.HUGE));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_LUDICROUS = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_ULTIMATE = BLOCKS.register("energyinputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName(),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.ULTIMATE));

  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_TINY = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.TINY.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.TINY));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_SMALL = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.SMALL.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.SMALL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_NORMAL = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.NORMAL.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.NORMAL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_REINFORCED = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.REINFORCED));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_BIG = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.BIG.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.BIG));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_HUGE = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.HUGE.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.HUGE));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_LUDICROUS = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_ULTIMATE = BLOCKS.register("energyoutputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName(),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.ULTIMATE));

  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_TINY = BLOCKS.register("inputbus_" + ItemBusSize.TINY.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.TINY));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_SMALL = BLOCKS.register("inputbus_" + ItemBusSize.SMALL.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.SMALL));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_NORMAL = BLOCKS.register("inputbus_" + ItemBusSize.NORMAL.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.NORMAL));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_REINFORCED = BLOCKS.register("inputbus_" + ItemBusSize.REINFORCED.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.REINFORCED));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_BIG = BLOCKS.register("inputbus_" + ItemBusSize.BIG.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.BIG));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_HUGE = BLOCKS.register("inputbus_" + ItemBusSize.HUGE.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.HUGE));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_LUDICROUS = BLOCKS.register("inputbus_" + ItemBusSize.LUDICROUS.getSerializedName(),
      () -> new BlockInputBus(ItemBusSize.LUDICROUS));

  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_TINY = BLOCKS.register("outputbus_" + ItemBusSize.TINY.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.TINY));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_SMALL = BLOCKS.register("outputbus_" + ItemBusSize.SMALL.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.SMALL));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_NORMAL = BLOCKS.register("outputbus_" + ItemBusSize.NORMAL.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.NORMAL));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_REINFORCED = BLOCKS.register("outputbus_" + ItemBusSize.REINFORCED.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.REINFORCED));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_BIG = BLOCKS.register("outputbus_" + ItemBusSize.BIG.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.BIG));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_HUGE = BLOCKS.register("outputbus_" + ItemBusSize.HUGE.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.HUGE));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_LUDICROUS = BLOCKS.register("outputbus_" + ItemBusSize.LUDICROUS.getSerializedName(),
      () -> new BlockOutputBus(ItemBusSize.LUDICROUS));

  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_TINY = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.TINY.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.TINY));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_SMALL = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.SMALL.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.SMALL));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_NORMAL = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.NORMAL.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.NORMAL));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_REINFORCED = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.REINFORCED.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.REINFORCED));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_BIG = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.BIG.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.BIG));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_HUGE = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.HUGE.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.HUGE));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_LUDICROUS = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_VACUUM = BLOCKS.register("fluidinputhatch_" + FluidHatchSize.VACUUM.getSerializedName(),
      () -> new BlockFluidInputHatch(FluidHatchSize.VACUUM));

  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_TINY = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.TINY.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.TINY));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_SMALL = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.SMALL.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.SMALL));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_NORMAL = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.NORMAL.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.NORMAL));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_REINFORCED = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.REINFORCED.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.REINFORCED));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_BIG = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.BIG.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.BIG));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_HUGE = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.HUGE.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.HUGE));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_LUDICROUS = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockFluidHatch> FLUID_OUTPUT_HATCH_VACUUM = BLOCKS.register("fluidoutputhatch_" + FluidHatchSize.VACUUM.getSerializedName(),
      () -> new BlockFluidOutputHatch(FluidHatchSize.VACUUM));

  public static final DeferredBlock<BlockDimensionDetector> DIMENSIONAL_DETECTOR = BLOCKS.register("dimensional_detector", BlockDimensionDetector::new);
  public static final DeferredBlock<BlockBiomeReader> BIOME_READER = BLOCKS.register("biome_reader", BlockBiomeReader::new);
  public static final DeferredBlock<BlockWeatherSensor> WEATHER_SENSOR = BLOCKS.register("weather_sensor", BlockWeatherSensor::new);
  public static final DeferredBlock<BlockTimeCounter> TIME_COUNTER = BLOCKS.register("time_counter", BlockTimeCounter::new);
  public static final DeferredBlock<BlockChunkloader> CHUNKLOADER = BLOCKS.register("chunkloader", BlockChunkloader::new);

  public static void register(final IEventBus bus) {
    BLOCKS.register(bus);
  }
}
