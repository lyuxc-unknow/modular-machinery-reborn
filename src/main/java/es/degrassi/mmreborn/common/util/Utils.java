package es.degrassi.mmreborn.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class Utils {
  private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,###");
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

  public static boolean isResourceNameValid(String resourceLocation) {
    try {
      ResourceLocation.tryParse(resourceLocation);
      return true;
    } catch (ResourceLocationException e) {
      return false;
    }
  }

  public static String format(int number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(int number) {
    return format(number) + "%";
  }

  public static String decimalFormat(int number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(int number) {
    return decimalFormat(number) + "%";
  }

  public static String format(long number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(long number) {
    return format(number) + "%";
  }

  public static String decimalFormat(long number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(long number) {
    return decimalFormat(number) + "%";
  }

  public static String format(double number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(double number) {
    return format(number) + "%";
  }

  public static String decimalFormat(double number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(double number) {
    return decimalFormat(number) + "%";
  }

  public static long clamp(long value, long min, long max) {
    return value < min ? min : Math.min(value, max);
  }
}
