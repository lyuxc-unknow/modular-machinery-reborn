package es.degrassi.mmreborn.common.util.nbt;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NBTMatchingHelper {

    public static boolean matchNBTCompound(@Nullable CompoundTag matchNBT, @Nullable CompoundTag itemStackNBTToCheck) {
        if(matchNBT == null) {
            return true;
        }
        if(itemStackNBTToCheck == null) {
            return matchNBT.getAllKeys().isEmpty();
        }
        return matchCompound(matchNBT, itemStackNBTToCheck);
    }

    private static boolean matchBase(Tag matchBase, Tag matchStack) {
        if(matchBase instanceof NBTComparableNumber) {
            return (matchStack instanceof NumericTag) &&
                    ((NBTComparableNumber) matchBase).test((NumericTag) matchStack);
        } else if(matchBase instanceof NBTPatternString) {
            return (matchStack instanceof StringTag) &&
                    ((NBTPatternString) matchBase).testString(matchStack.getAsString());
        } else if(matchBase instanceof CompoundTag) {
            return (matchStack instanceof CompoundTag) &&
                    matchCompound((CompoundTag) matchBase, (CompoundTag) matchStack);
        } else if(matchBase instanceof ListTag) {
            return (matchStack instanceof ListTag) &&
                    matchList((ListTag) matchBase, (ListTag) matchStack);
        } else if(matchBase instanceof ByteArrayTag) {
            return (matchStack instanceof ByteArrayTag) &&
                    Arrays.equals(((ByteArrayTag) matchBase).getAsByteArray(), ((ByteArrayTag) matchStack).getAsByteArray());
        } else if(matchBase instanceof IntArrayTag) {
            return (matchStack instanceof IntArrayTag) &&
                    Arrays.equals(((IntArrayTag) matchBase).getAsIntArray(), ((IntArrayTag) matchStack).getAsIntArray());
        }
        return matchBase.equals(matchStack);
    }

    private static boolean matchCompound(CompoundTag matchNBT, CompoundTag itemStackNBTToCheck) {
        for (String keyMatch : matchNBT.getAllKeys()) {
            if(itemStackNBTToCheck.contains(keyMatch)) {
                Tag baseOriginal = matchNBT.get(keyMatch);
                Tag baseStack = itemStackNBTToCheck.get(keyMatch);
                if(!matchBase(baseOriginal, baseStack)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean matchList(ListTag baseOriginal, ListTag baseStack) {
        if(baseOriginal.isEmpty()) {
            return true;
        }
        if(baseStack.isEmpty()) {
            return false;
        }

        if(baseOriginal.type != baseStack.type) {
            return false;
        }

        List<Tag> copyConsumeTags = Lists.newArrayList(baseStack);
        lblSearchTags:
        for (Tag entryBase : baseOriginal) {
            for (Tag stackBase : copyConsumeTags) {
                if (matchBase(entryBase, stackBase)) {
                    copyConsumeTags.remove(stackBase);
                    continue lblSearchTags;
                }
            }
            return false;
        }
        return true;
    }

}
