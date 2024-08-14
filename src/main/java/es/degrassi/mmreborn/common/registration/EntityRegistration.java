package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockDynamicColor;
import es.degrassi.mmreborn.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmreborn.common.entity.EnergyOutputHatchEntity;
import es.degrassi.mmreborn.common.entity.FluidInputHatchEntity;
import es.degrassi.mmreborn.common.entity.FluidOutputHatchEntity;
import es.degrassi.mmreborn.common.entity.ItemInputBusEntity;
import es.degrassi.mmreborn.common.entity.ItemOutputBusEntity;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistration {
  public static final DeferredRegister<BlockEntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<BlockEntityType<ColorableMachineComponentEntity>> COLORABLE_MACHINE = ENTITY_TYPE.register(
    "colorable_entity",
    () -> new BlockEntityType<>(
      ColorableMachineComponentEntity::new,
      Set.of(
        BlockRegistration.BLOCKS.getEntries().stream().filter(holder -> holder.get() instanceof BlockDynamicColor).map(DeferredHolder::get).toArray(Block[]::new)
      ),
      null)
  );
  public static final Supplier<BlockEntityType<MachineControllerEntity>> CONTROLLER = ENTITY_TYPE.register(
    "controller",
    () -> new BlockEntityType<>(
      MachineControllerEntity::new,
      Set.of(
        BlockRegistration.CONTROLLER.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_INPUT_HATCH = ENTITY_TYPE.register(
    "energy_hatch_input",
    () -> new BlockEntityType<>(
      EnergyInputHatchEntity::new,
      Set.of(
        BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_OUTPUT_HATCH = ENTITY_TYPE.register(
    "energy_hatch_output",
    () -> new BlockEntityType<>(
      EnergyOutputHatchEntity::new,
      Set.of(
        BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<ItemInputBusEntity>> ITEM_INPUT_BUS = ENTITY_TYPE.register(
    "item_input_bus",
    () -> new BlockEntityType<>(
      ItemInputBusEntity::new,
      Set.of(
        BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
        BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
        BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
        BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
        BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
        BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
        BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<ItemOutputBusEntity>> ITEM_OUTPUT_BUS = ENTITY_TYPE.register(
    "item_output_bus",
    () -> new BlockEntityType<>(
      ItemOutputBusEntity::new,
      Set.of(
        BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<FluidInputHatchEntity>> FLUID_INPUT_HATCH = ENTITY_TYPE.register(
    "fluid_hatch_input",
    () -> new BlockEntityType<>(
      FluidInputHatchEntity::new,
      Set.of(
        BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
        BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
        BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
        BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
        BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
        BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
        BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get()
      ),
      null)
  );
  public static final Supplier<BlockEntityType<FluidOutputHatchEntity>> FLUID_OUTPUT_HATCH = ENTITY_TYPE.register(
    "fluid_hatch_output",
    () -> new BlockEntityType<>(
      FluidOutputHatchEntity::new,
      Set.of(
        BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get()
      ),
      null)
  );

  public static void register(final IEventBus bus) {
    ENTITY_TYPE.register(bus);
  }
}
