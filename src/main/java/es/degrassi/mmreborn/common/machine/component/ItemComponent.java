package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public class ItemComponent extends MachineComponent<IOInventory> {
  private final IOInventory handler;

  public ItemComponent(IOInventory handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public IOInventory getContainerProvider() {
    return handler;
  }


  public int getItemAmount(ItemStack stack) {
    return this.handler.getItemAmount(stack);
  }

  public int getSpaceForItem(ItemStack stack) {
    return handler.getSpaceForItem(stack);
  }

  public void removeFromInputs(ItemStack stack, int amount) {
    handler.removeFromInputs(stack, amount);
  }

  public void addToOutputs(ItemStack stack, int amount) {
    handler.addToOutputs(stack, amount);
  }

  @Override
  public CompoundTag asTag(HolderLookup.Provider provider) {
    CompoundTag tag = super.asTag(provider);
    tag.put("handler", handler.writeNBT(provider));
    return tag;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> C merge(C c) {
    ItemComponent comp = (ItemComponent) c;
    return (C) new ItemComponent(
        IOInventory.mergeBuild(handler, comp.handler),
        getIOType()
    );
  }
}
