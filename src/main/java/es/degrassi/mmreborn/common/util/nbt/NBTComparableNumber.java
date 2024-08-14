package es.degrassi.mmreborn.common.util.nbt;

import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.nbt.NumericTag;

public interface NBTComparableNumber {

  boolean test(NumericTag numberTag);

  @Getter
  enum ComparisonMode {
    LESS_EQUAL("<="),
    EQUAL("=="),
    GREATER_EQUAL(">="),
    LESS("<"),
    GREATER(">");

    private final String identifier;

    ComparisonMode(String identifier) {
        this.identifier = identifier;
    }

    @Nullable
    public static ComparisonMode peekMode(String strModeAndValue) {
      lblModes:
      for (ComparisonMode mode : values()) {
        String id = mode.getIdentifier();

        char[] charArray = id.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
          char c = charArray[i];
          if (strModeAndValue.charAt(i) != c) {
            continue lblModes;
          }
        }
        return mode;
      }
      return null;
    }

    public boolean testByte(byte original, byte toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }

    public boolean testInt(int original, int toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }

    public boolean testShort(short original, short toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }

    public boolean testLong(long original, long toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }

    public boolean testFloat(float original, float toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }

    public boolean testDouble(double original, double toTest) {
      return switch (this) {
        case LESS -> toTest < original;
        case LESS_EQUAL -> toTest <= original;
        case EQUAL -> toTest == original;
        case GREATER_EQUAL -> toTest >= original;
        case GREATER -> toTest > original;
      };
    }
  }
}
