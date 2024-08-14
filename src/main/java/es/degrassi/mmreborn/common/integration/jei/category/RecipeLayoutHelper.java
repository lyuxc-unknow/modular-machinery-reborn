package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.resources.ResourceLocation;

public class RecipeLayoutHelper {

  static final ResourceLocation LOCATION_JEI_ICONS = ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/jeirecipeicons.png");

  static RecipePart PART_TANK_SHELL;
  static RecipePart PART_ENERGY_BACKGROUND;
  static RecipePart PART_ENERGY_FOREGROUND;
  static RecipePart PART_INVENTORY_CELL;
  static RecipePart PART_PROCESS_ARROW;
  static RecipePart PART_PROCESS_ARROW_ACTIVE;

  static {
    init();
  }

  public static void init() {
    if(PART_TANK_SHELL != null) return;

    PART_TANK_SHELL               = new RecipePart(LOCATION_JEI_ICONS, 0,  0, 22, 63);
    PART_ENERGY_FOREGROUND        = new RecipePart(LOCATION_JEI_ICONS, 22, 0, 22, 63);
    PART_ENERGY_BACKGROUND        = new RecipePart(LOCATION_JEI_ICONS, 44, 0, 22, 63);
    PART_INVENTORY_CELL           = new RecipePart(LOCATION_JEI_ICONS, 66, 0, 18, 18);
    PART_PROCESS_ARROW            = new RecipePart(LOCATION_JEI_ICONS, 84, 0, 22, 15);
    PART_PROCESS_ARROW_ACTIVE     = new RecipePart(LOCATION_JEI_ICONS, 84, 15, 22, 15);
  }

  public static class RecipePart {

    public final IDrawable drawable;
    public final int xSize, zSize;

    public RecipePart(ResourceLocation location, int textureX, int textureZ, int xSize, int zSize) {
      this.drawable = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(location, textureX, textureZ, xSize, zSize);
      this.xSize = xSize;
      this.zSize = zSize;
    }

  }

}