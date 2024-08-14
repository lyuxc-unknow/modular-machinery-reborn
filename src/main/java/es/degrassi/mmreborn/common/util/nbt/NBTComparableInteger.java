package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;

public class NBTComparableInteger extends IntTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableInteger(ComparisonMode mode, int data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableInteger copy() {
    return new NBTComparableInteger(this.comparisonMode, this.getAsInt());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof IntTag && comparisonMode.testInt(this.getAsInt(), nbtPrimitive.getAsInt());
  }

}
