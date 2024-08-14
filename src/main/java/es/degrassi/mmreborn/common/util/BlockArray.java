package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.client.ClientScheduler;
import es.degrassi.mmreborn.common.util.nbt.NBTJsonSerializer;
import es.degrassi.mmreborn.common.util.nbt.NBTMatchingHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

@Getter
public class BlockArray {
  private static final ResourceLocation ic2TileBlock = ResourceLocation.fromNamespaceAndPath("ic2", "te");
  protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();
  private Vec3i min = new Vec3i(0, 0, 0), max = new Vec3i(0, 0, 0), size = new Vec3i(0, 0, 0);

  public BlockArray() {}

  public BlockArray(BlockArray other) {
    this.pattern = new HashMap<>(other.pattern);
    this.min = new Vec3i(other.min.getX(), other.min.getY(), other.min.getZ());
    this.max = new Vec3i(other.max.getX(), other.max.getY(), other.max.getZ());
    this.size = new Vec3i(other.size.getX(), other.size.getY(), other.size.getZ());
  }

  public BlockArray(BlockArray other, Vec3i offset) {
    for (Map.Entry<BlockPos, BlockInformation> otherEntry : other.pattern.entrySet()) {
      this.pattern.put(otherEntry.getKey().offset(offset), otherEntry.getValue());
    }
    this.min  = new Vec3i(offset.getX() + other.min.getX(),  offset.getY() + other.min.getY(),  offset.getZ() + other.min.getZ());
    this.max  = new Vec3i(offset.getX() + other.max.getX(),  offset.getY() + other.max.getY(),  offset.getZ() + other.max.getZ());
    this.size = new Vec3i(other.size.getX(), other.size.getY(), other.size.getZ());
  }

  public void addBlock(int x, int y, int z, @Nonnull BlockInformation info) {
    addBlock(new BlockPos(x, y, z), info);
  }

  public void addBlock(BlockPos offset, @Nonnull BlockInformation info) {
    pattern.put(offset, info);
    updateSize(offset);
  }

  public boolean hasBlockAt(BlockPos pos) {
    return pattern.containsKey(pos);
  }

  public boolean isEmpty() {
    return pattern.isEmpty();
  }

  private void updateSize(BlockPos addedPos) {
    if(addedPos.getX() < min.getX()) {
      min = new Vec3i(addedPos.getX(), min.getY(), min.getZ());
    }
    if(addedPos.getX() > max.getX()) {
      max = new Vec3i(addedPos.getX(), max.getY(), max.getZ());
    }
    if(addedPos.getY() < min.getY()) {
      min = new Vec3i(min.getX(), addedPos.getY(), min.getZ());
    }
    if(addedPos.getY() > max.getY()) {
      max = new Vec3i(max.getX(), addedPos.getY(), max.getZ());
    }
    if(addedPos.getZ() < min.getZ()) {
      min = new Vec3i(min.getX(), min.getY(), addedPos.getZ());
    }
    if(addedPos.getZ() > max.getZ()) {
      max = new Vec3i(max.getX(), max.getY(), addedPos.getZ());
    }
    size = new Vec3i(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1);
  }

  public Map<BlockPos, BlockInformation> getPatternSlice(int slice) {
    Map<BlockPos, BlockInformation> copy = new HashMap<>();
    for (BlockPos pos : pattern.keySet()) {
      if(pos.getY() == slice) {
        copy.put(pos, pattern.get(pos));
      }
    }
    return copy;
  }

  @OnlyIn(Dist.CLIENT)
  public List<ItemStack> getAsDescriptiveStacks() {
    return getAsDescriptiveStacks(Optional.empty());
  }

  @OnlyIn(Dist.CLIENT)
  public List<ItemStack> getAsDescriptiveStacks(Optional<Long> snapSample) {
    List<ItemStack> out = new LinkedList<>();
    for (Map.Entry<BlockPos, BlockInformation> infoEntry : pattern.entrySet()) {
      BlockArray.BlockInformation bi = infoEntry.getValue();
      ItemStack s = bi.getDescriptiveStack(snapSample);

      if(!s.isEmpty()) {
        boolean found = false;
        for (ItemStack stack : out) {
          if(stack.getItem().getDescriptionId().equals(s.getItem().getDescriptionId()) && stack.getDamageValue() == s.getDamageValue()) {
            stack.setCount(stack.getCount() + 1);
            found = true;
            break;
          }
        }
        if(!found) {
          out.add(s);
        }
      }
    }
    return out;
  }

  public boolean matches(Level world, BlockPos center, boolean oldState, @Nullable Map<BlockPos, List<BlockInformation>> modifierReplacementPattern) {
    lblPattern:
    for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
      BlockPos at = center.offset(entry.getKey());
      if(!entry.getValue().matches(world, at, oldState)) {
        if(modifierReplacementPattern != null && modifierReplacementPattern.containsKey(entry.getKey())) {
          for (BlockInformation info : modifierReplacementPattern.get(entry.getKey())) {
            if (info.matches(world, at, oldState)) {
              continue lblPattern;
            }
          }
        }
        return false;
      }
    }
    return true;
  }

  public BlockPos getRelativeMismatchPosition(Level world, BlockPos center, @Nullable Map<BlockPos, List<BlockInformation>> modifierReplacementPattern) {
    for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
      BlockPos at = center.offset(entry.getKey());
      if(!entry.getValue().matches(world, at, false)) {
        if(modifierReplacementPattern != null && modifierReplacementPattern.containsKey(entry.getKey())) {
          for (BlockInformation info : modifierReplacementPattern.get(entry.getKey())) {
            if (info.matches(world, at, false)) {
              continue;
            }
          }
        }
        return entry.getKey();
      }
    }
    return null;
  }

  public BlockArray rotateYCCW() {
    BlockArray out = new BlockArray();

    for (BlockPos pos : pattern.keySet()) {
      BlockInformation info = pattern.get(pos);
      out.pattern.put(MiscUtils.rotateYCCW(pos), info.copyRotateYCCW());
    }
    return out;
  }

  public String serializeAsMachineJson() {
    String newline = System.getProperty("line.separator");
    String move = "    ";

    StringBuilder sb = new StringBuilder();
    sb.append("{").append(newline);
    sb.append(move).append("\"parts\": [").append(newline);

    for (Iterator<BlockPos> iterator = this.pattern.keySet().iterator(); iterator.hasNext(); ) {
      BlockPos pos = iterator.next();
      sb.append(move).append(move).append("{").append(newline);

      sb.append(move).append(move).append(move).append("\"x\": ").append(pos.getX()).append(",").append(newline);
      sb.append(move).append(move).append(move).append("\"y\": ").append(pos.getY()).append(",").append(newline);
      sb.append(move).append(move).append(move).append("\"z\": ").append(pos.getZ()).append(",").append(newline);

      BlockInformation bi = this.pattern.get(pos);
      if(bi.matchingTag != null) {
        String strTag = NBTJsonSerializer.serializeNBT(bi.matchingTag);
        sb.append(move).append(move).append(move).append("\"nbt\": ").append(strTag).append(",").append(newline);
      }

      sb.append(move).append(move).append(move).append("\"elements\": [").append(newline);
      for (Iterator<BlockState> iterator1 = bi.samples.iterator(); iterator1.hasNext(); ) {
        BlockState descriptor = iterator1.next();

        int meta = descriptor.getBlock().hashCode();
        String str = descriptor.getBlock().getDescriptionId()+ "@" + meta;
        sb.append(move).append(move).append(move).append(move).append("\"").append(str).append("\"");

        if(iterator1.hasNext()) {
          sb.append(",");
        }
        sb.append(newline);
      }

      sb.append(move).append(move).append(move).append("]").append(newline);
      sb.append(move).append(move).append("}");
      if(iterator.hasNext()) {
        sb.append(",");
      }
      sb.append(newline);
    }

    sb.append(move).append("]");
    sb.append("}");
    return sb.toString();
  }

  @Override
  public String toString() {
    return "BlockArray{\n" + serializeAsMachineJson() + "\n}";
  }

  public static class BlockInformation {
    public static final int CYCLE_TICK_SPEED = 30;
    public List<BlockStateDescriptor> matchingStates;
    private List<BlockState> samples = Lists.newLinkedList();
    public CompoundTag matchingTag = null;

    public BlockInformation(List<BlockStateDescriptor> matching) {
      this.matchingStates = Lists.newLinkedList(matching);
      for (BlockStateDescriptor desc : matchingStates) {
        samples.addAll(desc.applicable);
      }
    }

    public void setMatchingTag(@Nullable CompoundTag matchingTag) {
      this.matchingTag = matchingTag;
    }

    public BlockState getSampleState() {
      return getSampleState(Optional.empty());
    }

    public BlockState getSampleState(Optional<Long> snapTick) {
      int tickSpeed = CYCLE_TICK_SPEED;
      if(samples.size() > 10) {
        tickSpeed *= 0.6;
      }
      int p = (int) (snapTick.orElse(ClientScheduler.getClientTick()) / tickSpeed);
      int part = p % samples.size();
      return samples.get(part);
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getDescriptiveStack(Optional<Long> snapTick) {
      BlockState state = getSampleState(snapTick);

      Tuple<BlockState, BlockEntity> recovered = BlockCompatHelper.transformState(state, this.matchingTag,
        new BlockArray.TileInstantiateContext(Minecraft.getInstance().level, BlockPos.ZERO));
      state = recovered.getA();
      Block type = state.getBlock();
      ItemStack s = ItemStack.EMPTY;

      try {
        if(ic2TileBlock.toString().equals(type.getDescriptionId())) {
          s = BlockCompatHelper.tryGetIC2MachineStack(state, recovered.getB());
        } else {
//          s = state.getBlock().getPickBlock(state, null, null, BlockPos.ZERO, null);
        }
      } catch (Exception exc) {}

      if(s.isEmpty()) {
        if(type instanceof LiquidBlock liquid) {
          s = FluidUtil.getFilledBucket(new FluidStack(liquid.fluid.getSource(), 1000));
        } else {
          Fluid m = state.getFluidState().getType();
          if(m == Fluids.LAVA) {
            s = new ItemStack(Items.LAVA_BUCKET);
          } else if(m == Fluids.WATER) {
            s = new ItemStack(Items.WATER_BUCKET);
          } else {
            s = ItemStack.EMPTY;
          }
        }
      }
      return s;
    }

    public static BlockStateDescriptor getDescriptor(String strElement) throws JsonParseException {
      int meta = -1;
      int indexMeta = strElement.indexOf('@');
      if(indexMeta != -1 && indexMeta != strElement.length() - 1) {
        try {
          meta = Integer.parseInt(strElement.substring(indexMeta + 1));
        } catch (NumberFormatException exc) {
          throw new JsonParseException("Expected a metadata number, got " + strElement.substring(indexMeta + 1), exc);
        }
        strElement = strElement.substring(0, indexMeta);
      }
      ResourceLocation res = ResourceLocation.parse(strElement);
      Block b = BuiltInRegistries.BLOCK.get(res);
      if(b == Blocks.AIR) {
        throw new JsonParseException("Couldn't find block with registryName '" + res.toString() + "' !");
      }
      if(meta == -1) {
        return new BlockStateDescriptor(b);
      } else {
        return new BlockStateDescriptor(b);
      }
    }

    public BlockInformation copyRotateYCCW() {
      List<BlockStateDescriptor> newDescriptors = new ArrayList<>(this.matchingStates.size());
      for (BlockStateDescriptor desc : this.matchingStates) {
        BlockStateDescriptor copy = new BlockStateDescriptor();
        for (BlockState applicableState : desc.applicable) {
          copy.applicable.add(applicableState.rotate(Rotation.COUNTERCLOCKWISE_90));
        }
        newDescriptors.add(copy);
      }
      BlockInformation bi =  new BlockInformation(newDescriptors);
      if(this.matchingTag != null) {
        bi.matchingTag = this.matchingTag;
      }
      return bi;
    }

    public BlockInformation copy() {
      List<BlockStateDescriptor> descr = new ArrayList<>(this.matchingStates.size());
      for (BlockStateDescriptor desc : this.matchingStates) {
        BlockStateDescriptor copy = new BlockStateDescriptor();
        copy.applicable.addAll(desc.applicable);
        descr.add(copy);
      }
      BlockInformation bi =  new BlockInformation(descr);
      if(this.matchingTag != null) {
        bi.matchingTag = this.matchingTag;
      }
      return bi;
    }

    public boolean matchesState(BlockState state) {
      for (BlockStateDescriptor descriptor : matchingStates) {
        for (BlockState applicable : descriptor.applicable) {
          Block block = applicable.getBlock();
          if(block.equals(state.getBlock()) && applicable.equals(state)) {
            return true;
          }
        }
      }
      return false;
    }

    public boolean matches(Level world, BlockPos at, boolean default_) {
      if(!world.isLoaded(at)) {
        return default_;
      }

      if(matchingTag != null) {
        BlockEntity te = world.getBlockEntity(at);
        if(te != null && matchingTag.sizeInBytes() > 0) {
          CompoundTag cmp = te.saveWithFullMetadata(world.registryAccess());
          if(!NBTMatchingHelper.matchNBTCompound(matchingTag, cmp)) {
            return false; //No match at this position.
          }
        }
      }

      BlockState state = world.getBlockState(at);
      for (BlockStateDescriptor descriptor : matchingStates) {
        for (BlockState applicable : descriptor.applicable) {
          Block block = applicable.getBlock();
          if(block.equals(state.getBlock()) && applicable.equals(state)) {
            return true;
          }
        }
      }
      return false;
    }

  }

  public static class BlockStateDescriptor {
    public static final NamedCodec<BlockStateDescriptor> CODEC = NamedCodec.STRING.comapFlatMap(s -> {
      StringReader reader = new StringReader(s);
      try {
        BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, true);
        return DataResult.success(new BlockStateDescriptor(result.blockState()));
      } catch (CommandSyntaxException exception) {
        return DataResult.error(exception::getMessage);
      }
    }, BlockStateDescriptor::toString, "BlockStateDescriptor");
    public final List<BlockState> applicable = Lists.newArrayList();

    private BlockStateDescriptor() {}

    private BlockStateDescriptor(Block block) {
      this.applicable.addAll(block.getStateDefinition().getPossibleStates());
      if(applicable.isEmpty()) {
        applicable.add(block.defaultBlockState());
      }
    }

    public BlockStateDescriptor(BlockState state) {
      this.applicable.add(state);
    }

  }

  public record TileInstantiateContext(Level level, BlockPos pos) {
    public void apply(BlockEntity te) {
      if (te != null) {
        te.setLevel(level);
      }
    }
  }
}
