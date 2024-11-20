package es.degrassi.mmreborn.common.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class HybridTank extends FluidTank {

  public HybridTank(int capacity) {
    super(capacity);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("HybridTank{fluids:[");
    for (int i = 0; i < getTanks(); ++i, builder.append(", ")) {
      FluidStack fluid = getFluidInTank(i);
      builder.append(fluid.getAmount()).append("x ").append(fluid.getHoverName().getString());
    }
    builder.append("]}");
    return builder.toString();
  }
}
