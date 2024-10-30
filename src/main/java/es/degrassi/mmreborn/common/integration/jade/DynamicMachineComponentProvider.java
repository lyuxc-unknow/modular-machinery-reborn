package es.degrassi.mmreborn.common.integration.jade;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public class DynamicMachineComponentProvider implements IBlockComponentProvider {

  public static final DynamicMachineComponentProvider INSTANCE = new DynamicMachineComponentProvider();
  public static final ResourceLocation ID = ModularMachineryReborn.rl("machine_component_provider");

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    if (accessor.getBlockEntity() instanceof MachineControllerEntity) {
      CompoundTag nbt = accessor.getServerData().getCompound(ModularMachineryReborn.MODID);
      if (nbt.isEmpty()) return;
      if (nbt.contains("status", Tag.TAG_COMPOUND)) {
        MachineControllerEntity.CraftingStatus status = MachineControllerEntity.CraftingStatus.deserialize(nbt.getCompound("status"));
        MutableComponent message = Component.translatable(status.getUnlocMessage());
        switch (status.getStatus()) {
          case CRAFTING -> message.withStyle(ChatFormatting.GREEN);
          case NO_RECIPE -> message.withStyle(ChatFormatting.GOLD);
          case MISSING_STRUCTURE -> message.withStyle(ChatFormatting.RED);
        }
        tooltip.add(message);
      }
      if (nbt.contains("progress", Tag.TAG_DOUBLE) && nbt.contains("total", Tag.TAG_INT)) {
        double ticks = nbt.getDouble("progress");
        float total = nbt.getInt("total");
        float progress = (float) (ticks / total);
        String ticksTotal = ticks + " / " + total;
        if (total >= 20) {
          ticksTotal = Utils.decimalFormat(ticks / 20) + " / " + Utils.decimalFormat(total / 20) + "s";
        }
        Component component = Component
            .literal(ticksTotal + " (" + Utils.decimalFormatWithPercentage(progress * 100) + ")")
            .withStyle(ChatFormatting.WHITE);
        IElementHelper helper = IElementHelper.get();
        tooltip.add(helper.progress(progress, component, helper.progressStyle(), BoxStyle.getNestedBox(), true));
      }
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
