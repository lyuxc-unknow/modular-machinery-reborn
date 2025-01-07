package es.degrassi.mmreborn.api.crafting;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.manager.crafting.ProcessorType;
import es.degrassi.mmreborn.common.registration.ProcessorTypeRegistration;

public interface IProcessorTemplate<T extends IProcessor> {
  NamedCodec<IProcessorTemplate<? extends IProcessor>> CODEC = ProcessorTypeRegistration.PROCESSOR.dispatch(
      IProcessorTemplate::getType,
      ProcessorType::getCodec,
      "Processor"
  );

  ProcessorType<T> getType();

  T build(MachineControllerEntity tile);
}
