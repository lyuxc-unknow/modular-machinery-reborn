package es.degrassi.mmreborn.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ControllerRenderer implements BlockEntityRenderer<MachineControllerEntity> {
  public static final Map<BlockPos, StructureRenderer> renderers = new HashMap<>();

  public ControllerRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(MachineControllerEntity machineControllerEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
    if (machineControllerEntity.getLevel() == null) return;

    if (renderers.containsKey(machineControllerEntity.getBlockPos())) {
      Direction machineFacing = machineControllerEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
      StructureRenderer renderer = renderers.get(machineControllerEntity.getBlockPos());
      if (renderer.shouldRender()) {
        renderer.render(poseStack, multiBufferSource, machineFacing, machineControllerEntity.getLevel(), machineControllerEntity.getBlockPos());
      } else {
        renderers.remove(machineControllerEntity.getBlockPos());
      }
    }
  }

  public static void add(DynamicMachine machine, BlockPos controllerPos) {
    renderers.put(controllerPos, new StructureRenderer(MMRConfig.get().general.structureRenderTime, machine.getPattern()::getBlocks));
  }
}
