package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmreborn.common.util.HybridTank;
import java.util.Locale;
import lombok.Getter;
import net.minecraft.util.StringRepresentable;

public enum FluidHatchSize implements StringRepresentable {
  TINY(100),
  SMALL(400),
  NORMAL(1000),
  REINFORCED(2000),
  BIG(4500),
  HUGE(8000),
  LUDICROUS(16000),
  VACUUM(32000);

  @Getter
  private int size;

  public final int defaultConfigurationValue;

  FluidHatchSize(int defaultConfigurationValue) {
    this.defaultConfigurationValue = defaultConfigurationValue;
  }

  public static FluidHatchSize value(String value) {
    return switch(value.toUpperCase(Locale.ROOT)) {
      case "TINY" -> TINY;
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "VACUUM" -> VACUUM;
      default -> null;
    };
  }

  public HybridTank buildTank(BlockEntitySynchronized tileEntity, boolean canFill, boolean canDrain) {
    HybridTank tank;
//    if(Mods.MEKANISM.isPresent()) {
//      tank = buildMekTank(tileEntity);
//    } else {
      tank = buildDefaultTank(tileEntity);
//    }
//    tank.setCanFill(canFill);
//    tank.setCanDrain(canDrain);
    return tank;
  }

  private HybridTank buildDefaultTank(BlockEntitySynchronized tileEntity) {
    return new HybridTank(this.size) {
      @Override
      protected void onContentsChanged() {
        super.onContentsChanged();
        tileEntity.markForUpdate();
      }
    };
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }

  public static void loadFromConfig() {
    for (FluidHatchSize size : values()) {
      size.size = MMRConfig.get().fluidHatch.fluidSize(size);
    }
  }

}
