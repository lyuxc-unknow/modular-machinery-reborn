package es.degrassi.mmreborn.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class FuelItemHelper {

  private static List<ItemStack> knownFuelStacks = null;

  public static void initialize() {
    List<ItemStack> out = new LinkedList<>();
    for (Item i : BuiltInRegistries.ITEM) {
      ItemStack stack = new ItemStack(i);
      try {
        int burn = stack.getBurnTime(RecipeType.SMELTING);
        if (burn > 0 && !out.contains(stack)) out.add(stack);
        burn = stack.getBurnTime(RecipeType.BLASTING);
        if (burn > 0 && !out.contains(stack)) out.add(stack);
        burn = stack.getBurnTime(RecipeType.SMOKING);
        if (burn > 0 && !out.contains(stack)) out.add(stack);
      } catch (Exception ignored) {}
    }
    knownFuelStacks = ImmutableList.copyOf(out);
  }

  public static List<ItemStack> getFuelItems() {
    if(knownFuelStacks == null) {
      return Lists.newArrayList();
    }
    return knownFuelStacks;
  }

}
