package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "energy_hatch")
public class MMREnergyHatchConfig implements ConfigData {
  @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
  public Tier TINY = new Tier(EnergyHatchSize.TINY);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier SMALL = new Tier(EnergyHatchSize.SMALL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier NORMAL = new Tier(EnergyHatchSize.NORMAL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier REINFORCED = new Tier(EnergyHatchSize.REINFORCED);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier BIG = new Tier(EnergyHatchSize.BIG);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier HUGE = new Tier(EnergyHatchSize.HUGE);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier LUDICROUS = new Tier(EnergyHatchSize.LUDICROUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ULTIMATE = new Tier(EnergyHatchSize.ULTIMATE);

  @ConfigEntry.Category("Display")
  @Comment("Set to true, if the standard 'energy' FE (or RF) should be displayed in the tooltip of the energy hatch along with its transmission rates.")
  public boolean displayFETooltip = true;

  @ConfigEntry.Category("Display")
  @Comment("Set to true, if IC2's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if IC2 is installed.")
  public boolean displayIC2EUTooltip = true;

//  @ConfigEntry.Category("Display")
//  @Comment("Set to true, if GT's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if GregTech (community edition) is installed.")
//  public boolean displayGTEUTooltip = true;

  @ConfigEntry.Category("Display")
  @Comment("Available options: 'FE', 'IC2_EU' - Default: FE - Set this to one of those 3 types to have GUI, recipe preview and energy be displayed in that type of energy in ALL ModularMachinery Reborn things.")
  public EnergyDisplayUtil.EnergyType type = EnergyDisplayUtil.EnergyType.FE;

  public static class Tier {
    @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
    @Comment("Energy storage size of the energy hatch. \n[range: 0 ~ 9223372036854775807]")
    public long size;

    @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
    @Comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier. \n[range: 1 ~ 9223372036854775806")
    public long transferRate;

    @ConfigEntry.BoundedDiscrete(max = 12)
    @Comment("Defines the IC2 output-voltage tier. \nOnly affects the power the output hatches will output power as. \n0 = 'ULV' = 8 EU/t, 1 = 'LV' = 32 EU/t, 2 = 'MV' = 128 EU/t, ... \n[range: 0 ~ 12")
    public int ic2_tier;

    public Tier(EnergyHatchSize tier) {
      size = tier.defaultConfigurationEnergy;
      transferRate = tier.defaultConfigurationTransferLimit;
      ic2_tier = tier.defaultIC2EnergyTier;
    }
  }

  public long energySize(EnergyHatchSize size) {
    return switch(size) {
      case TINY -> TINY.size;
      case SMALL -> SMALL.size;
      case NORMAL -> NORMAL.size;
      case REINFORCED -> REINFORCED.size;
      case BIG -> BIG.size;
      case HUGE -> HUGE.size;
      case LUDICROUS -> LUDICROUS.size;
      case ULTIMATE -> ULTIMATE.size;
    };
  }

  public long energyLimit(EnergyHatchSize size) {
    return switch(size) {
      case TINY -> TINY.transferRate;
      case SMALL -> SMALL.transferRate;
      case NORMAL -> NORMAL.transferRate;
      case REINFORCED -> REINFORCED.transferRate;
      case BIG -> BIG.transferRate;
      case HUGE -> HUGE.transferRate;
      case LUDICROUS -> LUDICROUS.transferRate;
      case ULTIMATE -> ULTIMATE.transferRate;
    };
  }

  public int energyTier(EnergyHatchSize size) {
    return switch(size) {
      case TINY -> TINY.ic2_tier;
      case SMALL -> SMALL.ic2_tier;
      case NORMAL -> NORMAL.ic2_tier;
      case REINFORCED -> REINFORCED.ic2_tier;
      case BIG -> BIG.ic2_tier;
      case HUGE -> HUGE.ic2_tier;
      case LUDICROUS -> LUDICROUS.ic2_tier;
      case ULTIMATE -> ULTIMATE.ic2_tier;
    };
  }
}
