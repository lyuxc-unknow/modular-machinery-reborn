package es.degrassi.mmreborn.client.item;

import es.degrassi.mmreborn.common.util.CycleTimer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MMRItemTooltipComponent implements TooltipComponent {
  private final List<ItemStack> item;
  private Component component = Component.empty();
  private final CycleTimer timer;

  public MMRItemTooltipComponent(List<ItemStack> stack, CycleTimer timer) {
    this.item = stack;
    this.timer = timer;
  }

  public boolean isCompleted() {
    Inventory inv = Minecraft.getInstance().player.getInventory();
    List<ItemStack> invStacks = inv.items
        .stream()
        .filter(stack -> !stack.isEmpty())
        .collect(Collectors.groupingBy(ItemStack::getItemHolder, Collectors.summingInt(ItemStack::getCount)))
        .entrySet()
        .stream()
        .map(entry -> new ItemStack(entry.getKey(), entry.getValue()))
        .toList();
    for (ItemStack invStack : invStacks) {
      for (ItemStack stack : this.item) {
        if (ItemStack.isSameItemSameComponents(stack, invStack) && invStack.getCount() >= stack.getCount()) {
          return true;
        }
      }
    }
    return false;
  }

  public ItemStack getItem() {
//    this.timer.onDraw();
    return timer.getOrDefault(this.item, ItemStack.EMPTY);
  }
}
