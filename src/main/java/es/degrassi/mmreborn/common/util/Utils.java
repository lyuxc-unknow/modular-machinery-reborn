package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.IIngredient;
import es.degrassi.mmreborn.api.PartialBlockState;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Random;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Utils {

  public static final Random RAND = new Random();
  private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,###");

  public static boolean canPlayerManageMachines(Player player) {
    return player.hasPermissions(Objects.requireNonNull(player.getServer()).getOperatorUserPermissionLevel());
  }

  public static Vec3 vec3dFromBlockPos(BlockPos pos) {
    return new Vec3(pos.getX(), pos.getY(), pos.getZ());
  }

  public static AABB rotateBox(AABB box, Direction to) {
    //Based on south, positive Z
    return switch (to) {
      case EAST -> //90° CCW
        new AABB(box.minZ, box.minY, -box.minX, box.maxZ, box.maxY, -box.maxX);
      case NORTH -> //180° CCW
        new AABB(-box.minX, box.minY, -box.minZ, -box.maxX, box.maxY, -box.maxZ);
      case WEST -> //270° CCW
        new AABB(-box.minZ, box.minY, box.minX, -box.maxZ, box.maxY, box.maxX); //No changes
      default -> new AABB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    };
  }

  public static boolean isResourceNameValid(String resourceLocation) {
    try {
      ResourceLocation location = ResourceLocation.tryParse(resourceLocation);
      return true;
    } catch (ResourceLocationException e) {
      return false;
    }
  }

  public static int toInt(long l) {
    try {
      return Math.toIntExact(l);
    } catch (ArithmeticException e) {
      return Integer.MAX_VALUE;
    }
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
    return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
  }

  public static String format(int number) {
    return NUMBER_FORMAT.format(number);
  }

  public static String format(long number) {
    return NUMBER_FORMAT.format(number);
  }

  public static String format(double number) {
    return NUMBER_FORMAT.format(number);
  }

  public static MutableComponent getBlockName(IIngredient<PartialBlockState> ingredient) {
    if(ingredient.getAll().size() == 1) {
      PartialBlockState partialBlockState = ingredient.getAll().get(0);
      if(partialBlockState.getBlockState().getBlock() instanceof BlockController && partialBlockState.getNbt() != null && partialBlockState.getNbt().contains("machine", Tag.TAG_STRING)) {
        ResourceLocation machineID = ResourceLocation.tryParse(partialBlockState.getNbt().getString("machine"));
        if(machineID != null) {
          DynamicMachine machine = ModularMachineryReborn.MACHINES.get(machineID);
          if(machine != null)
            return Component.literal(machine.getLocalizedName());
        }
      }
      return partialBlockState.getName();
    }
    else return Component.literal(ingredient.toString());
  }

  public static long clamp(long value, long min, long max) {
    return value < min ? min : Math.min(value, max);
  }
}
