package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.ChemicalInputHatchEntity;
import es.degrassi.mmreborn.common.entity.ChemicalOutputHatchEntity;
import es.degrassi.mmreborn.common.entity.FluidOutputHatchEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class EntityRegistration {
  public static final DeferredRegister<BlockEntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<BlockEntityType<ChemicalInputHatchEntity>> CHEMICAL_INPUT_HATCH = ENTITY_TYPE.register(
      "chemical_hatch_input",
      () -> new BlockEntityType<>(
          ChemicalInputHatchEntity::new,
          Set.of(
              BlockRegistration.CHEMICAL_INPUT_HATCH_TINY.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_SMALL.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_NORMAL.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_REINFORCED.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_BIG.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_HUGE.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.CHEMICAL_INPUT_HATCH_VACUUM.get()
          ),
          null)
  );
  public static final Supplier<BlockEntityType<ChemicalOutputHatchEntity>> CHEMICAL_OUTPUT_HATCH = ENTITY_TYPE.register(
      "chemical_hatch_output",
      () -> new BlockEntityType<>(
          ChemicalOutputHatchEntity::new,
          Set.of(
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_TINY.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_SMALL.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_NORMAL.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_REINFORCED.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_BIG.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_HUGE.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.CHEMICAL_OUTPUT_HATCH_VACUUM.get()
          ),
          null)
  );
  public static void register(final IEventBus bus) {
    ENTITY_TYPE.register(bus);
  }
}
