package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RequirementItem extends ComponentRequirement<ItemStack, RequirementItem> implements ComponentRequirement.ChancedRequirement {
  public static final NamedMapCodec<RequirementItem> CODEC = NamedCodec.record(instance -> instance.group(
    DefaultCodecs.ITEM_OR_STACK.fieldOf("item").forGetter(requirement -> requirement.required),
    NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
    NamedCodec.INT.optionalFieldOf("amount").forGetter(req -> Optional.of(req.required.getCount())),
    NamedCodec.floatRange(0, 1).optionalFieldOf("chance", 1f).forGetter(req -> req.chance),
    NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt", new CompoundTag()).forGetter(req -> req.tag),
    NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt-display").forGetter(req -> Optional.ofNullable(req.previewDisplayTag))
  ).apply(instance, (item, mode, amount, chance, nbt, nbt_display) -> {
    amount.ifPresent(item::setCount);
    RequirementItem requirementItem = new RequirementItem(mode, item);
    requirementItem.setChance(chance);
    requirementItem.tag = nbt;
    requirementItem.previewDisplayTag = nbt_display.orElse(nbt);
    return requirementItem;
  }), "RequirementItem");

  public final ItemRequirementType requirementType;

  public final ItemStack required;

  public final String oreDictName;
  public final int oreDictItemAmount;

  public final int fuelBurntime;

  public int countIOBuffer = 0;

  public CompoundTag tag = null;
  public CompoundTag previewDisplayTag = null;

  public float chance = 1F;

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("item").toString());
    json.addProperty("item", required.getHoverName().getString());
    json.addProperty("count", required.getCount());
    json.addProperty("chance", chance);
    json.addProperty("nbt", tag.getAsString());
    json.addProperty("nbt-display", previewDisplayTag.getAsString());
    return json;
  }

  public RequirementItem(IOType ioType, ItemStack item) {
    super(RequirementTypeRegistration.ITEM.get(), ioType);
    this.requirementType = ItemRequirementType.ITEMSTACKS;
    this.required = item.copy();
    this.oreDictName = null;
    this.oreDictItemAmount = 0;
    this.fuelBurntime = 0;
  }

  public RequirementItem(IOType ioType, String oreDictName, int oreDictAmount) {
    super(RequirementTypeRegistration.ITEM.get(), ioType);
    this.requirementType = ItemRequirementType.TAG;
    this.oreDictName = oreDictName;
    this.oreDictItemAmount = oreDictAmount;
    this.required = ItemStack.EMPTY;
    this.fuelBurntime = 0;
  }

  public RequirementItem(IOType actionType, int fuelBurntime) {
    super(RequirementTypeRegistration.ITEM.get(), actionType);
    this.requirementType = ItemRequirementType.FUEL;
    this.fuelBurntime = fuelBurntime;
    this.oreDictName = null;
    this.oreDictItemAmount = 0;
    this.required = ItemStack.EMPTY;
  }

  @Override
  public int getSortingWeight() {
    return PRIORITY_WEIGHT_ITEM;
  }

  @Override
  public ComponentRequirement<ItemStack, RequirementItem> deepCopy() {
    RequirementItem item = switch (this.requirementType) {
      case TAG -> new RequirementItem(this.getActionType(), this.oreDictName, this.oreDictItemAmount);
      case FUEL -> new RequirementItem(this.getActionType(), this.fuelBurntime);
      default -> new RequirementItem(this.getActionType(), this.required.copy());
    };
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
    RequirementItem item;
    switch (this.requirementType) {
      case TAG:
        int inOreAmt = Math.round(RecipeModifier.applyModifiers(modifiers, this.getRequirementType(), getActionType(), this.oreDictItemAmount, false));
        item = new RequirementItem(this.getActionType(), this.oreDictName, inOreAmt);
        break;
      case FUEL:
        int inFuel = Math.round(RecipeModifier.applyModifiers(modifiers, this.getRequirementType(), getActionType(), this.fuelBurntime, false));
        item = new RequirementItem(this.getActionType(), inFuel);
        break;
      default:
      case ITEMSTACKS:
        ItemStack inReq = this.required.copy();
        int amt = Math.round(RecipeModifier.applyModifiers(modifiers, this.getRequirementType(), getActionType(), inReq.getCount(), false));
        inReq.setCount(amt);
        item = new RequirementItem(this.getActionType(), inReq);
        break;
    }

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
  public JEIComponent<ItemStack> provideJEIComponent() {
//    return new JEIComponentItem(this);
    return null;
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
    switch (this.requirementType) {
      case ITEMSTACKS:
        this.countIOBuffer = this.required.getCount();
        break;
      case TAG:
        this.countIOBuffer = this.oreDictItemAmount;
        break;
      case FUEL:
        this.countIOBuffer = this.fuelBurntime;
        break;
    }
    this.countIOBuffer = Math.round(RecipeModifier.applyModifiers(context, this, this.countIOBuffer, false));
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
    switch (getActionType()) {
      case INPUT:
        switch (this.requirementType) {
          case ITEMSTACKS:
            ItemStack inReq = this.required.copy();
            int amt = Math.round(RecipeModifier.applyModifiers(context, this, inReq.getCount(), false));
            inReq.setCount(amt);
            if (ItemUtils.consumeFromInventory(handler, inReq, true, this.tag)) {
              return CraftCheck.success();
            }
            break;
          case TAG:
            int inOreAmt = Math.round(RecipeModifier.applyModifiers(context, this, this.oreDictItemAmount, false));
            if (ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, inOreAmt, true, this.tag)) {
              return CraftCheck.success();
            }
            break;
          case FUEL:
            int inFuel = Math.round(RecipeModifier.applyModifiers(context, this, this.fuelBurntime, false));
            if (ItemUtils.consumeFromInventoryFuel(handler, inFuel, true, this.tag) <= 0) {
              return CraftCheck.success();
            }
            break;
        }
        return CraftCheck.failure("craftcheck.failure.item.input");
      case OUTPUT:
        handler = CopyHandlerHelper.copyInventory(handler, context.getMachineController().getLevel().registryAccess());
        for (ComponentOutputRestrictor restrictor : restrictions) {
          if (restrictor instanceof ComponentOutputRestrictor.RestrictionInventory inv) {
            if (inv.exactComponent.equals(component)) {
              ItemUtils.tryPlaceItemInInventory(inv.inserted.copy(), handler, false);
            }
          }
        }

        ItemStack stack = ItemStack.EMPTY;
        if (oreDictName != null) {
//          for (ItemStack oreInstance : OreDictionary.getOres(oreDictName)) {
//            if (!oreInstance.isEmpty()) {
//              stack = ItemUtils.copyStackWithSize(oreInstance, this.countIOBuffer);
//
//              if (!stack.isEmpty()) { //Try all options first..
//                break;
//              }
//            }
//          }

          if (this.countIOBuffer > 0 && stack.isEmpty()) {
            throw new IllegalArgumentException("Unknown ItemStack: Cannot find an item in oredict '" + oreDictName + "'!");
          }
        } else {
          stack = ItemUtils.copyStackWithSize(required, this.countIOBuffer);
        }

        if (tag != null) {
//          stack.setTag(tag.copy());
        }
        int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
        if (inserted > 0) {
          context.addRestriction(new ComponentOutputRestrictor.RestrictionInventory(ItemUtils.copyStackWithSize(stack, inserted), component));
        }
        this.countIOBuffer -= inserted;
        if (this.countIOBuffer <= 0) {
          return CraftCheck.success();
        }
        return CraftCheck.failure("craftcheck.failure.item.output.space");
    }
    return CraftCheck.skipComponent();
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IOInventory handler = (IOInventory) component.providedComponent();
    float productionChance = RecipeModifier.applyModifiers(context, this, this.chance, true);
    if (Objects.requireNonNull(getActionType()) == IOType.INPUT) {
      switch (this.requirementType) {
        //If it doesn't consume the item, we only need to see if it's actually there.
        case ITEMSTACKS:
          ItemStack stackRequired = this.required.copy();
          int amt = Math.round(RecipeModifier.applyModifiers(context, this, stackRequired.getCount(), false));
          stackRequired.setCount(amt);
          boolean can = ItemUtils.consumeFromInventory(handler, stackRequired, true, this.tag);
          if (chance.canProduce(productionChance)) {
            return can;
          }
          return can && ItemUtils.consumeFromInventory(handler, stackRequired, false, this.tag);
        case TAG:
          int requiredOredict = Math.round(RecipeModifier.applyModifiers(context, this, this.oreDictItemAmount, false));
          can = ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, requiredOredict, true, this.tag);
          if (chance.canProduce(productionChance)) {
            return can;
          }
          return can && ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, requiredOredict, false, this.tag);
        case FUEL:
          int requiredBurnTime = Math.round(RecipeModifier.applyModifiers(context, this, this.fuelBurntime, false));
          can = ItemUtils.consumeFromInventoryFuel(handler, requiredBurnTime, true, this.tag) <= 0;
          if (chance.canProduce(productionChance)) {
            return can;
          }
          if (!can) return false;
          ItemUtils.consumeFromInventoryFuel(handler, requiredBurnTime, false, this.tag);
          return true;
      }
    }
    return false;
  }

  @Override
  @Nonnull
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    if (fuelBurntime > 0 && oreDictName == null && required.isEmpty()) {
      throw new IllegalStateException("Invalid item output!");
    }
    IOInventory handler = (IOInventory) component.providedComponent();
    if (Objects.requireNonNull(getActionType()) == IOType.OUTPUT) {
      ItemStack stack;
      if (oreDictName != null) {
        stack = Iterables.getFirst(Collections.singleton(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(oreDictName)).getDefaultInstance()), ItemStack.EMPTY);
        stack = ItemUtils.copyStackWithSize(stack, this.countIOBuffer);
      } else {
        stack = ItemUtils.copyStackWithSize(required, this.countIOBuffer);
      }

      if (stack.isEmpty()) {
        return CraftCheck.success(); //Can't find anything to output. Guess that's a valid state.
      }
      if (tag != null) {
//        stack.setTag(tag);
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
    return "RequirementItem{" +
      "item=" + required.getDisplayName() +
      ", actionType=" + getActionType() +
      ", requirementType=" + getRequirementType() +
      '}';
  }

  public enum ItemRequirementType {
    ITEMSTACKS,
    TAG,
    FUEL
  }
}
