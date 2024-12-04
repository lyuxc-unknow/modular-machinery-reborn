package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementLootTable;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.ResourceLocation;

public interface LootTableRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS lootTable(ResourceLocation lootTable) {
    return lootTable(lootTable, 0, 0, 0);
  }

  default MachineRecipeBuilderJS lootTable(ResourceLocation lootTable, float luck) {
    return lootTable(lootTable, luck, 0, 0);
  }

  default MachineRecipeBuilderJS lootTable(ResourceLocation lootTable, int x, int y) {
    return lootTable(lootTable, 0, x, y);
  }

  default MachineRecipeBuilderJS lootTable(ResourceLocation lootTable, float luck, int x, int y) {
    return this.addRequirement(new RequirementLootTable(lootTable, luck, new PositionedRequirement(x, y)));
  }
}
