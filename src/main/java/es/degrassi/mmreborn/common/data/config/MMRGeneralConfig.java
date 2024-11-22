package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.util.LoggingLevel;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MMRGeneralConfig {
  public final ConfigValue<LoggingLevel> debugLevel;
  public final ConfigValue<Boolean> logMissingOptional;
  public final ConfigValue<Boolean> logFirstEitherError;

//  @ConfigEntry.Category("Logs")
//  @ConfigEntry.Gui.EnumHandler
//  @Comment("Configure what logs will be printed in the custommachinery.log file.\nOnly logs with level higher or equal than selected will be printed.\nFATAL > ERROR > WARN > INFO > DEBUG > ALL")
//  public LoggingLevel debugLevel = LoggingLevel.INFO;
//
//  @ConfigEntry.Category("Logs")
//  @Comment("If true, all missing optional properties\nand their default values will be logged\nwhen parsing custom machines jsons.")
//  public boolean logMissingOptional = false;
//
//  @ConfigEntry.Category("Logs")
//  @Comment("When parsing custom machines json files,\nsome properties can be read with 2 serializers.\nSet this to true to log when the first serializer throw an error,\neven if the second succeed.")
//  public boolean logFirstEitherError = false;

  public final ConfigValue<Integer> general_casing_color;
  public final ConfigValue<Integer> checkStructureTicks;
  public final ConfigValue<Integer> checkRecipeTicks;

//  @ConfigEntry.Category("General")
//  @ConfigEntry.ColorPicker(allowAlpha = true)
//  @Comment("Defines the _default_ color for machine casings as items or blocks. (Hex color with alpha at start) Has to be defined both server and clientside!")
//  public int general_casing_color = 0xFFFF4900;

//  @ConfigEntry.Category("General")
//  @ConfigEntry.BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
//  @Comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
//  public int checkStructureTicks = 5;

//  @ConfigEntry.Category("General")
//  @ConfigEntry.BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
//  @Comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
//  public int checkRecipeTicks = 80;

  public final ConfigValue<Integer> structureRenderTime;
  public final ConfigValue<Integer> blockTagCycleTime;

//  @ConfigEntry.Category("Rendering")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("The time in milliseconds the structure requirement\nstructure will render in world when clicking\non the icon in the jei recipe.")
//  public int structureRenderTime = 10000;
//
//  @ConfigEntry.Category("Rendering")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("The time in milliseconds each blocks will be shown\nwhen using a block tag in a structure.")
//  public int blockTagCycleTime = 1000;

//  @ConfigEntry.Category("Directories")
//  @Comment("A folder name where MMR will load machine structure json.\nThese folder must be under the \"data/<namespace>\" folder.")
//  public String machineDirectory = "machines";
  public final ConfigValue<String> machineDirectory;

  public MMRGeneralConfig(ModConfigSpec.Builder builder) {
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

    //LOGS
    {
      builder.push("General");
      this.general_casing_color = builder
          .comment("Defines the _default_ color for machine casings as items or blocks. (Hex color with alpha at start) Has to be defined both server and clientside!")
          .define("general_casing_color", 0xFFFF4900);
      this.checkStructureTicks = builder
          .comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
          .define("check_structure_ticks", 5);
      this.checkRecipeTicks = builder
          .comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
          .define("check_recipe_ticks", 20);
      builder.pop();
    }

    // RENDERING
    {
      builder.push("Rendering");
      this.structureRenderTime = builder
          .comment("The time in milliseconds the structure requirement\nstructure will render in world when clicking\non the icon in the jei recipe.")
          .define("structure_render_time", 10000);
      this.blockTagCycleTime = builder
          .comment("The time in milliseconds each blocks will be shown\nwhen using a block tag in a structure.")
          .define("block_tag_cycle_time", 1000);
      builder.pop();
    }

    // DIRECTORIES
    {
      builder.push("Directories");
      this.machineDirectory = builder
          .comment("A folder name where MMR will load machine structure json.\nThese folder must be under the \"data/<namespace>\" folder.")
          .define("machine_directory", "machines");
      builder.pop();
    }
  }
}
