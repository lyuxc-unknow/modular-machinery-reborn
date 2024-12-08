package es.degrassi.mmreborn.common.crafting.helper;

public abstract class ComponentOutputRestrictor<T> {
  public final ProcessingComponent<?> exactComponent;
  public final T inserted;

  public ComponentOutputRestrictor(T inserted, ProcessingComponent<?> component) {
    exactComponent = component;
    this.inserted = inserted;
  }
}
