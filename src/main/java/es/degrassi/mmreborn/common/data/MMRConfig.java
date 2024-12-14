package es.degrassi.mmreborn.common.data;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.util.LoggingLevel;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MMRConfig {
  private static final MMRConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<MMRConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(MMRConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public final ConfigValue<LoggingLevel> debugLevel;
  public final ConfigValue<Boolean> logMissingOptional;
  public final ConfigValue<Boolean> logFirstEitherError;

  public final ConfigValue<String> general_casing_color;
  public final ConfigValue<Integer> checkStructureTicks;
  public final ConfigValue<Integer> checkRecipeTicks;

  public final ConfigValue<Integer> structureRenderTime;
  public final ConfigValue<Integer> blockTagCycleTime;
  public final ConfigValue<String> machineDirectory;
  public final ConfigValue<List<String>> modelFolders;

  public final ConfigValue<Integer> TINY_energy_size;
  public final ConfigValue<Integer> TINY_energy_transferRate;

  public final ConfigValue<Integer> SMALL_energy_size;
  public final ConfigValue<Integer> SMALL_energy_transferRate;

  public final ConfigValue<Integer> NORMAL_energy_size;
  public final ConfigValue<Integer> NORMAL_energy_transferRate;

  public final ConfigValue<Integer> REINFORCED_energy_size;
  public final ConfigValue<Integer> REINFORCED_energy_transferRate;

  public final ConfigValue<Integer> BIG_energy_size;
  public final ConfigValue<Integer> BIG_energy_transferRate;

  public final ConfigValue<Integer> HUGE_energy_size;
  public final ConfigValue<Integer> HUGE_energy_transferRate;

  public final ConfigValue<Integer> LUDICROUS_energy_size;
  public final ConfigValue<Integer> LUDICROUS_energy_transferRate;

  public final ConfigValue<Integer> ULTIMATE_energy_size;
  public final ConfigValue<Integer> ULTIMATE_energy_transferRate;

  public final ConfigValue<Boolean> energy_displayFETooltip;
  public final ConfigValue<Boolean> energy_displayIC2EUTooltip;
  public final ConfigValue<EnergyDisplayUtil.EnergyType> energy_type;

  public final ConfigValue<Integer> TINY_fluid_size;

  public final ConfigValue<Integer> SMALL_fluid_size;

  public final ConfigValue<Integer> NORMAL_fluid_size;

  public final ConfigValue<Integer> REINFORCED_fluid_size;

  public final ConfigValue<Integer> BIG_fluid_size;

  public final ConfigValue<Integer> HUGE_fluid_size;

  public final ConfigValue<Integer> LUDICROUS_fluid_size;

  public final ConfigValue<Integer> VACUUM_fluid_size;

  public final ConfigValue<Integer> TINY_experience_size;
  public final ConfigValue<Integer> SMALL_experience_size;
  public final ConfigValue<Integer> NORMAL_experience_size;
  public final ConfigValue<Integer> REINFORCED_experience_size;
  public final ConfigValue<Integer> BIG_experience_size;
  public final ConfigValue<Integer> HUGE_experience_size;
  public final ConfigValue<Integer> LUDICROUS_experience_size;
  public final ConfigValue<Integer> VACUUM_experience_size;

  public static MMRConfig get() {
    return INSTANCE;
  }

  public MMRConfig(ModConfigSpec.Builder builder) {
    builder.push("general");
    {
      //LOGS
      {
        builder.push("Logs");
        this.logMissingOptional = builder
            .comment("If true, all missing optional properties\nand their default values will be logged\nwhen parsing custom machines jsons.")
            .define("log_missing_optional", false);
        this.logFirstEitherError = builder
            .comment("When parsing custom machines json files,\nsome properties can be read with 2 serializers.\nSet this to true to log when the first serializer throw an error,\neven if the second succeed.")
            .define("log_first_either_error", false);
        this.debugLevel = builder
            .comment("Configure what logs will be printed in the custommachinery.log file.\nOnly logs with level higher or equal than selected will be printed.\nFATAL > ERROR > WARN > INFO > DEBUG > ALL")
            .defineEnum("debug_level", LoggingLevel.INFO);
        builder.pop();
      }

      //GENERAL
      {
        builder.push("General");
        this.general_casing_color = builder
            .comment("Defines the _default_ color for machine casings as items or blocks. (Hex color with alpha at start) Has to be defined both server and clientside!")
            .define("general_casing_color", "#FFFF4900");
        this.checkStructureTicks = builder
            .comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
            .defineInRange("check_structure_ticks", 5, 1, Integer.MAX_VALUE);
        this.checkRecipeTicks = builder
            .comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
            .defineInRange("check_recipe_ticks", 20, 1, Integer.MAX_VALUE);
        builder.pop();
      }

      // RENDERING
      {
        builder.push("Rendering");
        this.structureRenderTime = builder
            .comment("The time in milliseconds the structure requirement\nstructure will render in world when clicking\non the icon in the jei recipe.")
            .defineInRange("structure_render_time", 10000, 1, Integer.MAX_VALUE);
        this.blockTagCycleTime = builder
            .comment("The time in milliseconds each blocks will be shown\nwhen using a block tag in a structure.")
            .defineInRange("block_tag_cycle_time", 1000, 1, Integer.MAX_VALUE);
        builder.pop();
      }

      // DIRECTORIES
      {
        builder.push("Directories");
        this.machineDirectory = builder
            .comment("A folder name where MMR will load machine structure json.\nThese folder must be under the \"data/<namespace>\" folder.")
            .define("machine_directory", "machines");
        this.modelFolders = builder
            .comment("A list of folder names where MMR will load controller models json. These folders must be under the 'assets/namespace/models' folder.")
            .define("model_folders", Lists.newArrayList("controller", "controllers"));
        builder.pop();
      }
    }
    builder.pop();
    builder.push("energyHatch");
    {
      builder.push(EnergyHatchSize.TINY.getSerializedName());
      TINY_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.TINY.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      TINY_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.TINY.defaultConfigurationTransferLimit, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.SMALL.getSerializedName());
      SMALL_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.SMALL.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      SMALL_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.SMALL.defaultConfigurationTransferLimit, 1,
              Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.NORMAL.getSerializedName());
      NORMAL_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.NORMAL.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      NORMAL_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.NORMAL.defaultConfigurationTransferLimit, 1,
              Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.REINFORCED.getSerializedName());
      REINFORCED_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.REINFORCED.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      REINFORCED_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.REINFORCED.defaultConfigurationTransferLimit, 1,
              Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.BIG.getSerializedName());
      BIG_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.BIG.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      BIG_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.BIG.defaultConfigurationTransferLimit, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.HUGE.getSerializedName());
      HUGE_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.HUGE.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      HUGE_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.HUGE.defaultConfigurationTransferLimit, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.LUDICROUS.getSerializedName());
      LUDICROUS_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.LUDICROUS.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      LUDICROUS_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.LUDICROUS.defaultConfigurationTransferLimit, 1,
              Integer.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.ULTIMATE.getSerializedName());
      ULTIMATE_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.ULTIMATE.defaultConfigurationEnergy, 1, Integer.MAX_VALUE);
      ULTIMATE_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.ULTIMATE.defaultConfigurationTransferLimit, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push("Display");
      this.energy_displayFETooltip = builder
          .comment("Set to true, if the standard 'energy' FE (or RF) should be displayed in the tooltip of the energy hatch along with its transmission rates.")
          .define("displayFETooltip", true);
      this.energy_displayIC2EUTooltip = builder
          .comment("Set to true, if IC2's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if IC2 is installed.")
          .define("displayIC2EUTooltip", true);
      this.energy_type = builder
          .comment("Available options: 'FE', 'IC2_EU' - Default: FE - Set this to one of those 2 types to have GUI, recipe preview and energy be displayed in that type of energy in ALL ModularMachinery Reborn things.")
          .defineEnum("type", EnergyDisplayUtil.EnergyType.FE);
      builder.pop();
    }
    builder.pop();
    builder.push("fluidHatch");
    {
      builder.push(FluidHatchSize.TINY.getSerializedName());
      TINY_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.TINY.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.SMALL.getSerializedName());
      SMALL_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.SMALL.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.NORMAL.getSerializedName());
      NORMAL_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.NORMAL.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.REINFORCED.getSerializedName());
      REINFORCED_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.REINFORCED.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.BIG.getSerializedName());
      BIG_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.BIG.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.HUGE.getSerializedName());
      HUGE_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.HUGE.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.LUDICROUS.getSerializedName());
      LUDICROUS_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.LUDICROUS.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(FluidHatchSize.VACUUM.getSerializedName());
      VACUUM_fluid_size = builder
          .comment("Defines the tank size of fluid hatch in mB")
          .defineInRange("size", FluidHatchSize.VACUUM.defaultConfigurationValue, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    builder.pop();
//    builder.push("itemBus");
//    {
//    }
//    builder.pop();
    builder.push("experienceHatch");
    {
      builder.push(ExperienceHatchSize.TINY.getSerializedName());
      TINY_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.TINY.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.SMALL.getSerializedName());
      SMALL_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.SMALL.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.NORMAL.getSerializedName());
      NORMAL_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.NORMAL.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.REINFORCED.getSerializedName());
      REINFORCED_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.REINFORCED.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.BIG.getSerializedName());
      BIG_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.BIG.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.HUGE.getSerializedName());
      HUGE_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.HUGE.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.LUDICROUS.getSerializedName());
      LUDICROUS_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.LUDICROUS.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ExperienceHatchSize.VACUUM.getSerializedName());
      VACUUM_experience_size = builder
          .comment("Defines the Experience Hatch")
          .defineInRange("capacity", ExperienceHatchSize.VACUUM.defaultCapacity, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    builder.pop();
  }

  public int fluidSize(FluidHatchSize size) {
    return switch(size) {
      case TINY -> TINY_fluid_size.get();
      case SMALL -> SMALL_fluid_size.get();
      case NORMAL -> NORMAL_fluid_size.get();
      case REINFORCED -> REINFORCED_fluid_size.get();
      case BIG -> BIG_fluid_size.get();
      case HUGE -> HUGE_fluid_size.get();
      case LUDICROUS -> LUDICROUS_fluid_size.get();
      case VACUUM -> VACUUM_fluid_size.get();
    };
  }

  public long energySize(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY_energy_size.get();
      case SMALL -> SMALL_energy_size.get();
      case NORMAL -> NORMAL_energy_size.get();
      case REINFORCED -> REINFORCED_energy_size.get();
      case BIG -> BIG_energy_size.get();
      case HUGE -> HUGE_energy_size.get();
      case LUDICROUS -> LUDICROUS_energy_size.get();
      case ULTIMATE -> ULTIMATE_energy_size.get();
    };
  }

  public long energyLimit(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY_energy_transferRate.get();
      case SMALL -> SMALL_energy_transferRate.get();
      case NORMAL -> NORMAL_energy_transferRate.get();
      case REINFORCED -> REINFORCED_energy_transferRate.get();
      case BIG -> BIG_energy_transferRate.get();
      case HUGE -> HUGE_energy_transferRate.get();
      case LUDICROUS -> LUDICROUS_energy_transferRate.get();
      case ULTIMATE -> ULTIMATE_energy_transferRate.get();
    };
  }

  public int experienceSize(ExperienceHatchSize size) {
    return switch(size) {
      case TINY -> TINY_experience_size.get();
      case SMALL -> SMALL_experience_size.get();
      case NORMAL -> NORMAL_experience_size.get();
      case REINFORCED -> REINFORCED_experience_size.get();
      case BIG -> BIG_experience_size.get();
      case HUGE -> HUGE_experience_size.get();
      case LUDICROUS -> LUDICROUS_experience_size.get();
      case VACUUM -> VACUUM_experience_size.get();
    };
  }
}
