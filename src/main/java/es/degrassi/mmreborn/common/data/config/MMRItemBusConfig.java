package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MMRItemBusConfig {
  public Tier TINY;

  public Tier SMALL;

  public Tier NORMAL;

  public Tier REINFORCED ;

  public Tier BIG;

  public Tier HUGE;

  public Tier LUDICROUS;

  public static class Tier {
    public final ConfigValue<Integer> size;
//    @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//    @Comment("Slot number of the item bus")
//    public int size;

    public Tier(ModConfigSpec.Builder builder, ItemBusSize tier) {
      builder.push(tier.getSerializedName());
      size = builder
          .comment("Slot number of the item bus")
          .define("size", tier.defaultConfigSize);
      builder.pop();
    }
  }

  public MMRItemBusConfig(ModConfigSpec.Builder builder) {
    TINY = new Tier(builder, ItemBusSize.TINY);
    SMALL = new Tier(builder, ItemBusSize.SMALL);
    NORMAL = new Tier(builder, ItemBusSize.NORMAL);
    REINFORCED = new Tier(builder, ItemBusSize.REINFORCED);
    BIG = new Tier(builder, ItemBusSize.BIG);
    HUGE = new Tier(builder, ItemBusSize.HUGE);
    LUDICROUS = new Tier(builder, ItemBusSize.LUDICROUS);
  }

  public int itemSize(ItemBusSize size) {
    return switch(size) {
      case TINY -> TINY.size.get();
      case SMALL -> SMALL.size.get();
      case NORMAL -> NORMAL.size.get();
      case REINFORCED -> REINFORCED.size.get();
      case BIG -> BIG.size.get();
      case HUGE -> HUGE.size.get();
      case LUDICROUS -> LUDICROUS.size.get();
    };
  }
}
