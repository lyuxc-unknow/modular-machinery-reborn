package es.degrassi.mmreborn.common.data;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
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
  public final ConfigValue<String> chance_color;
  public final ConfigValue<Integer> checkStructureTicks;
  public final ConfigValue<Integer> checkRecipeTicks;
  public final ConfigValue<Integer> maxParallel;

  public final ConfigValue<Boolean> shouldReplace;
  public final ConfigValue<Boolean> sendReplaceMessage;
  public final ConfigValue<Boolean> sendMissingBlockMessage;
  public final ConfigValue<Boolean> sendErrorMessage;

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

  public final ConfigValue<Integer> itemSlotXOffset;
  public final ConfigValue<Integer> itemSlotYOffset;

  public final ConfigValue<Integer> TINY_item_size;
  public final ConfigValue<Integer> SMALL_item_size;
  public final ConfigValue<Integer> NORMAL_item_size;
  public final ConfigValue<Integer> REINFORCED_item_size;
  public final ConfigValue<Integer> BIG_item_size;
  public final ConfigValue<Integer> HUGE_item_size;
  public final ConfigValue<Integer> LUDICROUS_item_size;

  public final ConfigValue<Integer> TINY_item_cols;
  public final ConfigValue<Integer> SMALL_item_cols;
  public final ConfigValue<Integer> NORMAL_item_cols;
  public final ConfigValue<Integer> REINFORCED_item_cols;
  public final ConfigValue<Integer> BIG_item_cols;
  public final ConfigValue<Integer> HUGE_item_cols;
  public final ConfigValue<Integer> LUDICROUS_item_cols;

  public final ConfigValue<Integer> TINY_experience_size;
  public final ConfigValue<Integer> SMALL_experience_size;
  public final ConfigValue<Integer> NORMAL_experience_size;
  public final ConfigValue<Integer> REINFORCED_experience_size;
  public final ConfigValue<Integer> BIG_experience_size;
  public final ConfigValue<Integer> HUGE_experience_size;
  public final ConfigValue<Integer> LUDICROUS_experience_size;
  public final ConfigValue<Integer> VACUUM_experience_size;

  public final ConfigValue<Integer> BASIC_parallel;
  public final ConfigValue<Integer> MEDIUM_parallel;
  public final ConfigValue<Integer> ADVANCED_parallel;
  public final ConfigValue<Integer> ULTIMATE_parallel;
  public final ConfigValue<Integer> MAX_parallel;

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
        this.chance_color = builder
            .comment("Defines the _default_ color for EMI/JEI chance text color. (Hex color without alpha) Has to be defined both server and clientside!")
            .define("chance_color", "#FFFFFF");
        this.checkStructureTicks = builder
            .comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
            .defineInRange("check_structure_ticks", 5, 1, Integer.MAX_VALUE);
        this.checkRecipeTicks = builder
            .comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
            .defineInRange("check_recipe_ticks", 20, 1, Integer.MAX_VALUE);
        this.maxParallel = builder
            .comment("Defines the number of max parallel recipes that can be run on multiblocks. If this number is " +
                "below than any on [parallel hatch] config path, it will use the max value of them instead.")
            .defineInRange("maxParallel", 256, 1, Integer.MAX_VALUE);
        builder.pop();
      }

      // STRUCTURE
      {
        builder.push("Structure");
        this.shouldReplace = builder
            .comment("Defines if it should break and place the non-matching blocks on trying to place structure. Default: true")
            .define("should_replace", true);
        this.sendReplaceMessage = builder
            .comment("Defines if should sent a message to the player for each replaced block. Default: true")
            .define("replaceMessage", true);
        this.sendMissingBlockMessage = builder
            .comment("Defines if should sent a message to the player for each missing block. Default: true")
            .define("missingBlockMessage", true);
        this.sendErrorMessage = builder
            .comment("Defines if should sent a message to the player for each error on place block. Default: true")
            .define("errorMessage", true);
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
    builder.push("itemBus");
    {
      builder.push("container");
      {
        itemSlotXOffset = builder
            .comment("Defines the container X offset relative to top-left corner of the texture")
            .defineInRange("xOffset", 8, 0, Integer.MAX_VALUE);
        itemSlotYOffset = builder
            .comment("Defines the container Y offset relative to top-left corner of the texture")
            .defineInRange("yOffset", 8, 0, Integer.MAX_VALUE);
      }
      builder.pop();
      builder.push(ItemBusSize.TINY.getSerializedName());
      TINY_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.TINY.defaultSlots, 1, Integer.MAX_VALUE);
      TINY_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.TINY.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.SMALL.getSerializedName());
      SMALL_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.SMALL.defaultSlots, 1, Integer.MAX_VALUE);
      SMALL_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.SMALL.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.NORMAL.getSerializedName());
      NORMAL_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.NORMAL.defaultSlots, 1, Integer.MAX_VALUE);
      NORMAL_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.NORMAL.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.REINFORCED.getSerializedName());
      REINFORCED_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.REINFORCED.defaultSlots, 1, Integer.MAX_VALUE);
      REINFORCED_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.REINFORCED.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.BIG.getSerializedName());
      BIG_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.BIG.defaultSlots, 1, Integer.MAX_VALUE);
      BIG_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.BIG.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.HUGE.getSerializedName());
      HUGE_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.HUGE.defaultSlots, 1, Integer.MAX_VALUE);
      HUGE_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.HUGE.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ItemBusSize.LUDICROUS.getSerializedName());
      LUDICROUS_item_size = builder
          .comment("Defines the slots number of item bus")
          .defineInRange("slots", ItemBusSize.LUDICROUS.defaultSlots, 1, Integer.MAX_VALUE);
      LUDICROUS_item_cols = builder
          .comment("Defines the slot cols number of item bus")
          .defineInRange("cols", ItemBusSize.LUDICROUS.defaultCols, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    builder.pop();
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
    builder.push("parallel hatch");
    {
      builder.push(ParallelHatchSize.BASIC.getSerializedName());
      BASIC_parallel = builder
          .comment("Defined the max amount of running recipes")
          .defineInRange("max", ParallelHatchSize.BASIC.defaultMax, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ParallelHatchSize.MEDIUM.getSerializedName());
      MEDIUM_parallel = builder
          .comment("Defined the max amount of running recipes")
          .defineInRange("max", ParallelHatchSize.MEDIUM.defaultMax, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ParallelHatchSize.ADVANCED.getSerializedName());
      ADVANCED_parallel = builder
          .comment("Defined the max amount of running recipes")
          .defineInRange("max", ParallelHatchSize.ADVANCED.defaultMax, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ParallelHatchSize.ULTIMATE.getSerializedName());
      ULTIMATE_parallel = builder
          .comment("Defined the max amount of running recipes")
          .defineInRange("max", ParallelHatchSize.ULTIMATE.defaultMax, 1, Integer.MAX_VALUE);
      builder.pop();
      builder.push(ParallelHatchSize.MAX.getSerializedName());
      MAX_parallel = builder
          .comment("Defined the max amount of running recipes")
          .defineInRange("max", ParallelHatchSize.MAX.defaultMax, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    builder.pop();
  }

  public int fluidSize(FluidHatchSize size) {
    return switch (size) {
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

  public int itemSize(ItemBusSize size) {
    return switch (size) {
      case TINY -> TINY_item_size.get();
      case SMALL -> SMALL_item_size.get();
      case NORMAL -> NORMAL_item_size.get();
      case REINFORCED -> REINFORCED_item_size.get();
      case BIG -> BIG_item_size.get();
      case HUGE -> HUGE_item_size.get();
      case LUDICROUS -> LUDICROUS_item_size.get();
    };
  }

  public int itemCols(ItemBusSize size) {
    return switch (size) {
      case TINY -> TINY_item_cols.get();
      case SMALL -> SMALL_item_cols.get();
      case NORMAL -> NORMAL_item_cols.get();
      case REINFORCED -> REINFORCED_item_cols.get();
      case BIG -> BIG_item_cols.get();
      case HUGE -> HUGE_item_cols.get();
      case LUDICROUS -> LUDICROUS_item_cols.get();
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
    return switch (size) {
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

  public int maxParallel(ParallelHatchSize size) {
    return switch (size) {
      case BASIC -> BASIC_parallel.get();
      case MEDIUM -> MEDIUM_parallel.get();
      case ADVANCED -> ADVANCED_parallel.get();
      case ULTIMATE -> ULTIMATE_parallel.get();
      case MAX -> MAX_parallel.get();
    };
  }

  public int getMaxParallel() {
    int max = BASIC_parallel.get();
    for (ParallelHatchSize size : ParallelHatchSize.values()) {
      int probable = maxParallel(size);
      if (probable > max) max = probable;
    }
    return max;
  }
}
