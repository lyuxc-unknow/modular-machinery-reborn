package es.degrassi.mmreborn.common.crafting.helper.restriction;

import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;

public class RestrictionTank extends ComponentOutputRestrictor<HybridFluid> {

  public RestrictionTank(HybridFluid inserted, ProcessingComponent<?> exactComponent) {
    super(inserted, exactComponent);
  }
}
