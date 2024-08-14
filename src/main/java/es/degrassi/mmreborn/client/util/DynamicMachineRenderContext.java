package es.degrassi.mmreborn.client.util;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.client.ClientScheduler;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.TaggedPositionBlockArray;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.util.BlockArray;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import org.joml.Vector3d;

@Getter
public class DynamicMachineRenderContext {

    private final DynamicMachine machine;
//    private final BlockArrayRenderHelper render;
    private final Vec3i moveOffset;
    @Nullable
    private BlockState machineState;
    private boolean render3D = true;
    private int renderSlice = 0;
    private float scale = 1F;
    private long shiftSnap = -1;

    private DynamicMachineRenderContext(DynamicMachine machine) {
        this(machine, new BlockPos(
          new Vec3i(
//            (machine.getPattern().getMin().getX() + (machine.getPattern().getMax().getX() - machine.getPattern().getMin().getX()) / 2) * -1,
//            -machine.getPattern().getMin().getY(),
//            (machine.getPattern().getMin().getZ() + (machine.getPattern().getMax().getZ() - machine.getPattern().getMin().getZ()) / 2) * -1
            0, 0, 0
          )
        ), BlockRegistration.CONTROLLER.get().defaultBlockState());
    }

    private DynamicMachineRenderContext(DynamicMachine machine, BlockPos machinePos, BlockState machineState) {
        this.machine = machine;
//        BlockArray pattern = machine.getPattern();
        this.moveOffset = machinePos;
        TaggedPositionBlockArray copy = new TaggedPositionBlockArray();
//        pattern.getPattern().forEach((key, value) -> copy.addBlock(key.offset(this.moveOffset), value));
        copy.addBlock(new BlockPos(this.moveOffset), new BlockArray.BlockInformation(Lists.newArrayList(new BlockArray.BlockStateDescriptor(machineState))));
//        this.render = new BlockArrayRenderHelper(copy);
        this.machineState = machineState;
    }

  public void snapSamples() {
        this.shiftSnap = ClientScheduler.getClientTick();
    }

    public void releaseSamples() {
        this.shiftSnap = -1;
    }

    public void resetRender() {
        setTo2D();
        setTo3D();
    }

    public void setTo2D() {
        if(!render3D) return;
        render3D = false;
//        renderSlice = render.getBlocks().getMin().getY();
//        render.resetRotation2D();
        scale = 1F;
    }

    public void setTo3D() {
        if(render3D) return;
        render3D = true;
        renderSlice = 0;
//        render.resetRotation();
        scale = 1F;
    }

  public Vector3d getCurrentMachineTranslate() {
//        if(render3D) {
            return new Vector3d(0, 0, 0);
//        }
//        return this.render.getCurrentTranslation();
    }

    public Vector2f getCurrentRenderOffset(float x, float z) {
        Minecraft mc = Minecraft.getInstance();
        double sc = new ScaledResolution(mc).getScaleFactor();
        double oX = x + 16D / sc;
        double oZ = z + 16D / sc;
        Vector3d tr = getCurrentMachineTranslate();
        return new Vector2f((float) (oX + tr.x), (float) (oZ + tr.z));
    }

    public void zoomOut() {
        scale *= 0.85F;
    }

    public void zoomIn() {
        scale *= 1.15F;
    }

    public boolean doesRenderIn3D() {
        return render3D;
    }

    public int getRenderSlice() {
        return renderSlice - this.moveOffset.getY();
    }

    public boolean hasSliceDown() {
//        return render.getBlocks().getMin().getY() < renderSlice;
      return false;
    }

    public boolean hasSliceUp() {
//        return render.getBlocks().getMax().getY() > renderSlice;
      return false;
    }

    public void sliceUp() {
        if(hasSliceUp()) {
            renderSlice++;
        }
    }

    public void sliceDown() {
        if(hasSliceDown()) {
            renderSlice--;
        }
    }

    public DynamicMachine getDisplayedMachine() {
        return machine;
    }

    @OnlyIn(Dist.CLIENT)
    public List<ItemStack> getDescriptiveStacks() {
//        return this.getDisplayedMachine().getPattern().getAsDescriptiveStacks(shiftSnap == -1 ? Optional.empty() : Optional.of(shiftSnap));
      return List.of();
    }

    public static DynamicMachineRenderContext createContext(DynamicMachine machine) {
        return new DynamicMachineRenderContext(machine);
    }

    public static DynamicMachineRenderContext createContext(DynamicMachine machine, BlockPos machinePos) {
        return createContext(machine, machinePos, BlockRegistration.CONTROLLER.get().defaultBlockState());
    }

    public static DynamicMachineRenderContext createContext(DynamicMachine machine, BlockPos machinePos, BlockState machineState) {
        return new DynamicMachineRenderContext(machine, machinePos, machineState);
    }

    public void renderAt(int x, int z) {
        renderAt(x, z, 1F);
    }

    public void renderAt(int x, int z, float partialTicks) {
//        render.sampleSnap = shiftSnap;
//        if(render3D) {
//            render.render3DGUI(x, z, scale, partialTicks);
//        } else {
//            render.render3DGUI(x, z, scale, partialTicks, Optional.of(renderSlice));
//        }
    }

    public void rotateRender(double x, double y, double z) {
//        this.render.rotate(x, y, z);
    }

    public void moveRender(double x, double y, double z) {
//        this.render.translate(x, y, z);
    }

}
