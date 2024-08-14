package es.degrassi.mmreborn.common.util.nbt;

import com.mojang.serialization.JavaOps;
import es.degrassi.mmreborn.common.util.MiscUtils;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class NBTHelper {

  public static void setBlockState(CompoundTag cmp, String key, BlockState state) {
    CompoundTag serialized = getBlockStateNBTTag(state);
    if (serialized != null) {
      cmp.put(key, serialized);
    }
  }

  @Nullable
  public static BlockState getBlockState(CompoundTag cmp, String key) {
    return getBlockStateFromTag(cmp.getCompound(key));
  }

  @Nullable
  public static CompoundTag getBlockStateNBTTag(BlockState state) {
    state.getBlock().getDescriptionId();
    CompoundTag tag = new CompoundTag();
    tag.putString("registryName", state.getBlock().getDescriptionId());
    ListTag properties = new ListTag();
    for (Property<?> property : state.getProperties()) {
      CompoundTag propTag = new CompoundTag();
      try {
        propTag.putString("value", state.getValue(property).toString());
      } catch (Exception exc) {
        return null;
      }
      propTag.putString("property", property.getName());
      properties.add(propTag);
    }
    tag.put("properties", properties);
    return tag;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<T>> BlockState getBlockStateFromTag(CompoundTag cmp) {
    ResourceLocation key = ResourceLocation.parse(cmp.getString("registryName"));
    Block block = BuiltInRegistries.BLOCK.get(key);
    if(block == Blocks.AIR) return null;
    BlockState state = block.defaultBlockState();
    Collection<Property<?>> properties = state.getProperties();
    ListTag list = cmp.getList("properties", Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      CompoundTag propertyTag = list.getCompound(i);
      String valueStr = propertyTag.getString("value");
      String propertyStr = propertyTag.getString("property");
      Property<T> match = (Property<T>) MiscUtils.iterativeSearch(properties, prop -> prop.getName().equalsIgnoreCase(propertyStr));
      if(match != null) {
        try {
          Optional<T> opt = match.codec().parse(JavaOps.INSTANCE, (T) valueStr).result();
          if(opt.isPresent()) {
            state = state.setValue(match, opt.get());
          }
        } catch (Exception ignored) {}
      }
    }
    return state;
  }

}
