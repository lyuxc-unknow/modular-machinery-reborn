package es.degrassi.mmreborn.common.entity.base;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ColorableMachineEntity {

  int getMachineColor();

  void setMachineColor(int newColor);

  void setRequestModelUpdate(boolean request);

  boolean isRequestModelUpdate();
}
