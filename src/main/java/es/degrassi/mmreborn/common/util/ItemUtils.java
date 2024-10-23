package es.degrassi.mmreborn.common.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemUtils {
  public static void decrStackInInventory(ItemStackHandler handler, int slot) {
    if (slot < 0 || slot >= handler.getSlots()) return;
    ItemStack st = handler.getStackInSlot(slot);
    if (st.isEmpty()) return;
    st.setCount(st.getCount() - 1);
    if (st.getCount() <= 0) {
      handler.setStackInSlot(slot, ItemStack.EMPTY);
    }
  }

  //Negative amount: overhead fuel burnt
  //Positive amount: Failure/couldn't find enough fuel
  public static int consumeFromInventoryFuel(IItemHandlerModifiable handler, int fuelAmtToConsume, boolean simulate, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> contents = findItemsIndexedInInventoryFuel(handler, matchNBTTag);
    if (contents.isEmpty()) {
      return fuelAmtToConsume;
    }

    for (int slot : contents.keySet()) {
      ItemStack inSlot = contents.get(slot);
      if (!simulate) {
      }
      if (fuelAmtToConsume <= 0) {
        break;
      }
    }
    return fuelAmtToConsume;
  }

  public static boolean consumeFromInventory(IItemHandlerModifiable handler, ItemStack toConsume, boolean simulate, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> contents = findItemsIndexedInInventory(handler, toConsume, false, matchNBTTag);
    if (contents.isEmpty()) return false;

    int cAmt = toConsume.getCount();
    for (int slot : contents.keySet()) {
      ItemStack inSlot = contents.get(slot);
      int toRemove = Math.min(cAmt, inSlot.getCount());
      cAmt -= toRemove;
      if (!simulate) {
        handler.setStackInSlot(slot, copyStackWithSize(inSlot, inSlot.getCount() - toRemove));
      }
      if (cAmt <= 0) return true;
    }
    return false;
  }

  public static boolean consumeFromInventoryOreDict(IItemHandlerModifiable handler, ResourceLocation oreName, int amount, boolean simulate, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> contents = findItemsIndexedInInventoryOreDict(handler, oreName, matchNBTTag);
    if (contents.isEmpty()) return false;

    int cAmt = amount;
    for (int slot : contents.keySet()) {
      ItemStack inSlot = contents.get(slot);
      int toRemove = Math.min(cAmt, inSlot.getCount());
      cAmt -= toRemove;
      if (!simulate) {
        handler.setStackInSlot(slot, copyStackWithSize(inSlot, inSlot.getCount() - toRemove));
      }
      if (cAmt <= 0) {
        break;
      }
    }
    return cAmt <= 0;
  }

  //Returns the amount inserted
  public static int tryPlaceItemInInventory(@Nonnull ItemStack stack, IItemHandlerModifiable handler, boolean simulate) {
    return tryPlaceItemInInventory(stack, handler, 0, handler.getSlots(), simulate);
  }

  public static int tryPlaceItemInInventory(@Nonnull ItemStack stack, IItemHandlerModifiable handler, int start, int end, boolean simulate) {
    ItemStack toAdd = stack.copy();
    if (!hasInventorySpace(toAdd, handler, start, end)) {
      return 0;
    }
    int insertedAmt = 0;
    int max = toAdd.getMaxStackSize();

    for (int i = start; i < end; i++) {
      ItemStack in = handler.getStackInSlot(i);
      if (in.isEmpty()) {
        int added = Math.min(stack.getCount(), max);
        stack.setCount(stack.getCount() - added);
        if(!simulate) {
          handler.setStackInSlot(i, copyStackWithSize(toAdd, added));
        }
        insertedAmt += added;
        if (stack.getCount() <= 0)
          return insertedAmt;
      } else {
        if (stackEqualsNonNBT(toAdd, in) && matchTags(toAdd, in)) {
          int space = max - in.getCount();
          int added = Math.min(stack.getCount(), space);
          insertedAmt += added;
          stack.setCount(stack.getCount() - added);
          if(!simulate) {
            handler.getStackInSlot(i).setCount(handler.getStackInSlot(i).getCount() + added);
          }
          if (stack.getCount() <= 0)
            return insertedAmt;
        }
      }
    }
    return insertedAmt;
  }

  public static boolean hasInventorySpace(@Nonnull ItemStack stack, IItemHandler handler, int rangeMin, int rangeMax) {
    int size = stack.getCount();
    int max = stack.getMaxStackSize();
    for (int i = rangeMin; i < rangeMax && size > 0; i++) {
      ItemStack in = handler.getStackInSlot(i);
      if (in.isEmpty()) {
        size -= max;
      } else {
        if (stackEqualsNonNBT(stack, in) && matchTags(stack, in)) {
          int space = max - in.getCount();
          size -= space;
        }
      }
    }
    return size <= 0;
  }

  public static boolean stackEqualsNonNBT(@Nonnull ItemStack stack, @Nonnull  ItemStack other) {
    if (stack.isEmpty() && other.isEmpty())
      return true;
    if (stack.isEmpty() || other.isEmpty())
      return false;
    return ItemStack.isSameItemSameComponents(stack, other);
  }

  public static boolean matchTags(@Nonnull ItemStack stack, @Nonnull  ItemStack other) {
    return ItemStack.isSameItem(stack, other);
  }

  @Nonnull
  public static ItemStack copyStackWithSize(@Nonnull ItemStack stack, int amount) {
    if (stack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
    ItemStack s = stack.copy();
    s.setCount(amount);
    return s;
  }

  public static Map<Integer, ItemStack> findItemsIndexedInInventoryFuel(IItemHandlerModifiable handler, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> stacksOut = new HashMap<>();
    for (int j = 0; j < handler.getSlots(); j++) {
      ItemStack s = handler.getStackInSlot(j);
    }
    return stacksOut;
  }

  public static Map<Integer, ItemStack> findItemsIndexedInInventoryOreDict(IItemHandlerModifiable handler, ResourceLocation oreDict, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> stacksOut = new HashMap<>();
    for (int j = 0; j < handler.getSlots(); j++) {
      ItemStack s = handler.getStackInSlot(j);
      if(s.isEmpty()) continue;
      if (s.is(TagKey.create(Registries.ITEM, oreDict)))
        stacksOut.put(j, s.copy());
    }
    return stacksOut;
  }

  public static Map<Integer, ItemStack> findItemsIndexedInInventory(IItemHandlerModifiable handler, ItemStack match, boolean strict, @Nullable CompoundTag matchNBTTag) {
    Map<Integer, ItemStack> stacksOut = new HashMap<>();
    for (int j = 0; j < handler.getSlots(); j++) {
      ItemStack s = handler.getStackInSlot(j);
      if ((strict ? matchStacks(s, match) : matchStackLoosely(s, match))) {
        stacksOut.put(j, s.copy());
      }
    }
    return stacksOut;
  }

  public static boolean matchStacks(@Nonnull ItemStack stack, @Nonnull  ItemStack other) {
    if (!ItemStack.isSameItem(stack, other)) return false;
    return ItemStack.isSameItemSameComponents(stack, other);
  }

  public static boolean matchStackLoosely(@Nonnull ItemStack stack, @Nonnull  ItemStack other) {
    if (stack.isEmpty()) return other.isEmpty();
    return ItemStack.isSameItem(stack, other);
  }
}
