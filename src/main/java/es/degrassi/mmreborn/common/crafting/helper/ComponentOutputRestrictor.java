package es.degrassi.mmreborn.common.crafting.helper;

import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public abstract class ComponentOutputRestrictor<T> {
  public final ProcessingComponent<?> exactComponent;
  public final T inserted;

  public ComponentOutputRestrictor(T inserted, ProcessingComponent<?> component) {
    exactComponent = component;
    this.inserted = inserted;
  }

  public static class RestrictionChemical extends ComponentOutputRestrictor<ChemicalStack> {

    public RestrictionChemical(ChemicalStack inserted, ProcessingComponent<?> exactComponent) {
      super(inserted, exactComponent);
    }
  }

  public static class RestrictionTank extends ComponentOutputRestrictor<HybridFluid> {

    public RestrictionTank(HybridFluid inserted, ProcessingComponent<?> exactComponent) {
      super(inserted, exactComponent);
    }
  }

  public static class RestrictionInventory extends ComponentOutputRestrictor<ItemStack> {

    public RestrictionInventory(ItemStack inserted, ProcessingComponent<?> exactComponent) {
      super(inserted, exactComponent);
    }
  }

}
