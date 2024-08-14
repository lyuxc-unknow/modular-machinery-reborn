package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.Registration;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemBlueprint extends Item {
  private static final ResourceLocation DUMMY = ModularMachineryReborn.rl("dummy");

  public ItemBlueprint() {
    super(
      new Properties()
        .stacksTo(16)
        .component(Registration.MACHINE_DATA, DUMMY)
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
    DynamicMachine machine = getAssociatedMachine(stack);
    if (machine == null) {
      pTooltipComponents.add(Component.translatable("tooltip.machinery.empty").withStyle(ChatFormatting.GRAY));
    } else {
      pTooltipComponents.add(Component.translatable(machine.getLocalizedName()).withStyle(ChatFormatting.GRAY));
    }
  }

  @Nullable
  public static DynamicMachine getAssociatedMachine(ItemStack stack) {
    ResourceLocation id = stack.get(Registration.MACHINE_DATA.get());
    if (id == null || id.equals(DUMMY)) return null;
    return ModularMachineryReborn.MACHINES.get(id);
  }
}
