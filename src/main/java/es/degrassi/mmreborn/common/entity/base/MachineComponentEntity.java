package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.model.hatch.HatchBakedModel;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nullable;

public interface MachineComponentEntity<T extends MachineComponent<?>> {
  @Nullable
  T provideComponent();

  default ModelData.Builder getModelDataBuilder(String mode) {
    return ModelData.builder()
        .with(HatchBakedModel.MODEL, ModularMachineryReborn.rl("default/hatch_" + mode));
  }
}
