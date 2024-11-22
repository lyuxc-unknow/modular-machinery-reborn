package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MMREnergyHatchConfig {
  public final Tier TINY;

  public final Tier SMALL;

  public final Tier NORMAL;

  public final Tier REINFORCED;

  public final Tier BIG;

  public final Tier HUGE;

  public final Tier LUDICROUS;

  public final Tier ULTIMATE;

  public final ConfigValue<Boolean> displayFETooltip;
  public final ConfigValue<Boolean> displayIC2EUTooltip;
  public final ConfigValue<EnergyDisplayUtil.EnergyType> type;

//  @ConfigEntry.Category("Display")
//  @Comment("Set to true, if the standard 'energy' FE (or RF) should be displayed in the tooltip of the energy hatch along with its transmission rates.")
//  public boolean displayFETooltip = true;

//  @ConfigEntry.Category("Display")
//  @Comment("Set to true, if IC2's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if IC2 is installed.")
//  public boolean displayIC2EUTooltip = true;

//  @ConfigEntry.Category("Display")
//  @Comment("Set to true, if GT's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if GregTech (community edition) is installed.")
//  public boolean displayGTEUTooltip = true;

//  @ConfigEntry.Category("Display")
//  @Comment("Available options: 'FE', 'IC2_EU' - Default: FE - Set this to one of those 3 types to have GUI, recipe preview and energy be displayed in that type of energy in ALL ModularMachinery Reborn things.")
//  public EnergyDisplayUtil.EnergyType type = EnergyDisplayUtil.EnergyType.FE;

  public static class Tier {
    public final ConfigValue<Integer> size;
    public final ConfigValue<Integer> transferRate;
    public final ConfigValue<Integer> ic2_tier;

//    @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
//    @Comment("Energy storage size of the energy hatch. \n[range: 0 ~ 9223372036854775807]")
//    public long size;
//
//    @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
//    @Comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier. \n[range: 1 ~ 9223372036854775806")
//    public long transferRate;
//
//    @ConfigEntry.BoundedDiscrete(max = 12)
//    @Comment("Defines the IC2 output-voltage tier. \nOnly affects the power the output hatches will output power as. \n0 = 'ULV' = 8 EU/t, 1 = 'LV' = 32 EU/t, 2 = 'MV' = 128 EU/t, ... \n[range: 0 ~ 12")
//    public int ic2_tier;

    public Tier(ModConfigSpec.Builder builder, EnergyHatchSize tier) {
      builder.push(tier.getSerializedName());
      size = builder
          .comment("Energy storage size of the energy hatch. \n[range: 0 ~ 9223372036854775807]")
          .define("size", tier.defaultConfigurationEnergy);
      transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier. \n[range: 1 ~ 9223372036854775806")
          .define("transfer_rate", tier.defaultConfigurationTransferLimit);
      ic2_tier = builder
          .comment("Defines the IC2 output-voltage tier. \nOnly affects the power the output hatches will output power as. \n0 = 'ULV' = 8 EU/t, 1 = 'LV' = 32 EU/t, 2 = 'MV' = 128 EU/t, ... \n[range: 0 ~ 12")
          .define("ic2_tier", tier.defaultIC2EnergyTier);
      builder.pop();
    }
  }

  public MMREnergyHatchConfig(ModConfigSpec.Builder builder) {
    this.TINY = new Tier(builder, EnergyHatchSize.TINY);
    this.SMALL = new Tier(builder, EnergyHatchSize.SMALL);
    this.NORMAL = new Tier(builder, EnergyHatchSize.NORMAL);
    this.REINFORCED = new Tier(builder, EnergyHatchSize.REINFORCED);
    this.BIG = new Tier(builder, EnergyHatchSize.BIG);
    this.HUGE = new Tier(builder, EnergyHatchSize.HUGE);
    this.LUDICROUS = new Tier(builder, EnergyHatchSize.LUDICROUS);
    this.ULTIMATE = new Tier(builder, EnergyHatchSize.ULTIMATE);
    builder.push("Display");
    this.displayFETooltip = builder
        .comment("Set to true, if the standard 'energy' FE (or RF) should be displayed in the tooltip of the energy hatch along with its transmission rates.")
        .define("displayFETooltip", true);
    this.displayIC2EUTooltip = builder
        .comment("Set to true, if IC2's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if IC2 is installed.")
        .define("displayIC2EUTooltip", true);
    this.type = builder
        .comment("Available options: 'FE', 'IC2_EU' - Default: FE - Set this to one of those 2 types to have GUI, recipe preview and energy be displayed in that type of energy in ALL ModularMachinery Reborn things.")
        .defineEnum("type", EnergyDisplayUtil.EnergyType.FE);
    builder.pop();
  }

  public long energySize(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY.size.get();
      case SMALL -> SMALL.size.get();
      case NORMAL -> NORMAL.size.get();
      case REINFORCED -> REINFORCED.size.get();
      case BIG -> BIG.size.get();
      case HUGE -> HUGE.size.get();
      case LUDICROUS -> LUDICROUS.size.get();
      case ULTIMATE -> ULTIMATE.size.get();
    };
  }

  public long energyLimit(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY.transferRate.get();
      case SMALL -> SMALL.transferRate.get();
      case NORMAL -> NORMAL.transferRate.get();
      case REINFORCED -> REINFORCED.transferRate.get();
      case BIG -> BIG.transferRate.get();
      case HUGE -> HUGE.transferRate.get();
      case LUDICROUS -> LUDICROUS.transferRate.get();
      case ULTIMATE -> ULTIMATE.transferRate.get();
    };
  }

  public int energyTier(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY.ic2_tier.get();
      case SMALL -> SMALL.ic2_tier.get();
      case NORMAL -> NORMAL.ic2_tier.get();
      case REINFORCED -> REINFORCED.ic2_tier.get();
      case BIG -> BIG.ic2_tier.get();
      case HUGE -> HUGE.ic2_tier.get();
      case LUDICROUS -> LUDICROUS.ic2_tier.get();
      case ULTIMATE -> ULTIMATE.ic2_tier.get();
    };
  }
}
