package es.degrassi.mmreborn.common.util.nbt;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;

public class NBTComparableByte extends ByteTag implements NBTComparableNumber {

  private final ComparisonMode comparisonMode;

  public NBTComparableByte(ComparisonMode mode, byte data) {
    super(data);
    this.comparisonMode = mode;
  }

  @Override
  public NBTComparableByte copy() {
    return new NBTComparableByte(this.comparisonMode, this.getAsByte());
  }

  @Override
  public boolean test(NumericTag nbtPrimitive) {
    return nbtPrimitive instanceof ByteTag && comparisonMode.testByte(this.getAsByte(), nbtPrimitive.getAsByte());
  }
}
