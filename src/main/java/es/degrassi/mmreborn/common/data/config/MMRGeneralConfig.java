package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.util.LoggingLevel;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "general")
public class MMRGeneralConfig implements ConfigData {
  @ConfigEntry.Category("Logs")
  @ConfigEntry.Gui.EnumHandler
  @Comment("Configure what logs will be printed in the custommachinery.log file.\nOnly logs with level higher or equal than selected will be printed.\nFATAL > ERROR > WARN > INFO > DEBUG > ALL")
  public LoggingLevel debugLevel = LoggingLevel.INFO;

  @ConfigEntry.Category("Logs")
  @Comment("If true, all missing optional properties\nand their default values will be logged\nwhen parsing custom machines jsons.")
  public boolean logMissingOptional = false;

  @ConfigEntry.Category("Logs")
  @Comment("When parsing custom machines json files,\nsome properties can be read with 2 serializers.\nSet this to true to log when the first serializer throw an error,\neven if the second succeed.")
  public boolean logFirstEitherError = false;

  @ConfigEntry.Category("General")
  @ConfigEntry.ColorPicker(allowAlpha = true)
  @Comment("Defines the _default_ color for machine casings as items or blocks. (Hex color with alpha at start) Has to be defined both server and clientside!")
  public int general_casing_color = 0xFFFF4900;

  @ConfigEntry.Category("General")
  @ConfigEntry.BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
  @Comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
  public int checkStructureTicks = 5;

  @ConfigEntry.Category("General")
  @ConfigEntry.BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
  @Comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
  public int checkRecipeTicks = 80;

  @ConfigEntry.Category("Rendering")
  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
  @Comment("The time in milliseconds the structure requirement\nstructure will render in world when clicking\non the icon in the jei recipe.")
  public int structureRenderTime = 10000;

  @ConfigEntry.Category("Rendering")
  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
  @Comment("The time in milliseconds each blocks will be shown\nwhen using a block tag in a structure.")
  public int blockTagCycleTime = 1000;

  @ConfigEntry.Category("Directories")
  @Comment("A folder name where MMR will load machine structure json.\nThese folder must be under the \"data/<namespace>\" folder.")
  public String machineDirectory = "machines";
}
