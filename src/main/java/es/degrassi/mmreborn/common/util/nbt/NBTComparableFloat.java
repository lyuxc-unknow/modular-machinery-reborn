package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NumericTag;

public class NBTComparableFloat extends FloatTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableFloat(ComparisonMode mode, float data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableFloat copy() {
    return new NBTComparableFloat(this.comparisonMode, this.getAsFloat());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof FloatTag && comparisonMode.testFloat(this.getAsFloat(), nbtPrimitive.getAsFloat());
  }

}
