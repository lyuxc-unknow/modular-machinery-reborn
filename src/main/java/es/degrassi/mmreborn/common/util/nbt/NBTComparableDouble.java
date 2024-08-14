package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.NumericTag;

public class NBTComparableDouble extends DoubleTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableDouble(ComparisonMode mode, double data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableDouble copy() {
    return new NBTComparableDouble(this.comparisonMode, this.getAsDouble());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof DoubleTag && comparisonMode.testDouble(this.getAsDouble(), nbtPrimitive.getAsDouble());
  }

}
