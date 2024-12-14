package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public class DimensionalDetectorEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  public DimensionalDetectorEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.DIMENSIONAL_DETECTOR.get(), pos, blockState);
  }

  @Override
  public MachineComponent<List<ResourceLocation>> provideComponent() {
    return new MachineComponent<>(IOType.INPUT) {
      @Override
      public ComponentType getComponentType() {
        return ComponentRegistration.COMPONENT_DIMENSION.get();
      }

      @Override
      public List<ResourceLocation> getContainerProvider() {
        return List.of(getLevel().dimension().location());
      }
    };
  }
}
