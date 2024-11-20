package es.degrassi.mmreborn.common.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CopyHandlerHelper {

    public static HybridTank copyTank(HybridTank tank, HolderLookup.Provider provider) {
        CompoundTag cmp = new CompoundTag();
        tank.writeToNBT(provider, cmp);
        HybridTank newTank = new HybridTank(tank.getCapacity());
        newTank.readFromNBT(provider, cmp);
        return newTank;
    }

    public static IOInventory copyInventory(IOInventory inventory, HolderLookup.Provider pRegistries) {
        return IOInventory.deserialize(inventory.getOwner(), inventory.writeNBT(pRegistries), pRegistries);
    }
}
