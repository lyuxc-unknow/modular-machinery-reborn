package es.degrassi.mmreborn.common.crafting.helper;

import es.degrassi.mmreborn.common.machine.MachineComponent;

public record ProcessingComponent<T>(MachineComponent<T> component, T providedComponent) {}
