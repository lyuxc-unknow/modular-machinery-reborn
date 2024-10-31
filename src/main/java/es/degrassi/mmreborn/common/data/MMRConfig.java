package es.degrassi.mmreborn.common.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.data.config.MMREnergyHatchConfig;
import es.degrassi.mmreborn.common.data.config.MMRFluidHatchConfig;
import es.degrassi.mmreborn.common.data.config.MMRGeneralConfig;
import es.degrassi.mmreborn.common.data.config.MMRItemBusConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = ModularMachineryReborn.MODID)
public class MMRConfig extends PartitioningSerializer.GlobalData {
  @ConfigEntry.Category("general")
  @ConfigEntry.Gui.TransitiveObject
  public MMRGeneralConfig general = new MMRGeneralConfig();

  @ConfigEntry.Category("energy_hatch")
  @ConfigEntry.Gui.TransitiveObject
  public MMREnergyHatchConfig energyHatch = new MMREnergyHatchConfig();

  @ConfigEntry.Category("fluid_hatch")
  @ConfigEntry.Gui.TransitiveObject
  public MMRFluidHatchConfig fluidHatch = new MMRFluidHatchConfig();

  @ConfigEntry.Category("item_bus")
  @ConfigEntry.Gui.TransitiveObject
  public MMRItemBusConfig itemBus = new MMRItemBusConfig();

  public static MMRConfig get() {
    return AutoConfig.getConfigHolder(MMRConfig.class).getConfig();
  }
}
