package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.integration.almostunified.AlmostUnifiedAdapter;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.common.util.Mods;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
public class RequirementItem implements IRequirement<ItemComponent> {
  public static final NamedCodec<RequirementItem> CODEC =
      NamedCodec.record(instance -> instance.group(
          DefaultCodecs.SIZED_INGREDIENT_WITH_NBT.fieldOf("sizedIngredient").forGetter(req -> req.ingredient),
          NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, (item, mode, position) -> new RequirementItem(mode, item, position)), "RequirementItem");

  public final SizedIngredient ingredient;
  private final IOType mode;
  private final PositionedRequirement position;

  public RequirementItem(IOType ioType, SizedIngredient ingredient, PositionedRequirement position) {
    if (Mods.isAULoaded()) {
      ingredient = new SizedIngredient(Ingredient.fromValues(Arrays.stream(ingredient.ingredient().getValues())
              .map(v -> {
                if (v instanceof Ingredient.ItemValue(ItemStack item)) {
                  if (((Ingredient.ItemValue) v).item().getComponents().isEmpty())
                    return new Ingredient.ItemValue(AlmostUnifiedAdapter.getPreferredItemForItem(item.getItemHolder()).getDefaultInstance());
                } else if (v instanceof Ingredient.TagValue(TagKey<Item> tag)) {
                  return new Ingredient.ItemValue(AlmostUnifiedAdapter.getPreferredItemForTag(tag).getDefaultInstance());
                }
                return v;
              })), ingredient.count());
    }
    this.ingredient = ingredient;
    this.mode = ioType;
    this.position = position;
  }

  public JsonObject asJson(SizedIngredient ingredient) {
    JsonObject json = new JsonObject();
    JsonArray stacks = new JsonArray();
    for (ItemStack stack : ingredient.getItems()) {
      ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result().map(JsonElement::toString).ifPresent(stacks::add);
    }
    json.add("items", stacks);
    json.addProperty("count", ingredient.count());
    return json;
  }

  @Override
  public RequirementType<RequirementItem> getType() {
    return RequirementTypeRegistration.ITEM.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public boolean test(ItemComponent component, ICraftingContext context) {
    IOInventory handler = component.getContainerProvider();
    return switch (getMode()) {
      case INPUT -> {
        int amt = Math.round(RecipeModifier.applyModifiers(context.getModifiers(getType()), this.getType(), getMode(),
            ingredient.count(),
            false));
        for (int i = 0; i < handler.getSlots(); i++) {
          ItemStack stack = handler.getStackInSlot(i).copyWithCount(amt);
          if (ingredient.test(stack))
            yield true;
        }
        yield false;
      }
      case OUTPUT -> {
        ItemStack stack = ingredient.getItems()[0].copyWithCount(ingredient.count());

        int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
        yield ingredient.count() - inserted <= 0;
      }
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<ItemComponent> list) {
    switch (getMode()) {
      case INPUT -> list.processOnStart(this::processInput);
      case OUTPUT -> list.processOnEnd(this::processOutput);
    }
  }
  private CraftingResult processInput(ItemComponent component, ICraftingContext context) {
    IOInventory handler = component.getContainerProvider();
    int required = Math.round(RecipeModifier.applyModifiers(context, new RecipeRequirement<>(this), this.ingredient.count(), false));
    for (ItemStack stack : ingredient.getItems()) {
      stack = stack.copyWithCount(required);
      boolean can = ItemUtils.consumeFromInventory(handler, stack, true, false);
      if (can)
        if (ItemUtils.consumeFromInventory(handler, stack, false, false))
          return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.item.input", required, ingredient.ingredient().toString()));
  }

  private CraftingResult processOutput(ItemComponent component, ICraftingContext context) {
    if (!test(component, context)) return CraftingResult.error(Component.translatable("craftcheck.failure.item.output.space"));
    IOInventory handler = component.getContainerProvider();
    ItemStack stack = ingredient.getItems()[0].copyWithCount(ingredient.count());
    ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, false);
    return CraftingResult.success();
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.add("ingredient", asJson(ingredient));
    return json;
  }

  @Override
  public RequirementItem deepCopyModified(List<RecipeModifier> modifiers) {
    int inAmt = Math.round(RecipeModifier.applyModifiers(modifiers, this.getType(), getMode(), ingredient.count(), false));
    return new RequirementItem(getMode(), new SizedIngredient(ingredient.ingredient(), inAmt), getPosition());
  }

  @Override
  public RequirementItem deepCopy() {
    return new RequirementItem(getMode(), new SizedIngredient(ingredient.ingredient(), ingredient.count()),
        getPosition());
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.item.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(ItemComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
