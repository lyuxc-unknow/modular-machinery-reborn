package es.degrassi.mmreborn.common.integration.jade;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MMRWailaPlugin implements IWailaPlugin {

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(DynamicMachineComponentProvider.INSTANCE, BlockController.class);
    registration.usePickedResult(BlockRegistration.CONTROLLER.get());
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(registration::usePickedResult);
  }

  @Override
  public void register(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(DynamicMachineServerDataProvider.INSTANCE, MachineControllerEntity.class);
  }
}
