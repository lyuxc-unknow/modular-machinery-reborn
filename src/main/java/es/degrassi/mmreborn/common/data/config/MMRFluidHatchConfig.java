package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MMRFluidHatchConfig {
  public Tier TINY;

  public Tier SMALL;

  public Tier NORMAL;

  public Tier REINFORCED;

  public Tier BIG;

  public Tier HUGE;

  public Tier LUDICROUS;

  public Tier VACUUM;

  public static class Tier {
    public final ConfigValue<Integer> size;
//    @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//    @Comment("Defines the tank size of fluid hatch in mB")
//    public int size;

    public Tier(ModConfigSpec.Builder builder, FluidHatchSize tier) {
      builder.push(tier.getSerializedName());
      this.size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .define("size", tier.defaultConfigurationValue);
      builder.pop();
    }
  }

  public MMRFluidHatchConfig(ModConfigSpec.Builder builder) {
    TINY = new Tier(builder, FluidHatchSize.TINY);
    SMALL = new Tier(builder, FluidHatchSize.SMALL);
    NORMAL = new Tier(builder, FluidHatchSize.NORMAL);
    REINFORCED = new Tier(builder, FluidHatchSize.REINFORCED);
    BIG = new Tier(builder, FluidHatchSize.BIG);
    HUGE = new Tier(builder, FluidHatchSize.HUGE);
    LUDICROUS = new Tier(builder, FluidHatchSize.LUDICROUS);
    VACUUM = new Tier(builder, FluidHatchSize.VACUUM);
  }

  public int fluidSize(FluidHatchSize size) {
    return switch(size) {
      case TINY -> TINY.size.get();
      case SMALL -> SMALL.size.get();
      case NORMAL -> NORMAL.size.get();
      case REINFORCED -> REINFORCED.size.get();
      case BIG -> BIG.size.get();
      case HUGE -> HUGE.size.get();
      case LUDICROUS -> LUDICROUS.size.get();
      case VACUUM -> VACUUM.size.get();
    };
  }
}
