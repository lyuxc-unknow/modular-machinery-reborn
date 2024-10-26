package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class JeiItemComponent extends JeiComponent<ItemStack, RequirementItem> {
  public JeiItemComponent(RequirementItem requirement) {
    super(requirement, 36, 0);
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<ItemStack> ingredients() {
    return Lists.newArrayList(new ItemStack(requirement.ingredient.getAll().get(0), requirement.amount));
  }
}
