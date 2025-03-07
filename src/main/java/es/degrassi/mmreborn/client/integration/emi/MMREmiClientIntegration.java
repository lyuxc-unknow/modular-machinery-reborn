package es.degrassi.mmreborn.client.integration.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.api.TagUtil;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiComponentEvent;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiRequirementToIngredientEvent;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiRequirementToStackEvent;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiBiomeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiChunkloadComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiDimensionComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiEnergyComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiExperienceComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiFluidComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiItemComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiLootTableComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiTimeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiWeatherComponent;
import es.degrassi.mmreborn.common.integration.emi.EmiComponentRegistry;
import es.degrassi.mmreborn.common.integration.emi.EmiIngredientRegistry;
import es.degrassi.mmreborn.common.integration.emi.EmiStackRegistry;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.LootTableHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

public class MMREmiClientIntegration {
  public MMREmiClientIntegration(IEventBus bus) {
    bus.register(this);
    EmiComponentRegistry.init();
    EmiStackRegistry.init();
    EmiIngredientRegistry.init();
  }

  @SubscribeEvent
  public void registerEmiComponents(final RegisterEmiComponentEvent event) {
    event.register(RequirementTypeRegistration.ENERGY.get(), EmiEnergyComponent::new);
    event.register(RequirementTypeRegistration.EXPERIENCE.get(), EmiExperienceComponent::new);
    event.register(RequirementTypeRegistration.ITEM.get(), EmiItemComponent::new);
    event.register(RequirementTypeRegistration.FLUID.get(), EmiFluidComponent::new);
    event.register(RequirementTypeRegistration.BIOME.get(), EmiBiomeComponent::new);
    event.register(RequirementTypeRegistration.TIME.get(), EmiTimeComponent::new);
    event.register(RequirementTypeRegistration.CHUNKLOAD.get(), EmiChunkloadComponent::new);
    event.register(RequirementTypeRegistration.DIMENSION.get(), EmiDimensionComponent::new);
    event.register(RequirementTypeRegistration.WEATHER.get(), EmiWeatherComponent::new);
    event.register(RequirementTypeRegistration.LOOT_TABLE.get(), EmiLootTableComponent::new);
  }

  @SubscribeEvent
  public void registerEmiStacks(final RegisterEmiRequirementToStackEvent event) {
    event.register(
        RequirementTypeRegistration.ITEM.get(),
        this::emiStackFromItemRequirement
    );
    event.register(
        RequirementTypeRegistration.FLUID.get(),
        requirement -> List.of(
            EmiStack.of(requirement.requirement().required.asFluidStack().getFluid(), requirement.requirement().amount)
        )
    );
    event.register(
        RequirementTypeRegistration.LOOT_TABLE.get(),
        requirement -> LootTableHelper
            .getLootsForTable(requirement.requirement().getLootTable())
            .stream()
            .map(LootTableHelper.LootData::stack)
            .map(EmiStack::of)
            .toList()
    );
  }

  @SubscribeEvent
  public void registerEmiIngredients(final RegisterEmiRequirementToIngredientEvent event) {
    event.register(
        RequirementTypeRegistration.ITEM.get(),
        this::emiIngredientFromItemRequirement
    );
  }

  private EmiIngredient emiIngredientFromItemRequirement(RecipeRequirement<ItemComponent, RequirementItem> requirement) {
    return EmiIngredient.of(requirement.requirement().ingredient.ingredient(), requirement.requirement().ingredient.count());
  }

  private List<EmiStack> emiStackFromItemRequirement(RecipeRequirement<ItemComponent, RequirementItem> requirement) {
    List<EmiStack> stacks = Lists.newArrayList();
    for (Ingredient.Value value : requirement.requirement().getIngredient().ingredient().values) {
      if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) {
        for (Item stack : TagUtil.getItems(tag).toList()) {
          stacks.add(EmiStack.of(stack, requirement.requirement().ingredient.count()));
        }
      } else if (value instanceof Ingredient.ItemValue(ItemStack item)) {
        stacks.add(EmiStack.of(item, requirement.requirement().ingredient.count()));
      }
    }
    return stacks;
  }
}
