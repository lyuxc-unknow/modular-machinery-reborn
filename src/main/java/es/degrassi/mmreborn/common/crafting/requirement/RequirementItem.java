package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.IIngredient;
import es.degrassi.mmreborn.api.ItemIngredient;
import es.degrassi.mmreborn.api.ItemTagIngredient;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiItemComponent;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.common.util.ResultChance;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@SuppressWarnings("unchecked")
public class RequirementItem extends ComponentRequirement<ItemStack, RequirementItem> implements ComponentRequirement.ChancedRequirement {
  public static final NamedCodec<RequirementItem> CODEC =
    NamedCodec.record(instance -> instance.group(
      IIngredient.ITEM.fieldOf("item").forGetter(req -> req.ingredient),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
      NamedCodec.INT.optionalFieldOf("amount").forGetter(req -> Optional.of(req.amount)),
      NamedCodec.floatRange(0, 1).optionalFieldOf("chance", 1f).forGetter(req -> req.chance),
      NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt", new CompoundTag()).forGetter(req -> req.tag),
      NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt-display", new CompoundTag()).forGetter(req -> req.previewDisplayTag)
    ).apply(instance, (item, mode, amount, chance, nbt, nbt_display) -> {
      RequirementItem requirementItem = new RequirementItem(mode, item, amount.orElse(1));
      requirementItem.setChance(chance);
      requirementItem.tag = nbt;
      requirementItem.previewDisplayTag = nbt_display;

      return requirementItem;
    }), "RequirementItem");

  @Getter
  public final IIngredient<Item> ingredient;
  @Getter
  public final int amount;

  public int countIOBuffer = 0;

  public CompoundTag tag = new CompoundTag();
  public CompoundTag previewDisplayTag = new CompoundTag();

  public float chance = 1F;

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("item").toString());
    json.add("ingredient", ingredient.asJson());
    json.addProperty("amount", amount);
    json.addProperty("chance", chance);
    if (tag != null) json.addProperty("nbt", tag.getAsString());
    if (previewDisplayTag != null) json.addProperty("nbt-display", previewDisplayTag.getAsString());
    return json;
  }

  @Override
  public JeiItemComponent jeiComponent() {
    return new JeiItemComponent(this);
  }

  public RequirementItem(IOType ioType, IIngredient<Item> ingredient, int amount) {
    super(RequirementTypeRegistration.ITEM.get(), ioType);
    boolean isTag = ingredient instanceof ItemTagIngredient;
    if (ioType == IOType.OUTPUT && isTag) throw new IllegalArgumentException("Output item can not be a tag");
    this.ingredient = ingredient;
    this.amount = amount;
  }

  @Override
  public int getSortingWeight() {
    return PRIORITY_WEIGHT_ITEM;
  }

  @Override
  public ComponentRequirement<ItemStack, RequirementItem> deepCopy() {
    RequirementItem item = new RequirementItem(getActionType(), getIngredient(), getAmount());
    item.chance = this.chance;
    if (this.tag != null) {
      item.tag = this.tag.copy();
    }
    if (this.previewDisplayTag != null) {
      item.previewDisplayTag = this.previewDisplayTag.copy();
    }
    return item;
  }

  @Override
  public ComponentRequirement<ItemStack, RequirementItem> deepCopyModified(List<RecipeModifier> modifiers) {
    int inAmt = Math.round(RecipeModifier.applyModifiers(modifiers, this.getRequirementType(), getActionType(), getAmount(), false));
    RequirementItem item = new RequirementItem(getActionType(), getIngredient(), inAmt);

    item.chance = RecipeModifier.applyModifiers(modifiers, this, this.chance, true);
    if (this.tag != null) {
      item.tag = this.tag.copy();
    }
    if (this.previewDisplayTag != null) {
      item.previewDisplayTag = this.previewDisplayTag.copy();
    }
    return item;
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
    this.countIOBuffer = Math.round(RecipeModifier.applyModifiers(context, this, this.amount, false));
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
  public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor> restrictions) {
    IOInventory handler = (IOInventory) component.providedComponent();
    return switch (getActionType()) {
      case INPUT -> {
        int amt = Math.round(RecipeModifier.applyModifiers(context, this, this.amount, false));
        if (ingredient instanceof ItemTagIngredient tagIng) {
          if (ItemUtils.consumeFromInventoryOreDict(handler, tagIng.getTag(), amt, true, this.tag)) {
            yield CraftCheck.success();
          }
        } else if (ingredient instanceof ItemIngredient ing) {
          if (ItemUtils.consumeFromInventory(handler, ing.getStack(amt), true, this.tag)) {
            yield CraftCheck.success();
          }
        }
        yield CraftCheck.failure("craftcheck.failure.item.input");
      }
      case OUTPUT -> {
        handler = CopyHandlerHelper.copyInventory(handler, context.getMachineController().getLevel().registryAccess());
        for (ComponentOutputRestrictor restrictor : restrictions) {
          if (restrictor instanceof ComponentOutputRestrictor.RestrictionInventory inv) {
            if (inv.exactComponent.equals(component)) {
              ItemUtils.tryPlaceItemInInventory(inv.inserted.copy(), handler, false);
            }
          }
        }

        ItemStack stack = new ItemStack(ingredient.getAll().get(0), this.countIOBuffer);

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
      if (ingredient instanceof ItemTagIngredient tagIng) {
        int requiredOredict = Math.round(RecipeModifier.applyModifiers(context, this, this.amount, false));
        boolean can = ItemUtils.consumeFromInventoryOreDict(handler, tagIng.getTag(), requiredOredict, true, this.tag);
        if (chance.canProduce(productionChance)) {
          return can;
        }
        return can && ItemUtils.consumeFromInventoryOreDict(handler, tagIng.getTag(), requiredOredict, false, this.tag);
      } else if (ingredient instanceof ItemIngredient ing) {
        ItemStack stackRequired = ing.getStack(this.getAmount());
        int amt = Math.round(RecipeModifier.applyModifiers(context, this, stackRequired.getCount(), false));
        stackRequired.setCount(amt);
        boolean can = ItemUtils.consumeFromInventory(handler, stackRequired, true, this.tag);
        if (chance.canProduce(productionChance)) {
          return can;
        }
        return can && ItemUtils.consumeFromInventory(handler, stackRequired, false, this.tag);
      }
    }
    return false;
  }

  @Override
  @Nonnull
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IOInventory handler = (IOInventory) component.providedComponent();
    if (Objects.requireNonNull(getActionType()) == IOType.OUTPUT) {
      if (ingredient instanceof ItemTagIngredient)
        throw new IllegalStateException("Invalid item output: can not accept tags");
      if (ingredient instanceof ItemIngredient ing) {
        if (ing.getStack(1).isEmpty())
          throw new IllegalStateException("Invalid item output: can not be empty");
        else if (ing.getStack(1).is(Items.AIR))
          throw new IllegalStateException("Invalid item output: can not be air");
        ItemStack stack = ing.getStack(this.countIOBuffer);

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
    }
    return CraftCheck.skipComponent();
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
