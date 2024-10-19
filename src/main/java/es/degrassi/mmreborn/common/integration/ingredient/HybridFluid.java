package es.degrassi.mmreborn.common.integration.ingredient;

import net.neoforged.neoforge.fluids.FluidStack;

public class HybridFluid {

  private final FluidStack underlyingFluid;

  public HybridFluid(FluidStack underlyingFluid) {
    this.underlyingFluid = underlyingFluid;
  }

  public int getAmount() {
    if (underlyingFluid == FluidStack.EMPTY) {
      return 0;
    }
    return underlyingFluid.getAmount();
  }

  public void setAmount(int amount) {
    if (!underlyingFluid.isEmpty()) {
      underlyingFluid.setAmount(amount);
    }
  }

  public FluidStack asFluidStack() {
    return underlyingFluid;
  }

  public HybridFluid copy() {
    if (underlyingFluid == FluidStack.EMPTY) {
      return new HybridFluid(FluidStack.EMPTY.copy());
    }
    return new HybridFluid(this.underlyingFluid.copy());
  }
}
