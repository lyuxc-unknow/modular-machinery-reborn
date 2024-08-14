package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.BlockArray;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@Getter
public class BlockArrayPreviewRenderHelper {

//    private BlockArrayRenderHelper renderHelper = null;
    private BlockArray matchArray = null;
    private Vec3i renderHelperOffset = null;
    private DynamicMachine machine = null;
    private BlockState machineState = null;

    private BlockPos attachedPosition = null;

    private int renderedLayer = -1;

    private static int hash = -1;
    private static int batchDList = -1;

    public boolean startPreview(DynamicMachineRenderContext currentContext) {
        if(currentContext.getShiftSnap() != -1) {
//            this.renderHelper = currentContext.getRender();
//            this.matchArray = this.renderHelper.getBlocks();
//            this.renderHelper.sampleSnap = currentContext.getShiftSnap(); //Just for good measure
            this.renderHelperOffset = currentContext.getMoveOffset();
            this.machine = currentContext.getDisplayedMachine();
            this.machineState = currentContext.getMachineState();
            this.attachedPosition = null;
            if(Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().player.sendMessage(new TextComponentTranslation("gui.blueprint.popout.place"));
            }
            return true;
        }
        return false;
    }

    public boolean placePreview(PoseStack pose, MultiBufferSource buffer, BlockPos machinePos) {
        Player player = Minecraft.getInstance().player;

//        if(player != null && this.renderHelper != null && machineState != null && this.attachedPosition == null) {
//            BlockPos moveDir = MiscUtils.rotateYCCWNorthUntil(new BlockPos(this.renderHelperOffset), rotate);
//                attachPos = attachPos.subtract(moveDir);
//            this.matchArray = MiscUtils.rotateYCCWNorthUntil(this.matchArray, rotate);
//            renderHelper.render(pose, buffer, machineState.getValue(BlockController.FACING), Minecraft.getInstance().level, machinePos);
//            attachedPosition = machinePos;
//            updateLayers();
//            return true;
//        }
        return false;
    }

    public void tick() {
//        if(attachedPosition != null) {
//            if(Minecraft.getInstance().player != null &&
//                    attachedPosition.distToCenterSqr(Minecraft.getInstance().player.blockPosition().getX(), Minecraft.getInstance().player.blockPosition().getY(), Minecraft.getInstance().player.blockPosition().getZ()) >= 1024) {
//                clearSelection();
//            }
//
//            if(Minecraft.getInstance().level != null && renderHelper != null) {
//                if (hasLowerLayer() && !doesPlacedLayerMatch(this.renderedLayer - 1)) {
//                    updateLayers();
//                } else if (doesPlacedLayerMatch(this.renderedLayer)) {
//                    if (!this.matchArray.matches(Minecraft.getInstance().level, this.attachedPosition, true, this.machine.getModifiersAsMatchingReplacements())) {
//                        updateLayers();
//                    } else {
//                        clearSelection();
//                    }
//                }
//            }
//        }
    }

//    void renderTranslucentBlocks() {
//        Minecraft.getInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//        GlStateManager.pushMatrix();

//        float partialTicks = Minecraft.getInstance().getPartialTick();
//        Entity rView = Minecraft.getInstance().getEntityRenderDispatcher().crosshairPickEntity;
//        Entity entity = rView;
//        double tx = entity.getX() + ((entity.getBlockX() - entity.getX()) * partialTicks);
//        double ty = entity.getY() + ((entity.getBlockY() - entity.getY()) * partialTicks);
//        double tz = entity.getZ() + ((entity.getBlockZ() - entity.getZ()) * partialTicks);

//        GlStateManager.translate(-tx, -ty, -tz);

//        GlStateManager.color(1F, 1F, 1F, 1F);

//        if(batchDList == -1) {
//            batchBlocks();
//            hash = hashBlocks();
//        } else {
//            int currentHash = hashBlocks();
//            if(hash != currentHash) {
//                GLAllocation.deleteDisplayLists(batchDList);
//                batchBlocks();
//                hash = currentHash;
//            }
//        }
//        GlStateManager.disableDepth();
//        GlStateManager.enableBlend();
//        Blending.ALPHA.applyStateManager();
//        GlStateManager.callList(batchDList);
//        Blending.DEFAULT.applyStateManager();
//        GlStateManager.enableDepth();
//
//        GlStateManager.popMatrix();

        //Color desync on block rendering - prevent that, resync
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GL11.glColor4f(1F, 1F, 1F, 1F);
//    }

//    private void renderTranslucentBlocks() {
//        float partialTicks = Minecraft.getInstance().getPartialTick();
//    }
//
//    private int hashBlocks() {
//        int hash = 80238287;
//        if(this.renderHelper != null && Minecraft.getInstance().player != null) {
//            Vec3i move = getRenderOffset();
//            if(move != null) {
//                BlockArray render = new BlockArray(this.matchArray, move);
//                for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : render.getPattern().entrySet()) {
//                    if(Minecraft.getInstance().level != null && entry.getValue().matches(Minecraft.getInstance().level, entry.getKey(), false)) {
//                        continue;
//                    }
//                    int layer = entry.getKey().subtract(move).getY();
//                    if (this.attachedPosition != null && this.renderedLayer != layer) {
//                        continue;
//                    }
//                    hash = (hash << 4) ^ (hash >> 28) ^ (entry.getKey().getX() * 5449 % 130651);
//                    hash = (hash << 4) ^ (hash >> 28) ^ (entry.getKey().getY() * 5449 % 130651);
//                    hash = (hash << 4) ^ (hash >> 28) ^ (entry.getKey().getZ() * 5449 % 130651);
//                    hash = (hash << 4) ^ (hash >> 28) ^ (entry.getValue().getSampleState(Optional.of(renderHelper.sampleSnap)).hashCode() * 5449 % 130651);
//                }
//            }
//        }
//        return hash % 75327403;
//    }
//
//    private void batchBlocks() {
//        BlockPos move = getRenderOffset();
//        if (move == null || this.renderHelper == null) {
//            if (batchDList != -1) {
////                GlStateManager.glDeleteLists(batchDList, 1);
//                batchDList = -1;
//            }
//            return;
//        }
//        batchDList = GLAllocation.generateDisplayLists(1);
//        GlStateManager.glNewList(batchDList, GL11.GL_COMPILE);
//        Tesselator tes = Tesselator.getInstance();
//        BufferBuilder vb = tes.getBuilder();
//        BlockArray matchPattern = this.matchArray;
//
//        if (this.attachedPosition == null) {
//            BlockState lookState = Minecraft.getInstance().level.getBlockState(move);
//            if (lookState.getBlock() instanceof BlockController) {
//                Direction rotate = lookState.getValue(BlockController.FACING);
//
//                BlockPos moveDir = MiscUtils.rotateYCCWNorthUntil(new BlockPos(this.renderHelperOffset), rotate);
//                move = move.subtract(moveDir);
//                matchPattern = MiscUtils.rotateYCCWNorthUntil(matchPattern, rotate);
//            }
//        }
//        BlockArrayRenderHelper.WorldBlockArrayRenderAccess access = renderHelper.getRenderAccess().build(renderHelper, matchPattern, move);
//        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
//        VertexFormat blockFormat = DefaultVertexFormat.BLOCK;
//
//        for (Map.Entry<BlockPos, BlockArrayRenderHelper.BakedBlockData> data : access.blockRenderData.entrySet()) {
//            BlockPos offset = data.getKey();
//            int layer = offset.subtract(move).getY();
//            if (this.attachedPosition != null && this.renderedLayer != layer) {
//                continue;
//            }
//
//            BlockArrayRenderHelper.BakedBlockData renderData = data.getValue();
//            BlockArrayRenderHelper.SampleRenderState state = renderData.getSampleState();
//
//            if(Minecraft.getInstance().level != null &&
//                    matchPattern.getPattern().get(offset.subtract(move)).matches(Minecraft.getInstance().level, offset, false)) {
//                continue;
//            }
//            if(state.state.getBlock() != Blocks.AIR) {
//                BlockArrayRenderHelper.TileEntityRenderData terd = state.renderData;
//                if(terd != null && terd.tileEntity != null) {
//                    terd.tileEntity.setLevel(Minecraft.getInstance().level);
//                    terd.tileEntity.setPos(offset);
//                }
//                BlockState actRenderState = state.state;
//                actRenderState = actRenderState.getBlock().getActualState(actRenderState, access, offset);
//                GlStateManager.pushMatrix();
//                GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());
//                GlStateManager.translate(0.125, 0.125, 0.125);
//                GlStateManager.scale(0.75, 0.75, 0.75);
//                vb.begin(GL11.GL_QUADS, blockFormat);
//                brd.renderSingleBlock(actRenderState, BlockPos.ZERO, access, vb);
//                tes.draw();
//                GlStateManager.popMatrix();
//            }
//        }
//        GlStateManager.glEndList();
//    }

    private BlockPos getRenderOffset() {
        BlockPos move = this.attachedPosition;
        if (move == null) {
            BlockHitResult res = getLookBlock(Minecraft.getInstance().player, false, true, 20);
            if(res != null && res.getType() == BlockHitResult.Type.BLOCK) {
                BlockState state = Minecraft.getInstance().level.getBlockState(res.getBlockPos());
                if (state.getBlock() instanceof BlockController) {
                    return res.getBlockPos();
                } else {
                    return res.getBlockPos().relative(res.getDirection());
                }
            }
        }
        return move;
    }

    @Nullable
    private BlockHitResult getLookBlock(Entity e, boolean stopTraceOnLiquids, boolean ignoreBlockWithoutBoundingBox, double range) {
        float pitch = e.getXRot();
        float yaw = e.getYRot();
        Vec3 entityVec = new Vec3(e.getX(), e.getY() + e.getEyeHeight(), e.getZ());
        float f2 = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -Mth.cos(-pitch * 0.017453292F);
        float f5 = Mth.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec3d1 = entityVec.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
        BlockHitResult rtr = e.level().clip(new ClipContext(
          entityVec,
          vec3d1,
          ignoreBlockWithoutBoundingBox ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
          stopTraceOnLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
          e
        ));
        if (rtr.getType() != BlockHitResult.Type.BLOCK) {
            return null;
        }
        return rtr;
    }

    private boolean doesPlacedLayerMatch(int slice) {
        if (this.attachedPosition != null) {
            Level world = Minecraft.getInstance().level;
            if (world != null) {
//                DynamicMachine.ModifierReplacementMap replacements = this.machine.getModifiersAsMatchingReplacements();
                Map<BlockPos, BlockArray.BlockInformation> patternSlice = this.matchArray.getPatternSlice(slice);
                lblMatching:
                for (Map.Entry<BlockPos, BlockArray.BlockInformation> data : patternSlice.entrySet()) {
                    BlockPos offset = data.getKey();
                    BlockPos actualPosition = offset.offset(this.attachedPosition);
                    BlockArray.BlockInformation info = this.matchArray.getPattern().get(offset);
                    if (info.matches(world, actualPosition, false)) {
                        continue;
                    }
//                    if(replacements.containsKey(offset)) {
//                        for (BlockArray.BlockInformation bi : replacements.get(offset)) {
//                            if (bi.matches(world, actualPosition, false)) {
//                                continue lblMatching;
//                            }
//                        }
//                    }
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasLowerLayer() {
        if (this.attachedPosition != null) {
            return (this.matchArray.getMin().getY()) <= this.renderedLayer - 1;
        }
        return false;
    }

    private void updateLayers() {
        this.renderedLayer = -1;
        if (this.attachedPosition != null) {
//            BlockArray matchingArray = this.renderHelper.getBlocks();
//            int lowestSlice = matchingArray.getMin().getY();
//            int maxSlice = matchingArray.getMax().getY();
//            for (int y = lowestSlice; y <= maxSlice; y++) {
//                if (!doesPlacedLayerMatch(y)) {
//                    this.renderedLayer = y;
//                    return;
//                }
//            }
        }
    }

    private void clearSelection() {
//        this.renderHelper = null;
        this.matchArray = null;
        this.renderHelperOffset = null;
        this.attachedPosition = null;
        this.machine = null;
    }

    public void unloadWorld() {
        clearSelection();
    }

}
