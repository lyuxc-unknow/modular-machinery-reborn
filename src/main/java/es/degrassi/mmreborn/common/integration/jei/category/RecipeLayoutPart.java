package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import java.awt.Point;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class RecipeLayoutPart<T> implements IIngredientType<T> {

  private final Point offset;

  protected RecipeLayoutPart(Point offset) {
    this.offset = offset;
  }

  public abstract int getComponentWidth();

  public abstract int getComponentHeight();

  public final Point getOffset() {
    return offset;
  }

  public abstract Class<T> getLayoutTypeClass();

  public abstract IIngredientRenderer<T> provideIngredientRenderer();

  public abstract int getRendererPaddingX();

  public abstract int getRendererPaddingY();

  //Defines how many of them can be placed next to each other horizontally, before
  //a new 'line' is used for more.
  public abstract int getMaxHorizontalCount();

  public abstract int getComponentHorizontalGap();

  public abstract int getComponentVerticalGap();

  //The higher number, the more left (for inputs) and the more right (for outputs) the component is gonna appear.
  //Should be unique/final depending on component type and NOT vary between different recipe instances or components!!

  //Defaults:
  //1000 is energy
  //100 is fluids/mek gases
  //10 is items
  public abstract int getComponentHorizontalSortingOrder();

  @Deprecated
  public abstract boolean canBeScaled();

  public abstract void drawBackground(GuiGraphics mc);


  @Override
  public Class<T> getIngredientClass() {
    return getLayoutTypeClass();
  }

  public static class Tank extends RecipeLayoutPart<HybridFluid> {

    public Tank(Point offset) {
      super(offset);
    }

    @Override
    public int getComponentHeight() {
      return 63;
    }

    @Override
    public int getComponentWidth() {
      return 22;
    }

    @Override
    public Class<HybridFluid> getLayoutTypeClass() {
      return HybridFluid.class;
    }

    @Override
    public int getComponentHorizontalGap() {
      return 4;
    }

    @Override
    public int getComponentVerticalGap() {
      return 4;
    }

    @Override
    public int getMaxHorizontalCount() {
      return 2;
    }

    @Override
    public int getComponentHorizontalSortingOrder() {
      return 100;
    }

    @Override
    public boolean canBeScaled() {
      return true;
    }

    @Override
    public IIngredientRenderer<HybridFluid> provideIngredientRenderer() {
//      HybridFluidRenderer<HybridFluid> copy = new HybridFluidRenderer<>().
//        copyPrepareFluidRender(
//          getComponentWidth(),
//          getComponentHeight(),
//          1000,
//          false,
//          RecipeLayoutHelper.PART_TANK_SHELL.drawable);
//      return copy;
      return null;
    }

    @Override
    public int getRendererPaddingX() {
      return 0;
    }

    @Override
    public int getRendererPaddingY() {
      return 0;
    }

//    @Optional.Method(modid = "mekanism")
//    private HybridFluidRenderer<HybridFluid> addGasRenderer(HybridFluidRenderer<HybridFluid> copy) {
//      return copy.copyPrepareGasRender(
//        getComponentWidth(),
//        getComponentHeight(),
//        1000,
//        false,
//        RecipeLayoutHelper.PART_TANK_SHELL.drawable);
//    }

    @Override
    public void drawBackground(GuiGraphics guiGraphics) {
    }

  }

  public static class Energy extends RecipeLayoutPart<Long> {

    public Energy(Point offset) {
      super(offset);
    }

    @Override
    public int getComponentWidth() {
      return 22;
    }

    @Override
    public int getComponentHeight() {
      return 63;
    }

    @Override
    public Class<Long> getLayoutTypeClass() {
      return Long.class;
    }

    @Override
    public int getMaxHorizontalCount() {
      return 1;
    }

    @Override
    public int getComponentHorizontalGap() {
      return 0;
    }

    @Override
    public int getComponentVerticalGap() {
      return 4;
    }

    @Override
    public int getComponentHorizontalSortingOrder() {
      return 1000;
    }

    @Override
    public boolean canBeScaled() {
      return true;
    }

    @Override
    public IIngredientRenderer<Long> provideIngredientRenderer() {
      throw new UnsupportedOperationException("Cannot provide Energy ingredientrenderer as this is no ingredient!");
    }

    @Override
    public int getRendererPaddingX() {
      return 0;
    }

    @Override
    public int getRendererPaddingY() {
      return 0;
    }

    @Override
    public void drawBackground(GuiGraphics mc) {
      RecipeLayoutHelper.PART_ENERGY_BACKGROUND.drawable.draw(mc, getOffset().x, getOffset().y);
    }

    public void drawEnergy(GuiGraphics mc, Long energy) {
      if (energy > 0) {
        RecipeLayoutHelper.PART_ENERGY_FOREGROUND.drawable.draw(mc, getOffset().x, getOffset().y);
      }
    }
  }

  public static class Item extends RecipeLayoutPart<ItemStack> {

    public Item(Point offset) {
      super(offset);
    }

    @Override
    public int getComponentHeight() {
      return 18;
    }

    @Override
    public int getComponentWidth() {
      return 18;
    }

    @Override
    public Class<ItemStack> getLayoutTypeClass() {
      return ItemStack.class;
    }

    @Override
    public int getMaxHorizontalCount() {
      return 3;
    }

    @Override
    public int getComponentVerticalGap() {
      return 0;
    }

    @Override
    public int getComponentHorizontalGap() {
      return 0;
    }

    @Override
    public int getComponentHorizontalSortingOrder() {
      return 10;
    }

    @Override
    public boolean canBeScaled() {
      return false;
    }

    @Override
    public IIngredientRenderer<ItemStack> provideIngredientRenderer() {
      return MMRJeiPlugin.jeiHelpers.getIngredientManager().getIngredientRenderer(Items.AIR.getDefaultInstance());
    }

    @Override
    public int getRendererPaddingX() {
      return 1;
    }

    @Override
    public int getRendererPaddingY() {
      return 1;
    }

    @Override
    public void drawBackground(GuiGraphics mc) {
      RecipeLayoutHelper.PART_INVENTORY_CELL.drawable.draw(mc, getOffset().x, getOffset().y);
    }

  }

}
