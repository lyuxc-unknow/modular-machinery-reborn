package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class MiscUtils {

  public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
    return map.entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, (e) -> remapFct.apply(e.getValue())));
  }

  public static List<String> splitStringBy(String str, String spl) {
    return Lists.newArrayList(str.split(spl));
  }

  public static BlockPos rotateYCCWNorthUntil(BlockPos at, Direction dir) {
    Direction currentFacing = Direction.NORTH;
    BlockPos pos = at;
    while (currentFacing != dir) {
      currentFacing = currentFacing.getCounterClockWise(Direction.Axis.Y);
      pos = new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
    }
    return pos;
  }

  public static BlockArray rotateYCCWNorthUntil(BlockArray array, Direction dir) {
    Direction currentFacing = Direction.NORTH;
    BlockArray rot = array;
    while (currentFacing != dir) {
      currentFacing = currentFacing.getCounterClockWise(Direction.Axis.Y);
      rot = rot.rotateYCCW();
    }
    return rot;
  }

  public static BlockPos rotateYCCW(BlockPos pos) {
    return new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
  }

  public static <T> List<T> flatten(Collection<List<T>> collection) {
    return collection.stream()
      .flatMap(Collection::stream)
      .toList();
  }

  @Nullable
  public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
    for (T element : collection) {
      if(matchingFct.test(element)) {
        return element;
      }
    }
    return null;
  }

  public static long clamp(long num, long min, long max) {
    if (num < min) {
      return min;
    } else {
      return Math.min(num, max);
    }
  }

}
