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

//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 100")
//  public int TINY_E = 100;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 400")
//  public int SMALL_E = 400;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 1000")
//  public int NORMAL_E = 1000;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete( max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 2000")
//  public int REINFORCED_E = 2000;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 4500")
//  public int BIG_E = 4500;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 8000")
//  public int HUGE_E = 8000;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 16000")
//  public int LUDICROUS_E = 16000;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Defines the tank size for the size-type of fluid hatch, default: 32000")
//  public int VACUUM_E = 32000;

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
