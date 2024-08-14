package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;

public class NBTComparableShort extends ShortTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableShort(ComparisonMode mode, short data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableShort copy() {
    return new NBTComparableShort(this.comparisonMode, this.getAsShort());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof ShortTag && comparisonMode.testShort(this.getAsShort(), nbtPrimitive.getAsShort());
  }

}
