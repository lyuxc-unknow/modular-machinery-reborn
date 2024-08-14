package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;

public class NBTComparableLong extends LongTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableLong(ComparisonMode mode, long data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableLong copy() {
    return new NBTComparableLong(this.comparisonMode, this.getAsLong());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof LongTag && comparisonMode.testLong(this.getAsLong(), nbtPrimitive.getAsLong());
  }

}
