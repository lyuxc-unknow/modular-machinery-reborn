package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.machine.TaggedPositionBlockArray;
import es.degrassi.mmreborn.common.util.BlockArray;
import es.degrassi.mmreborn.common.util.CycleTimer;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@Getter
@Setter
public class BlockArrayRenderHelper {

  private final TaggedPositionBlockArray blocks;
  private final CycleTimer timer;
  private final long start, time = 10_000;

  public BlockArrayRenderHelper(TaggedPositionBlockArray blocks) {
    this.start = System.currentTimeMillis();
    this.timer = new CycleTimer(() -> 1000);
    this.blocks = blocks;
  }

  public void render(PoseStack pose, MultiBufferSource buffer, BlockEntity entity) {
    render(
      pose,
      buffer,
      entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING),
      entity.getLevel(),
      entity.getBlockPos()
    );
  }

  public void render(PoseStack matrix, MultiBufferSource buffer, Direction direction, Level level, BlockPos machinePos) {
    MMRLogger.INSTANCE.info("rendering structure: {}", this.blocks);
    Map<BlockPos, BlockArray.BlockInformation> blocks = this.blocks.getPattern().entrySet().stream().map(entry -> {
      Map<BlockPos, BlockArray.BlockInformation> map = new HashMap<>();
      map.put(entry.getKey(), entry.getValue());
      return map;
    }).reduce(new HashMap<>(), (acc, map) -> {
      BlockPos pos = map.keySet().stream().toList().get(0);
      acc.put(switch (direction) {
        case NORTH, UP, DOWN -> pos;
        case SOUTH -> pos.rotate(Rotation.CLOCKWISE_180);
        case WEST -> pos.rotate(Rotation.CLOCKWISE_90);
        case EAST -> pos.rotate(Rotation.COUNTERCLOCKWISE_90);
      }, map.get(pos));
      return acc;
    });
    timer.onDraw();
    blocks.forEach((pos, info) -> {
      matrix.pushPose();
      matrix.translate(pos.getX(), pos.getY(), pos.getZ());
      if (!(pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) && !info.matchingStates.isEmpty()) {
        info.matchingStates.forEach(descriptor -> {
          BlockState state = timer.get(descriptor.applicable);
          BlockPos blockPos = machinePos.offset(pos);
          if (state != null && !state.isAir()) {
            if (level.getBlockState(blockPos).isAir())
              renderTransparentBlock(state, matrix, buffer);
            else if (descriptor.applicable.stream().noneMatch(test -> test.equals(level.getBlockState(blockPos))))
              renderNope(matrix, buffer);
          }
        });
      }
      matrix.popPose();
    });
  }

  @SuppressWarnings("deprecation")
  public void renderTransparentBlock(BlockState state, PoseStack matrix, MultiBufferSource buffer) {
    VertexConsumer builder = buffer.getBuffer(RenderTypes.PHANTOM);
    matrix.translate(0.1f, 0.1f, 0.1f);
    matrix.scale(0.8f, 0.8f, 0.8f);
    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    if (model != Minecraft.getInstance().getModelManager().getMissingModel()) {
      Arrays.stream(Direction.values())
        .flatMap(direction -> model.getQuads(state, direction, RandomSource.create(42L)).stream())
        .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
      model.getQuads(state, null, RandomSource.create(42L))
        .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1f, 1f, 1f, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
    }
  }

  @SuppressWarnings("deprecation")
  public void renderNope(PoseStack matrix, MultiBufferSource buffer) {
    VertexConsumer builder = buffer.getBuffer(RenderTypes.NOPE);
    BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ModularMachineryReborn.rl("block/nope"), ""));
    matrix.translate(-0.0005, -0.0005, -0.0005);
    matrix.scale(1.001F, 1.001F, 1.001F);
    int[] light = new int[4];
    Arrays.fill(light, LightTexture.pack(15, 15));
    Arrays.stream(Direction.values())
      .flatMap(direction -> model.getQuads(null, direction, RandomSource.create(42L)).stream())
      .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1.0F, 1.0F, 1.0F, 0.8F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
    model.getQuads(null, null, RandomSource.create(42L))
      .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1.0F, 1.0F, 1.0F, 0.8F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
  }

  public boolean shouldRender() {
    return System.currentTimeMillis() < this.start + this.time;
  }
}
