package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class RequirementItem extends ComponentRequirement<ItemStack, RequirementItem> implements ComponentRequirement.ChancedRequirement {
  public static final NamedCodec<RequirementItem> CODEC =
      NamedCodec.record(instance -> instance.group(
          DefaultCodecs.SIZED_INGREDIENT_WITH_NBT.fieldOf("sizedIngredient").forGetter(req -> req.ingredient),
          NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
          NamedCodec.floatRange(0, 1).optionalFieldOf("chance", 1f).forGetter(req -> req.chance),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(ComponentRequirement::getPosition)
      ).apply(instance, (item, mode, chance, position) -> {
        RequirementItem requirementItem = new RequirementItem(
            mode,
            new SizedIngredient(item.ingredient(), item.count()),
            position
        );
        requirementItem.setChance(chance);

        return requirementItem;
      }), "RequirementItem");

  @Getter
  public final SizedIngredient ingredient;

  public int countIOBuffer = 0;

  @Getter
  public float chance = 1F;

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
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("item").toString());
    json.add("ingredient", asJson(ingredient));
    json.addProperty("chance", chance);
    return json;
  }

  public RequirementItem(IOType ioType, SizedIngredient ingredient, PositionedRequirement position) {
    super(RequirementTypeRegistration.ITEM.get(), ioType, position);
    this.ingredient = ingredient;
  }

  @Override
  public int getSortingWeight() {
    return PRIORITY_WEIGHT_ITEM;
  }

  @Override
  public ComponentRequirement<ItemStack, RequirementItem> deepCopy() {
    RequirementItem item = new RequirementItem(getActionType(), new SizedIngredient(ingredient.ingredient(), ingredient.count()), getPosition());
    item.chance = this.chance;
    return item;
  }

  @Override
  public ComponentRequirement<ItemStack, RequirementItem> deepCopyModified(List<RecipeModifier> modifiers) {
    int inAmt = Math.round(RecipeModifier.applyModifiers(modifiers, this.getRequirementType(), getActionType(),
        ingredient.count(), false));
    RequirementItem item = new RequirementItem(getActionType(), new SizedIngredient(ingredient.ingredient(), inAmt), getPosition());

    item.chance = RecipeModifier.applyModifiers(modifiers, this, this.chance, true);
    return item;
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
    this.countIOBuffer = Math.round(RecipeModifier.applyModifiers(context, this, this.ingredient.count(), false));
  }

  @Override
  public void endRequirementCheck() {
    this.countIOBuffer = 0;
  }

  @Override
  public void setChance(float chance) {
    this.chance = chance;
  }

  @Nonnull
  @Override
  public String getMissingComponentErrorMessage(IOType ioType) {
    return String.format("component.missing.item.%s", ioType.name().toLowerCase());
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_ITEM.get()) &&
        cmp instanceof MachineComponent.ItemBus &&
        cmp.getIOType() == getActionType();
  }

  @Nonnull
  @Override
  public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context,
                                     List<ComponentOutputRestrictor<?>> restrictions) {
    IOInventory handler = (IOInventory) component.providedComponent();
    return switch (getActionType()) {
      case INPUT -> {
        int amt = Math.round(RecipeModifier.applyModifiers(context, this, ingredient.count(), false));
        for (ItemStack stack : ingredient.getItems()) {
          if (ItemUtils.consumeFromInventory(handler, stack.copyWithCount(amt), true)) {
            yield CraftCheck.success();
          }
        }
        yield CraftCheck.failure("craftcheck.failure.item.input");
      }
      case OUTPUT -> {
        handler = CopyHandlerHelper.copyInventory(handler, context.getMachineController().getLevel().registryAccess());
        for (ComponentOutputRestrictor<?> restrictor : restrictions) {
          if (restrictor instanceof ComponentOutputRestrictor.RestrictionInventory inv) {
            if (inv.exactComponent.equals(component)) {
              ItemUtils.tryPlaceItemInInventory(inv.inserted.copy(), handler, false);
            }
          }
        }

        ItemStack stack = ingredient.getItems()[0].copyWithCount(countIOBuffer);

        int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
        if (inserted > 0) {
          context.addRestriction(new ComponentOutputRestrictor.RestrictionInventory(ItemUtils.copyStackWithSize(stack, inserted), component));
        }
        this.countIOBuffer -= inserted;
        if (this.countIOBuffer <= 0) {
          yield CraftCheck.success();
        }
        yield CraftCheck.failure("craftcheck.failure.item.output.space");
      }
    };
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IOInventory handler = (IOInventory) component.providedComponent();
    float productionChance = RecipeModifier.applyModifiers(context, this, this.chance, true);
    if (Objects.requireNonNull(getActionType()) == IOType.INPUT) {
      int required = Math.round(RecipeModifier.applyModifiers(context, this, this.ingredient.count(), false));
      for (ItemStack stack : ingredient.getItems()) {
        stack = stack.copyWithCount(required);
        boolean can = ItemUtils.consumeFromInventory(handler, stack, true);
        if (chance.canProduce(productionChance)) {
          return can;
        }
        if (can)
          return ItemUtils.consumeFromInventory(handler, stack, false);
      }
    }
    return false;
  }

  @Override
  @Nonnull
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IOInventory handler = (IOInventory) component.providedComponent();
    if (Objects.requireNonNull(getActionType()) == IOType.OUTPUT) {
      if (ingredient.getItems().length > 1)
        throw new IllegalStateException("Invalid item output: can not accept tags");
      if (ingredient.getItems()[0].isEmpty() || ingredient.getItems()[0].is(Items.AIR))
        throw new IllegalStateException("Invalid item output: can not be empty or air");
      ItemStack stack = ingredient.getItems()[0].copyWithCount(this.countIOBuffer);

      if (stack.isEmpty()) {
        return CraftCheck.success(); //Can't find anything to output. Guess that's a valid state.
      }
      //If we don't produce the item, we only need to see if there would be space for it at all.
      int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
      if (inserted > 0 && chance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true))) {
        return CraftCheck.success();
      }
      if (inserted > 0) {
        int actual = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, false);
        this.countIOBuffer -= actual;
        if (this.countIOBuffer <= 0) {
          return CraftCheck.success();
        }
        return CraftCheck.partialSuccess();
      }
      return CraftCheck.failure("craftcheck.failure.item.output.space");
    }
    return CraftCheck.skipComponent();
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
