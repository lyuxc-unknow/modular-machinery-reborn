package es.degrassi.mmreborn.api;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import es.degrassi.mmreborn.common.registration.BlockRegistration;
import lombok.Getter;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class PartialBlockState implements Predicate<BlockInWorld> {

  public static final PartialBlockState AIR = new PartialBlockState(Blocks.AIR.defaultBlockState(), Collections.emptyList(), null);
  public static final PartialBlockState ANY = new PartialBlockState(Blocks.AIR.defaultBlockState(), Collections.emptyList(), null) {
    @Override
    public boolean test(BlockInWorld cachedBlockInfo) {
      return true;
    }

    @Override
    public String toString() {
      return "ANY";
    }
  };

  public static final PartialBlockState MACHINE = new PartialBlockState(BlockRegistration.CONTROLLER.get().defaultBlockState(), Collections.emptyList(), null) {
    @Override
    public boolean test(BlockInWorld cachedBlockInfo) {
      return cachedBlockInfo.getState().getBlock() instanceof BlockController || cachedBlockInfo.getEntity() instanceof MachineControllerEntity;
    }

    @Override
    public String toString() {
      return "MACHINE";
    }
  };

  public static final NamedCodec<PartialBlockState> CODEC = NamedCodec.STRING.comapFlatMap(s -> {
    StringReader reader = new StringReader(s);
    try {
      BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, true);
      return DataResult.success(new PartialBlockState(result.blockState(), Lists.newArrayList(result.properties().keySet()), result.nbt()));
    } catch (CommandSyntaxException exception) {
      return DataResult.error(exception::getMessage);
    }
  }, PartialBlockState::toString, "Partial block state");

  public static final NamedCodec<List<PartialBlockState>> CODEC_LIST = CODEC.listOf();

  @Getter
  private final BlockState blockState;
  private final List<Property<?>> properties;
  @Getter
  private final CompoundTag nbt;

  public PartialBlockState(BlockState blockState, List<Property<?>> properties, CompoundTag nbt) {
    this.blockState = blockState;
    this.properties = properties;
    this.nbt = nbt;
  }

  public PartialBlockState copy() {
    return new PartialBlockState(blockState, properties, nbt);
  }

  public PartialBlockState(Block block) {
    this(block.defaultBlockState(), new ArrayList<>(), null);
  }

  public List<String> getProperties() {
    return this.properties.stream().map(property -> property.getName() + "=" + this.blockState.getValue(property)).toList();
  }

  public PartialBlockState rotate(Rotation rotation) {
    if(this.properties.contains(BlockStateProperties.HORIZONTAL_FACING) && this.blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && !(this.blockState.getBlock() instanceof BlockController)) {
      Direction direction = this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
      direction = rotation.rotate(direction);
      BlockState blockState = this.blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
      List<Property<?>> properties = Lists.newArrayList(this.properties);
      if(!properties.contains(BlockStateProperties.HORIZONTAL_FACING))
        properties.add(BlockStateProperties.HORIZONTAL_FACING);
      return new PartialBlockState(blockState, properties, this.nbt);
    } else if(this.properties.contains(BlockStateProperties.FACING) && this.blockState.hasProperty(BlockStateProperties.FACING) && !(this.blockState.getBlock() instanceof BlockController)) {
      Direction direction = this.blockState.getValue(BlockStateProperties.FACING);
      if(direction.getAxis() == Direction.Axis.Y)
        return this;
      direction = rotation.rotate(direction);
      BlockState blockState = this.blockState.setValue(BlockStateProperties.FACING, direction);
      List<Property<?>> properties = Lists.newArrayList(this.properties);
      if(!properties.contains(BlockStateProperties.FACING))
        properties.add(BlockStateProperties.FACING);
      return new PartialBlockState(blockState, properties, this.nbt);
    }
    return this;
  }

  @Override
  public boolean test(BlockInWorld cachedBlockInfo) {
    BlockState blockstate = cachedBlockInfo.getState();
    if (!blockstate.is(this.blockState.getBlock())) {
      return false;
    } else {
      for(Property<?> property : this.properties) {
        if (blockstate.getValue(property) != this.blockState.getValue(property)) {
          return false;
        }
      }

      if (this.nbt == null) {
        return true;
      } else {
        BlockEntity tileentity = cachedBlockInfo.getEntity();
        return tileentity != null && NbtUtils.compareNbt(this.nbt, tileentity.saveWithFullMetadata(cachedBlockInfo.getLevel().registryAccess()), true);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(BuiltInRegistries.BLOCK.getKey(this.blockState.getBlock()));
    if(!this.properties.isEmpty())
      builder.append("[");
    Iterator<Property<?>> iterator = this.properties.iterator();
    while (iterator.hasNext()) {
      Property<?> property = iterator.next();
      Comparable<?> value = this.blockState.getValue(property);
      builder.append(property.getName());
      builder.append("=");
      builder.append(value);
      if(iterator.hasNext())
        builder.append(",");
      else
        builder.append("]");
    }

    if(this.nbt != null && !this.nbt.isEmpty())
      builder.append(this.nbt);
    return builder.toString();
  }

  public MutableComponent getName() {
    return this.blockState.getBlock().getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PartialBlockState other)) return false;
    if(this.blockState != other.blockState)
      return false;
    if(!new HashSet<>(this.properties).containsAll(other.properties) || !new HashSet<>(other.properties).containsAll(this.properties))
      return false;
    return NbtUtils.compareNbt(this.nbt, other.nbt, true);
  }

  public PartialBlockState copyWithRotation(Rotation rotation) {
    return rotate(rotation).copy();
  }
}
