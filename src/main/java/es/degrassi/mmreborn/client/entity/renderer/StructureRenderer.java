package es.degrassi.mmreborn.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.IIngredient;
import es.degrassi.mmreborn.api.PartialBlockState;
import es.degrassi.mmreborn.client.util.RenderTypes;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.util.CycleTimer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class StructureRenderer {

  private final int time;
  private final long start;
  private final Function<Direction, Map<BlockPos, BlockIngredient>> blocksGetter;
  private final CycleTimer timer;

  public StructureRenderer(int time, Function<Direction, Map<BlockPos, BlockIngredient>> blocksGetter) {
    this.time = time;
    this.start = System.currentTimeMillis();
    this.blocksGetter = blocksGetter;
    AtomicInteger minCycleTime = new AtomicInteger(time);
    Map<BlockPos, BlockIngredient> map = blocksGetter.apply(Direction.NORTH);
    map.forEach((block, ingredient) -> {
      int cycleTime = time / ingredient.getAll().size();
      minCycleTime.set(Math.min(cycleTime, minCycleTime.get()));
    });
    this.timer = new CycleTimer(() -> Math.min(MMRConfig.get().blockTagCycleTime.get(), minCycleTime.get()));
  }

  public void render(PoseStack matrix, MultiBufferSource buffer, Direction direction, Level world, BlockPos machinePos) {
    Map<BlockPos, BlockIngredient> blocks = this.blocksGetter.apply(direction);
    this.timer.onDraw();
    blocks.forEach((pos, ingredient) -> {
      matrix.pushPose();
      matrix.translate(pos.getX(), pos.getY(), pos.getZ());
      if(!(pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) && ingredient != BlockIngredient.ANY) {
        PartialBlockState state = timer.get(ingredient.getAll());
        BlockPos blockPos = machinePos.offset(pos);
        if(state != null && state != PartialBlockState.ANY && !state.getBlockState().isAir()) {
          if(world.getBlockState(blockPos).isAir()) {
            matrix.translate(0.1F, 0.1F, 0.1F);
            renderTransparentBlock(state, matrix, buffer, 1f, 1f, 0.8f);
          } else if(ingredient.getAll().stream().noneMatch(test -> test.test(new BlockInWorld(world, blockPos, false)))) {
            matrix.translate(-0.0005, -0.0005, -0.0005);
            renderTransparentBlock(state, matrix, buffer, 0f, 0f, 1.001F);
          }
        }
      }
      matrix.popPose();
    });
  }

  @SuppressWarnings("deprecation")
  private void renderTransparentBlock(PartialBlockState state, PoseStack matrix, MultiBufferSource buffer, float green, float blue, float scale) {
    VertexConsumer builder = buffer.getBuffer(RenderTypes.PHANTOM);
//    matrix.translate(0.1F, 0.1F, 0.1F);
    matrix.scale(scale, scale, scale);
    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state.getBlockState());
    if(model != Minecraft.getInstance().getModelManager().getMissingModel()) {
      Arrays.stream(Direction.values())
        .flatMap(direction -> model.getQuads(state.getBlockState(), direction, RandomSource.create(42L)).stream())
        .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1f, green, blue, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
      model.getQuads(state.getBlockState(), null, RandomSource.create(42L))
        .forEach(quad -> builder.putBulkData(matrix.last(), quad, 1f, green, blue, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false));
    }
  }

  @SuppressWarnings("deprecation, unused")
  private void renderNope(PoseStack matrix, MultiBufferSource buffer) {
    VertexConsumer builder = buffer.getBuffer(RenderTypes.NOPE);
    BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ModularMachineryReborn.rl("block/nope")));
    matrix.translate(-0.0005, -0.0005, -0.0005);
    matrix.scale(1.001F, 1.001F, 1.001F);
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
