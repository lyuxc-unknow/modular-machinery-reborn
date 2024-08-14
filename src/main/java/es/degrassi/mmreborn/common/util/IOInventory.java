package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class IOInventory implements IItemHandlerModifiable {

  public boolean allowAnySlots = false;
  @Getter
  private final BlockEntitySynchronized owner;

  private Map<Integer, Integer> slotLimits = new HashMap<>(); //Value not present means default, aka 64.
  private Map<Integer, SlotStackHolder> inventory = new HashMap<>();
  private int[] inSlots = new int[0], outSlots = new int[0], miscSlots = new int[0];

  private InventoryUpdateListener listener = null;
  public List<Direction> accessibleSides = new ArrayList<>();

  private IOInventory(BlockEntitySynchronized owner) {
    this.owner = owner;
  }

  public IOInventory(BlockEntitySynchronized owner, int[] inSlots, int[] outSlots) {
    this(owner, inSlots, outSlots, Direction.values());
  }

  public IOInventory(BlockEntitySynchronized owner, int[] inSlots, int[] outSlots, Direction... accessibleFrom) {
    this.owner = owner;
    this.inSlots = inSlots;
    this.outSlots = outSlots;
    for (Integer slot : inSlots) {
      this.inventory.put(slot, new SlotStackHolder(slot));
    }
    for (Integer slot : outSlots) {
      this.inventory.put(slot, new SlotStackHolder(slot));
    }
    this.accessibleSides = Arrays.asList(accessibleFrom);
  }

  public IOInventory setMiscSlots(int... miscSlots) {
    this.miscSlots = miscSlots;
    for (Integer slot : miscSlots) {
      this.inventory.put(slot, new SlotStackHolder(slot));
    }
    return this;
  }

  public IOInventory setStackLimit(int limit, int... slots) {
    for (int slot : slots) {
      this.slotLimits.put(slot, limit);
    }
    return this;
  }

  public IOInventory setListener(InventoryUpdateListener listener) {
    this.listener = listener;
    return this;
  }

  public IItemHandlerModifiable asGUIAccess() {
    return new GuiAccess(this);
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    if(this.inventory.containsKey(slot)) {
      this.inventory.get(slot).itemStack = stack;
      getOwner().markForUpdate();
      if(listener != null) {
        listener.onChange();
      }
    }
  }

  @Override
  public int getSlots() {
    return inventory.size();
  }

  @Override
  public int getSlotLimit(int slot) {
    if(slotLimits.containsKey(slot)) {
      return slotLimits.get(slot);
    }
    return 64;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return true;
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    return inventory.containsKey(slot) ? inventory.get(slot).itemStack : ItemStack.EMPTY;
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if(stack.isEmpty()) return stack;
    if (!allowAnySlots) {
      if (!arrayContains(inSlots, slot)) return stack;
    }
    if (!this.inventory.containsKey(slot)) return stack; //Shouldn't happen anymore here tho

    SlotStackHolder holder = this.inventory.get(slot);
    ItemStack toInsert = copyWithSize(stack, stack.getCount());
    if(!holder.itemStack.isEmpty()) {
      ItemStack existing = copyWithSize(holder.itemStack, holder.itemStack.getCount());
      int max = Math.min(existing.getMaxStackSize(), getSlotLimit(slot));
      if (existing.getCount() >= max || !canMergeItemStacks(existing, toInsert)) {
        return stack;
      }
      int movable = Math.min(max - existing.getCount(), stack.getCount());
      if (!simulate) {
        holder.itemStack.grow(movable);
        getOwner().markForUpdate();
        if(listener != null) {
          listener.onChange();
        }
      }
      if (movable >= stack.getCount()) {
        return ItemStack.EMPTY;
      } else {
        ItemStack copy = stack.copy();
        copy.shrink(movable);
        return copy;
      }
    } else {
      int max = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
      if (max >= stack.getCount()) {
        if (!simulate) {
          holder.itemStack = stack.copy();
          getOwner().markForUpdate();
          if(listener != null) {
            listener.onChange();
          }
        }
        return ItemStack.EMPTY;
      } else {
        ItemStack copy = stack.copy();
        copy.setCount(max);
        if (!simulate) {
          holder.itemStack = copy;
          getOwner().markForUpdate();
          if(listener != null) {
            listener.onChange();
          }
        }
        copy = stack.copy();
        copy.shrink(max);
        return copy;
      }
    }
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (!allowAnySlots) {
      if (!arrayContains(outSlots, slot)) return ItemStack.EMPTY;
    }
    if (!this.inventory.containsKey(slot)) return ItemStack.EMPTY; //Shouldn't happen anymore here tho
    SlotStackHolder holder = this.inventory.get(slot);
    if(holder.itemStack.isEmpty()) return ItemStack.EMPTY;

    ItemStack extract = copyWithSize(holder.itemStack, Math.min(amount, holder.itemStack.getCount()));
    if(extract.isEmpty()) return ItemStack.EMPTY;
    if(!simulate) {
      holder.itemStack = copyWithSize(holder.itemStack, holder.itemStack.getCount() - extract.getCount());
      if(listener != null) {
        listener.onChange();
      }
    }
    getOwner().markForUpdate();
    return extract;
  }

  @Nonnull
  private ItemStack copyWithSize(@Nonnull ItemStack stack, int amount) {
    if (stack.isEmpty()|| amount <= 0) return ItemStack.EMPTY;
    ItemStack s = stack.copy();
    s.setCount(Math.min(amount, stack.getMaxStackSize()));
    return s;
  }

  private boolean arrayContains(int[] array, int i) {
    for (int id : array) {
      if(id == i) return true;
    }
    return false;
  }

  public CompoundTag writeNBT(HolderLookup.Provider pRegistries) {
    CompoundTag tag = new CompoundTag();
    tag.putIntArray("inSlots", this.inSlots);
    tag.putIntArray("outSlots", this.outSlots);
    tag.putIntArray("miscSlots", this.miscSlots);

    ListTag inv = new ListTag();
    for (Integer slot : this.inventory.keySet()) {
      SlotStackHolder holder = this.inventory.get(slot);
      CompoundTag holderTag = new CompoundTag();
      holderTag.putBoolean("holderEmpty", holder.itemStack.isEmpty());
      holderTag.putInt("holderId", slot);
      if(!holder.itemStack.isEmpty()) {
        holderTag.put("item", holder.itemStack.save(pRegistries, new CompoundTag()));
      }
      inv.add(holderTag);
    }
    tag.put("inventoryArray", inv);

    int[] sides = new int[accessibleSides.size()];
    for (int i = 0; i < accessibleSides.size(); i++) {
      Direction side = accessibleSides.get(i);
      sides[i] = side.ordinal();
    }
    tag.putIntArray("sides", sides);
    return tag;
  }

  public void readNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
    this.inSlots = tag.getIntArray("inSlots");
    this.outSlots = tag.getIntArray("outSlots");
    this.miscSlots = tag.getIntArray("miscSlots");

    this.inventory.clear();
    ListTag list = tag.getList("inventoryArray", Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      CompoundTag holderTag = list.getCompound(i);
      int slot = holderTag.getInt("holderId");
      boolean isEmpty = holderTag.getBoolean("holderEmpty");
      ItemStack stack = ItemStack.EMPTY;
      if(!isEmpty) {
        stack = ItemStack.parseOptional(pRegistries, holderTag.getCompound("item"));
      }
      this.inventory.put(slot, new SlotStackHolder(slot, stack));
    }

    int[] sides = tag.getIntArray("sides");
    for (int i : sides) {
      this.accessibleSides.add(Direction.values()[i]);
    }

    if(listener != null) {
      listener.onChange();
    }
  }

  private boolean canMergeItemStacks(@Nonnull ItemStack stack, @Nonnull ItemStack other) {
    if (stack.isEmpty() || other.isEmpty() || !stack.isStackable() || !other.isStackable()) {
      return false;
    }
    return ItemStack.isSameItem(stack, other) && ItemStack.isSameItemSameComponents(stack, other) && stack.getCount() + other.getCount() <= stack.getMaxStackSize();
  }

  public static IOInventory deserialize(BlockEntitySynchronized owner, CompoundTag tag, HolderLookup.Provider pRegistries) {
    IOInventory inv = new IOInventory(owner);
    inv.readNBT(tag, pRegistries);
    return inv;
  }

  public boolean hasCapability(Direction facing) {
    return facing == null || accessibleSides.contains(facing);
  }

  public IItemHandlerModifiable getCapability(Direction facing) {
    if(hasCapability(facing)) {
      return this;
    }
    return null;
  }

  public int calcRedstoneFromInventory() {
    int i = 0;
    float f = 0.0F;
    for (int j = 0; j < getSlots(); ++j) {
      ItemStack itemstack = getStackInSlot(j);
      if (!itemstack.isEmpty()) {
        f += (float) itemstack.getCount() / (float) Math.min(getSlotLimit(j), itemstack.getMaxStackSize());
        ++i;
      }
    }
    f = f / (float) getSlots();
    return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);

  }

  public static IOInventory mergeBuild(BlockEntitySynchronized tile, IOInventory... inventories) {
    IOInventory merged = new IOInventory(tile);
    int slotOffset = 0;
    for (IOInventory inventory : inventories) {
      for (Integer key : inventory.inventory.keySet()) {
        merged.inventory.put(key + slotOffset, inventory.inventory.get(key));
      }
      for (Integer key : inventory.slotLimits.keySet()) {
        merged.slotLimits.put(key + slotOffset, inventory.slotLimits.get(key));
      }
      slotOffset += inventory.inventory.size();
    }
    return merged;
  }

  private static class SlotStackHolder {

    private final int slotId;
    @Nonnull
    private ItemStack itemStack = ItemStack.EMPTY;

    private SlotStackHolder(int slotId) {
      this.slotId = slotId;
    }
    private SlotStackHolder(int slotId, ItemStack stack) {
      this.slotId = slotId;
      this.itemStack = stack;
    }

  }

  public static class GuiAccess implements IItemHandlerModifiable {

    private final IOInventory inventory;

    public GuiAccess(IOInventory inventory) {
      this.inventory = inventory;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
      return inventory.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
      return inventory.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      boolean allowPrev = inventory.allowAnySlots;
      inventory.allowAnySlots = true;
      ItemStack insert = inventory.insertItem(slot, stack, simulate);
      inventory.allowAnySlots = allowPrev;
      return insert;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
      boolean allowPrev = inventory.allowAnySlots;
      inventory.allowAnySlots = true;
      ItemStack extract = inventory.extractItem(slot, amount, simulate);
      inventory.allowAnySlots = allowPrev;
      return extract;
    }

    @Override
    public int getSlotLimit(int slot) {
      return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      return true;
    }
  }

}
