package es.degrassi.mmreborn.common.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class CopyHandlerHelper {

    public static HybridTank copyTank(HybridTank tank, HolderLookup.Provider provider) {
        CompoundTag cmp = new CompoundTag();
        tank.writeToNBT(provider, cmp);
//        if(Mods.MEKANISM.isPresent()) {
//            writeGasTag(tank, cmp);
//        }
        HybridTank newTank = new HybridTank(tank.getCapacity());
//        if(Mods.MEKANISM.isPresent()) {
//            newTank = buildMekGasTank(tank.getCapacity());
//        }
        newTank.readFromNBT(provider, cmp);
//        if(Mods.MEKANISM.isPresent()) {
//            readGasTag(newTank, cmp);
//        }
        return newTank;
    }

//    @Optional.Method(modid = "mekanism")
//    private static HybridTank buildMekGasTank(int capacity) {
//        return new HybridGasTank(capacity);
//    }

//    @Optional.Method(modid = "mekanism")
//    private static void writeGasTag(HybridTank tank, NBTTagCompound compound) {
//        if(tank instanceof HybridGasTank) {
//            ((HybridGasTank) tank).writeGasToNBT(compound);
//        }
//    }
//
//    @Optional.Method(modid = "mekanism")
//    private static void readGasTag(HybridTank tank, NBTTagCompound compound) {
//        if(tank instanceof HybridGasTank) {
//            ((HybridGasTank) tank).readGasFromNBT(compound);
//        }
//    }

    public static IOInventory copyInventory(IOInventory inventory, HolderLookup.Provider pRegistries) {
        return IOInventory.deserialize(inventory.getOwner(), inventory.writeNBT(pRegistries), pRegistries);
    }

}
