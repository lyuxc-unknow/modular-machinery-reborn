package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.ItemIngredient;
import es.degrassi.mmreborn.api.ItemTagIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.Arrays;

public interface ItemRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack, int x, int y) {
    if(stack.getItems().length == 0)
      return this.error("Invalid empty ingredient in item input requirement");
    return addRequirement(new RequirementItem(IOType.INPUT, stack,
        new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }

  /*default MachineRecipeBuilderJS requireItem(ItemStack stack, int x, int y) {
    MMRLogger.INSTANCE.info("stack: {}", stack);
    if(stack.isEmpty())
      return this.error("Invalid empty ingredient in item input requirement");
    return addRequirement(new RequirementItem(IOType.INPUT, new SizedIngredient(Ingredient.of(stack), stack.getCount()),
        stack.getCount(),
        new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS requireItemTag(ResourceLocation tag, int x, int y) {
    return requireItemTag(tag, 1, x, y);
  }

  default MachineRecipeBuilderJS requireItemTag(ResourceLocation tag, int amount, int x, int y) {
    try {
      TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), tag);
      return addRequirement(new RequirementItem(IOType.INPUT, new SizedIngredient(Ingredient.of(tagKey), amount), amount,
          new IJeiRequirement.JeiPositionedRequirement(x, y)));
    } catch (Exception ignored) {
      return this.error("Invalid tag provided: {}", tag);
    }
  }*/

  default MachineRecipeBuilderJS produceItem(ItemStack stack, int x, int y) {
    if(stack.isEmpty())
      return this.error("Invalid empty item in item output requirement");
    return addRequirement(new RequirementItem(IOType.OUTPUT, new SizedIngredient(Ingredient.of(stack),
        stack.getCount()),
        new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }
}
