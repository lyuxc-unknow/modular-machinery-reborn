package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.BlockEnergyInputHatch;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyInputHatchEntity extends EnergyHatchEntity {

  public EnergyInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state);
    this.size = null;//state.getValue(BlockEnergyInputHatch.BUS_TYPE);
  }

  public EnergyInputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  //    @Override
  public void tick() {
  }

//    @Override
//    @Optional.Method(modid = "ic2")
//    public void onLoad() {
//        super.onLoad();
//        IntegrationIC2EventHandlerHelper.fireLoadEvent(world, this);
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public void invalidate() {
//        super.invalidate();
//        if(!world.isRemote) {
//            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
//        }
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public double getDemandedEnergy() {
//        return Math.min((this.size.maxEnergy - this.energy) / 4, this.size.getIC2EnergyTransmission());
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public int getSinkTier() {
//        return this.size.ic2EnergyTier;
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
//        long addable = Math.min((this.size.maxEnergy - this.energy) / 4L, MathHelper.lfloor(amount));
//        amount -= addable;
//        this.energy = MiscUtils.clamp(this.energy + MathHelper.lfloor(addable * 4), 0, this.size.maxEnergy);
//        markForUpdate();
//        return amount;
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
//        return true;
//    }

  @Nullable
  @Override
  public MachineComponent.EnergyHatch provideComponent() {
    return new MachineComponent.EnergyHatch(IOType.INPUT) {
      @Override
      public IEnergyHandler getContainerProvider() {
        return EnergyInputHatchEntity.this;
      }
    };
  }
}
