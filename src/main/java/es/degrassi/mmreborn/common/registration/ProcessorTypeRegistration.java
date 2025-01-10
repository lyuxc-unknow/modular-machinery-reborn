package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.api.crafting.IProcessor;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessor;
import es.degrassi.mmreborn.common.manager.crafting.ProcessorType;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ProcessorTypeRegistration {
  public static final DeferredRegister<ProcessorType<? extends IProcessor>> MACHINE_PROCESSORS =
      DeferredRegister.create(ProcessorType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Registry<ProcessorType<? extends IProcessor>> PROCESSOR_REGISTRY =
      MACHINE_PROCESSORS.makeRegistry(builder -> {});

  public static final NamedCodec<ProcessorType<?>> PROCESSOR = RegistrarCodec.of(PROCESSOR_REGISTRY, true);
  public static final Supplier<ProcessorType<MachineProcessor>> MACHINE_PROCESSOR = MACHINE_PROCESSORS.register("machine", () -> ProcessorType.create(MachineProcessor.Template.CODEC));


  public static void register(final IEventBus bus) {
    MACHINE_PROCESSORS.register(bus);
  }
}
