package es.degrassi.mmreborn.common.machine;

import com.mojang.serialization.DataResult;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentSelectorTag;
import es.degrassi.mmreborn.common.util.BlockArray;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class TaggedPositionBlockArray extends BlockArray {
  public static final NamedCodec<TaggedPositionBlockArray> CODEC = NamedCodec.list(NamedCodec.<TaggedPositionArrayElement>record(instance -> instance.group(
    NamedCodec.of(BlockPos.CODEC).fieldOf("pos").forGetter(element -> element.pos),
    NamedCodec.list(BlockStateDescriptor.CODEC).fieldOf("elements").forGetter(element -> element.info.matchingStates)
  ).apply(instance, (pos, elements) -> new TaggedPositionArrayElement(pos, new BlockInformation(elements))), "TaggedPositionArrayElement"), "TaggedPositionArrayElement list").comapFlatMap((r) -> {
    TaggedPositionBlockArray array = new TaggedPositionBlockArray();
    r.forEach(element -> array.addBlock(element.pos, element.info));
    return DataResult.success(array);
  }, r -> r.pattern.entrySet().stream().map((entry) -> new TaggedPositionArrayElement(entry.getKey(), entry.getValue())).toList(), "TaggedPositionArray");

  private record TaggedPositionArrayElement(BlockPos pos, BlockInformation info) {}

  private final Map<BlockPos, ComponentSelectorTag> taggedPositions = new HashMap<>();

  public void setTag(BlockPos pos, ComponentSelectorTag tag) {
    this.taggedPositions.put(pos, tag);
  }

  @Nullable
  public ComponentSelectorTag getTag(BlockPos pos) {
    return this.taggedPositions.get(pos);
  }

  @Override
  public TaggedPositionBlockArray rotateYCCW() {
    TaggedPositionBlockArray out = new TaggedPositionBlockArray();

    for (BlockPos pos : pattern.keySet()) {
      BlockInformation info = pattern.get(pos);
      out.pattern.put(new BlockPos(pos.getZ(), pos.getY(), -pos.getX()), info.copyRotateYCCW());
    }
    for (BlockPos pos : taggedPositions.keySet()) {
      out.taggedPositions.put(new BlockPos(pos.getZ(), pos.getY(), -pos.getX()), taggedPositions.get(pos));
    }
    return out;
  }

  @Override
  public String toString() {
    return "TaggedPositionBlockArray{" +
      "\ntaggedPositions=" + taggedPositions +
      "\n, pattern=" + pattern +
      "\n}";
  }
}
