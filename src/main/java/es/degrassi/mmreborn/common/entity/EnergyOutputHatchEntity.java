package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.BlockEnergyOutputHatch;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyOutputHatchEntity extends EnergyHatchEntity {

  private BlockPos foundCore = null;

  public EnergyOutputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_OUTPUT_HATCH.get(), pos, state);
    this.size = null;
  }

  public EnergyOutputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_OUTPUT_HATCH.get(), pos, state, size, IOType.OUTPUT);
  }

  public void tick() {
    if (level.isClientSide()) return;

    long prevEnergy = this.energy;

    long transferCap = Math.min(this.size.transferLimit, this.energy);
//        if (Mods.DRACONICEVOLUTION.isPresent()) {
//            long transferred = attemptDECoreTransfer(transferCap);
//            transferCap -= transferred;
//            this.energy -= transferred;
//        }
//        long usableAmps = Math.min(this.size.getGtAmperage(), transferCap / 4L / this.size.getGTEnergyTransferVoltage());
    for (Direction face : Direction.values()) {
//            if (transferCap > 0 && Mods.GREGTECH.isPresent() && usableAmps > 0) {
//                long totalTransferred = attemptGTTransfer(face, transferCap / 4L, usableAmps) * 4L;
//                usableAmps -= totalTransferred / 4L / this.size.getGTEnergyTransferVoltage();
//                transferCap -= totalTransferred;
//                this.energy -= totalTransferred;
//            }
      if (transferCap > 0) {
//                if (Mods.REDSTONEFLUXAPI.isPresent()) {
//                    int transferred = attemptFERFTransfer(face, convertDownEnergy(transferCap));
//                    transferCap -= transferred;
//                    this.energy -= transferred;
//                } else {
        int transferred = attemptFETransfer(face, convertDownEnergy(transferCap));
        transferCap -= transferred;
        this.energy -= transferred;
//                }
      }
      if (transferCap <= 0) {
        break;
      }
    }

    if (prevEnergy != this.energy) {
      markForUpdate();
    }
  }

//    @Optional.Method(modid = "draconicevolution")
//    private long attemptDECoreTransfer(long transferCap) {
//        TileEntity te = foundCore == null ? null : world.getTileEntity(foundCore);
//        if (foundCore == null || !(te instanceof TileEnergyStorageCore)) {
//            if (world.getTotalWorldTime() % 100 == 0) {
//                foundCore = findCore(foundCore);
//            }
//        }
//
//        if (foundCore != null && te instanceof TileEnergyStorageCore) {
//            TileEnergyStorageCore core = (TileEnergyStorageCore) te;
//
//            long energyReceived = Math.min(core.getExtendedCapacity() - core.energy.value, transferCap);
//            ((TileEnergyStorageCore) te).energy.value += energyReceived;
//
//            return energyReceived;
//        }
//        return 0;
//    }
//
//    @Optional.Method(modid = "draconicevolution")
//    private BlockPos findCore(BlockPos before) {
//        List<TileEnergyStorageCore> list = new LinkedList<>();
//        int range = 16;
//
//        Iterable<BlockPos> positions = BlockPos.getAllInBox(pos.add(-range, -range, -range), pos.add(range, range, range));
//
//        for (BlockPos blockPos : positions) {
//            if (world.getBlockState(blockPos).getBlock() == DEFeatures.energyStorageCore) {
//                TileEntity tile = world.getTileEntity(blockPos);
//                if (tile instanceof TileEnergyStorageCore && ((TileEnergyStorageCore) tile).active.value) {
//                    list.add(((TileEnergyStorageCore) tile));
//                }
//            }
//        }
//        if (before != null) {
//            list.removeIf(tile -> tile.getPos().equals(before));
//        }
//        Collections.shuffle(list);
//        TileEnergyStorageCore first = Iterables.getFirst(list, null);
//        return first == null ? null : first.getPos();
//    }
//
//    @Optional.Method(modid = "gregtech")
//    private long attemptGTTransfer(EnumFacing face, long transferCap, long usedAmps) {
//        long voltage = this.size.getGTEnergyTransferVoltage();
//        long amperes = Math.min(usedAmps, this.size.getGtAmperage());
//        int transferableAmps = 0;
//        while (transferableAmps < amperes && (transferableAmps * voltage) <= transferCap) {
//            transferableAmps++;
//        }
//        if (transferableAmps == 0) {
//            return 0L;
//        }
//
//        TileEntity tileEntity = getWorld().getTileEntity(getPos().offset(face));
//        EnumFacing oppositeSide = face.getOpposite();
//        if(tileEntity != null && tileEntity.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, oppositeSide)) {
//            IEnergyContainer energyContainer = tileEntity.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, oppositeSide);
//            if (energyContainer != null && energyContainer.inputsEnergy(oppositeSide)) {
//                return energyContainer.acceptEnergyFromNetwork(oppositeSide, voltage, transferableAmps)
//                        * voltage;
//            }
//        }
//        return 0L;
//    }

  private int attemptFETransfer(Direction face, int maxTransferLeft) {
    BlockPos at = this.getBlockPos().relative(face);

    int receivedEnergy = 0;
    BlockEntity te = level.getBlockEntity(at);
    if (te != null && !(te instanceof EnergyHatchEntity)) {
      var cache = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, (ServerLevel) getLevel(), getBlockPos().relative(face), face.getOpposite(),
        () -> !isRemoved(), () -> {});
      IEnergyStorage ce = cache.getCapability();
      if (ce != null && ce.canReceive()) {
        try {
          receivedEnergy = ce.receiveEnergy(maxTransferLeft, false);
        } catch (Exception ignored) {
        }
      }
    }
    return receivedEnergy;
  }

//    @Optional.Method(modid = "redstoneflux")
//    private int attemptFERFTransfer(EnumFacing face, int maxTransferLeft) {
//        BlockPos at = this.getPos().offset(face);
//        EnumFacing accessingSide = face.getOpposite();
//
//        int receivedEnergy = 0;
//        TileEntity te = world.getTileEntity(at);
//        if(te != null && !(te instanceof TileEnergyHatch)) {
//            if(te instanceof cofh.redstoneflux.api.IEnergyReceiver && ((IEnergyReceiver) te).canConnectEnergy(accessingSide)) {
//                try {
//                    receivedEnergy = ((IEnergyReceiver) te).receiveEnergy(accessingSide, maxTransferLeft, false);
//                } catch (Exception ignored) {}
//            }
//            if(receivedEnergy <= 0 && te instanceof IEnergyStorage) {
//                try {
//                    receivedEnergy = ((IEnergyStorage) te).receiveEnergy(maxTransferLeft, false);
//                } catch (Exception ignored) {}
//            }
//            if(receivedEnergy <= 0 && te.hasCapability(CapabilityEnergy.ENERGY, accessingSide)) {
//                net.minecraftforge.energy.IEnergyStorage ce = te.getCapability(CapabilityEnergy.ENERGY, accessingSide);
//                if(ce != null && ce.canReceive()) {
//                    try {
//                        receivedEnergy = ce.receiveEnergy(maxTransferLeft, false);
//                    } catch (Exception ignored) {}
//                }
//            }
//        }
//        return receivedEnergy;
//    }

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
//    public double getOfferedEnergy() {
//        return Math.min(this.size.getIC2EnergyTransmission(), this.getCurrentEnergy() / 4L);
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public void drawEnergy(double amount) {
//        this.energy = MiscUtils.clamp(this.energy - (MathHelper.lfloor(amount) * 4L), 0, this.size.maxEnergy);
//        markForUpdate();
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public int getSourceTier() {
//        return size.ic2EnergyTier;
//    }
//
//    @Override
//    @Optional.Method(modid = "ic2")
//    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
//        return true;
//    }

  @Nullable
  @Override
  public MachineComponent.EnergyHatch provideComponent() {
    return new MachineComponent.EnergyHatch(IOType.OUTPUT) {
      @Override
      public IEnergyHandler getContainerProvider() {
        return EnergyOutputHatchEntity.this;
      }
    };
  }

}
