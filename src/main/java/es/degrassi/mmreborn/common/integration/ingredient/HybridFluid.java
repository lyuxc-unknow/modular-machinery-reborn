package es.degrassi.mmreborn.common.integration.ingredient;

import javax.annotation.Nullable;
import net.neoforged.neoforge.fluids.FluidStack;

public class HybridFluid {

  @Nullable
  private final FluidStack underlyingFluid;

  public HybridFluid(@Nullable FluidStack underlyingFluid) {
    this.underlyingFluid = underlyingFluid;
  }

  public int getAmount() {
    if (underlyingFluid == null) {
      return 0;
    }
    return underlyingFluid.getAmount();
  }

  public void setAmount(int amount) {
    if (underlyingFluid != null) {
      underlyingFluid.setAmount(amount);
    }
  }

  @Nullable
  public FluidStack asFluidStack() {
    return underlyingFluid;
  }

  public HybridFluid copy() {
    if (underlyingFluid == null) {
      return new HybridFluid(null);
    }
    return new HybridFluid(this.underlyingFluid.copy());
  }
}
