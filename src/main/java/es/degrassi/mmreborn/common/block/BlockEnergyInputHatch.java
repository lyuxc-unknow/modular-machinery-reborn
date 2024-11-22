package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEnergyInputHatch extends BlockEnergyHatch {

//  public static final EnumProperty<EnergyHatchSize> BUS_TYPE = EnumProperty.create("size", EnergyHatchSize.class);
  public BlockEnergyInputHatch(EnergyHatchSize type) {
    super(type);
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    switch (type) {
      case TINY ->        drops.add(ItemRegistration.ENERGY_INPUT_HATCH_TINY.get().getDefaultInstance());
      case SMALL ->       drops.add(ItemRegistration.ENERGY_INPUT_HATCH_SMALL.get().getDefaultInstance());
      case NORMAL ->      drops.add(ItemRegistration.ENERGY_INPUT_HATCH_NORMAL.get().getDefaultInstance());
      case REINFORCED ->  drops.add(ItemRegistration.ENERGY_INPUT_HATCH_REINFORCED.get().getDefaultInstance());
      case BIG ->         drops.add(ItemRegistration.ENERGY_INPUT_HATCH_BIG.get().getDefaultInstance());
      case HUGE ->        drops.add(ItemRegistration.ENERGY_INPUT_HATCH_HUGE.get().getDefaultInstance());
      case LUDICROUS ->   drops.add(ItemRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get().getDefaultInstance());
      case ULTIMATE ->    drops.add(ItemRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get().getDefaultInstance());
    }
    return drops;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, List<Component> tooltip, TooltipFlag flag) {
    if (EnergyDisplayUtil.displayFETooltip) {
      tooltip.add(Component.translatable("tooltip.energyhatch.storage", type.maxEnergy).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.energyhatch.in.accept", type.transferLimit).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.empty());
    }
    if(Mods.IC2.isPresent() && EnergyDisplayUtil.displayIC2EUTooltip) {
      tooltip.add(Component.translatable(
        "tooltip.energyhatch.ic2.in.voltage",
        Component.translatable("tooltip.energyhatch.ic2.any").withStyle(ChatFormatting.BLUE)
      ).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable(
        "tooltip.energyhatch.ic2.in.transfer",
        Component.translatable("tooltip.energyhatch.ic2.any").withStyle(ChatFormatting.BLUE),
        Component.translatable("tooltip.energyhatch.ic2.powerrate").withStyle(ChatFormatting.BLUE)
      ));
      tooltip.add(Component.empty());
    }

//        if (Mods.GREGTECH.isPresent() && EnergyDisplayUtil.displayGTEUTooltip) {
//            addGTTooltip(tooltip, size);
//            tooltip.add("");
//        }
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new EnergyInputHatchEntity(pos, state, type/*state.getValue(BUS_TYPE)*/);
  }
}