package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import es.degrassi.mmreborn.common.network.server.component.SUpdateChemicalComponentPacket;
import lombok.Getter;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

public enum ChemicalHatchSize implements StringRepresentable {
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

  ChemicalHatchSize(int defaultConfigurationValue) {
    this.defaultConfigurationValue = defaultConfigurationValue;
  }

  public static ChemicalHatchSize value(String value) {
    return switch(value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "VACUUM" -> VACUUM;
      default -> TINY;
    };
  }

  public BasicChemicalTank buildTank(ChemicalTankEntity tileEntity, boolean canFill, boolean canDrain) {
    return buildDefaultTank(tileEntity, canFill, canDrain);
  }

  private BasicChemicalTank buildDefaultTank(ChemicalTankEntity tileEntity, boolean canFill, boolean canDrain) {
    return (BasicChemicalTank) BasicChemicalTank.create(
      size,
      ((chemical, automationType) -> canDrain),
      ((chemical, automationType) -> canFill),
      ConstantPredicates.alwaysTrue(),
      ChemicalAttributeValidator.ALWAYS_ALLOW,
      () -> {
        if (tileEntity.getLevel() instanceof ServerLevel l)
          PacketDistributor.sendToPlayersTrackingChunk(
            l,
            new ChunkPos(tileEntity.getBlockPos()),
            new SUpdateChemicalComponentPacket(tileEntity.getTank().getStack(), tileEntity.getBlockPos())
          );
      }
    );
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }

  public static void loadFromConfig() {
    for (ChemicalHatchSize size : values()) {
      size.size = MMRConfig.get().chemicalHatch.chemicalSize(size);
    }
  }
}
