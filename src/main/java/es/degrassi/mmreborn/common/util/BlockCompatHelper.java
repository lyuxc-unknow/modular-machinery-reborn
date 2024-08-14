package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Iterables;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BlockCompatHelper {

  private static final ResourceLocation ic2TileBlock = ResourceLocation.fromNamespaceAndPath("ic2", "te");

  private static final Method getITeBlockIc2, getTeClassIc2, getTeBlockState, getITEgetSupportedFacings, getTEBlockId, getITeBlockIc2Class;
  private static final DirectionProperty facingPropertyField;
  private static final Field teBlockItemField;

  @Nonnull
  public static ItemStack tryGetIC2MachineStack(BlockState state, Object tile) {
    try {
      Object tileITBlock = getITeBlockIc2Class.invoke(null, tile.getClass());
      int id = (int) getTEBlockId.invoke(tileITBlock);
      if(id != -1) {
        Item i = (Item) teBlockItemField.get(state.getBlock());
        return new ItemStack(i, 1);
      }
    } catch (Throwable ignored) {}
    return ItemStack.EMPTY;
  }

  @Nonnull
  public static Tuple<BlockState, BlockEntity> transformState(BlockState state, @Nullable CompoundTag matchTag, BlockArray.TileInstantiateContext context) {
    ResourceLocation blockRes = state.getBlock().builtInRegistryHolder().key().location();
    if(ic2TileBlock.equals(blockRes) && matchTag != null) {
      Tuple<BlockState, BlockEntity> ret = tryRecoverTileState(state, matchTag, context);
      if(ret != null) {
        return ret;
      }
    }
    BlockEntity te = state.getBlock() instanceof EntityBlock ? ((EntityBlock) state.getBlock()).newBlockEntity(context.pos(), state) : null;
    if(te != null) {
      context.apply(te);
    }
    return new Tuple<>(state, te);
  }

  private static Tuple<BlockState, BlockEntity> tryRecoverTileState(BlockState state, @Nonnull CompoundTag matchTag, BlockArray.TileInstantiateContext context) {
    if(getTeClassIc2 == null || getITeBlockIc2 == null || getTeBlockState == null
      || getITEgetSupportedFacings == null || facingPropertyField == null) {
      return null;
    }

    ResourceLocation ic2TileBlock = ResourceLocation.fromNamespaceAndPath("ic2", "te");
    if(ic2TileBlock.toString().equals(state.getBlock().getDescriptionId())) {
      if(matchTag.contains("id")) {
        ResourceLocation key = ResourceLocation.parse(matchTag.getString("id"));
        if(key.getNamespace().equalsIgnoreCase("ic2")) {
          String name = key.getPath();
          try {
            Object o = getITeBlockIc2.invoke(null, name);
            Object oClazz = getTeClassIc2.invoke(o);
            if(oClazz instanceof Class) {
              BlockEntity te =  (BlockEntity) ((Class<?>) oClazz).newInstance();
              context.apply(te);
              te.loadCustomOnly(matchTag, te.getLevel().registryAccess());
              BlockState st = (BlockState) getTeBlockState.invoke(te);
              Direction applicable = Iterables.getFirst((Collection<Direction>) getITEgetSupportedFacings.invoke(o), Direction.NORTH);
              st = st.setValue(facingPropertyField, applicable);
              return new Tuple<>(st, te);
            }
          } catch (Throwable tr) {
            tr.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  static {
    Method m = null, m2 = null, m3 = null, m4 = null, m5 = null, m6 = null;
    DirectionProperty f = null;
    Field f1 = null;
    if(Mods.IC2.isPresent()) {
      try {
        Class<?> c = Class.forName("ic2.core.block.TeBlockRegistry");
        m = c.getDeclaredMethod("get", String.class);
        m6 = c.getDeclaredMethod("get", Class.class);
        c = Class.forName("ic2.core.block.ITeBlock");
        m2 = c.getDeclaredMethod("getTeClass");
        m4 = c.getDeclaredMethod("getSupportedFacings");
        c = Class.forName("ic2.core.block.state.IIdProvider");
        m5 = c.getDeclaredMethod("getId");
        m5.setAccessible(true);
        c = Class.forName("ic2.core.block.TileEntityBlock");
        m3 = c.getDeclaredMethod("getBlockState");
        c = Class.forName("ic2.core.block.BlockTileEntity");
        f1 = c.getDeclaredField("item");
        f1.setAccessible(true);
        f = (DirectionProperty) c.getDeclaredField("facingProperty").get(null);
      } catch (Throwable ignored) {}
    }
    getITeBlockIc2 = m;
    getITeBlockIc2Class = m6;
    getTeClassIc2 = m2;
    getTeBlockState = m3;
    facingPropertyField = f;
    getITEgetSupportedFacings = m4;
    getTEBlockId = m5;
    teBlockItemField = f1;
  }

}
