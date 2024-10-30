package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "fluid_hatch")
public class MMRFluidHatchConfig implements ConfigData {
  @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
  public Tier TINY = new Tier(FluidHatchSize.TINY);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier SMALL = new Tier(FluidHatchSize.SMALL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier NORMAL = new Tier(FluidHatchSize.NORMAL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier REINFORCED = new Tier(FluidHatchSize.REINFORCED);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier BIG = new Tier(FluidHatchSize.BIG);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier HUGE = new Tier(FluidHatchSize.HUGE);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier LUDICROUS = new Tier(FluidHatchSize.LUDICROUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier VACUUM = new Tier(FluidHatchSize.VACUUM);

  public static class Tier {
    @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
    @Comment("Defines the tank size of fluid hatch in mB")
    public int size;

    public Tier(FluidHatchSize tier) {
      this.size = tier.defaultConfigurationValue;
    }
  }

  public int fluidSize(FluidHatchSize size) {
    return switch(size) {
      case TINY -> TINY.size;
      case SMALL -> SMALL.size;
      case NORMAL -> NORMAL.size;
      case REINFORCED -> REINFORCED.size;
      case BIG -> BIG.size;
      case HUGE -> HUGE.size;
      case LUDICROUS -> LUDICROUS.size;
      case VACUUM -> VACUUM.size;
    };
  }
}
