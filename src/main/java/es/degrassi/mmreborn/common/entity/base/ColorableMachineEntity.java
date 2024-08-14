package es.degrassi.mmreborn.common.entity.base;

public interface ColorableMachineEntity {
  int getMachineColor();

  void setMachineColor(int newColor);

  void setRequestModelUpdate(boolean request);

  boolean isRequestModelUpdate();
}
