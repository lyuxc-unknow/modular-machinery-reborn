package es.degrassi.mmreborn.client.item;

import es.degrassi.mmreborn.common.util.CycleTimer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Getter
@Setter
public class MMRItemTooltipComponent implements TooltipComponent {
  private final List<ItemStack> item;
  private Component component = Component.empty();
  private final CycleTimer timer;

  public MMRItemTooltipComponent(List<ItemStack> stack) {
    this.item = stack;
    this.timer = new CycleTimer(() -> 1000);
    timer.setIgnoreShift(true);
  }

  public ItemStack getItem() {
    this.timer.onDraw();
    return timer.getOrDefault(this.item, ItemStack.EMPTY);
  }
}
