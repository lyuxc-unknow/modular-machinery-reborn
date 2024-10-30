package es.degrassi.mmreborn.common.util;

import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.tile.TileEntityChemicalTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class CopyHandlerHelper {

    public static BasicChemicalTank copyTank(BasicChemicalTank tank, HolderLookup.Provider provider) {
        CompoundTag cmp = tank.serializeNBT(provider);
        BasicChemicalTank newTank = (BasicChemicalTank) BasicChemicalTank.create(0, tank);
        newTank.deserializeNBT(provider, cmp);
        return newTank;
    }

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
