package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.FluidIngredient;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiFluidComponent;
import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.ResultChance;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@SuppressWarnings("unchecked")
public class RequirementFluid extends ComponentRequirement<FluidStack, RequirementFluid> implements ComponentRequirement.ChancedRequirement {
  public static final NamedMapCodec<RequirementFluid> CODEC = NamedCodec.record(instance -> instance.group(
    FluidIngredient.CODEC.fieldOf("fluid").forGetter(req -> req.ingredient),
    NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
    NamedCodec.INT.optionalFieldOf("amount").forGetter(req -> Optional.of(req.amount)),
    NamedCodec.floatRange(0, 1).optionalFieldOf("chance", 1f).forGetter(req -> req.chance),
    NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt", new CompoundTag()).forGetter(RequirementFluid::getTagMatch),
    NamedCodec.of(CompoundTag.CODEC).optionalFieldOf("nbt-display").forGetter(req -> Optional.ofNullable(req.getTagDisplay()))
  ).apply(instance, (fluid, mode, amount, chance, nbt, nbt_display) -> {
    RequirementFluid requirementFluid = new RequirementFluid(mode, fluid, amount.orElse(1000));
    requirementFluid.setChance(chance);
    requirementFluid.setMatchNBTTag(nbt);
    requirementFluid.setDisplayNBTTag(nbt_display.orElse(nbt));
    return requirementFluid;
  }), "FluidRequirement");

  public final HybridFluid required;
  public float chance = 1F;
  public final int amount;

  private HybridFluid requirementCheck;
  private boolean doesntConsumeInput;
  private final FluidIngredient ingredient;

  private CompoundTag tagMatch = new CompoundTag(), tagDisplay = new CompoundTag();

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("fluid").toString());
    json.addProperty("fluid", required.asFluidStack().getHoverName().getString());
    json.addProperty("amount", required.asFluidStack().getAmount());
    json.addProperty("chance", chance);
    json.addProperty("nbt", tagMatch.getAsString());
    json.addProperty("nbt-display", tagDisplay.getAsString());
    return json;
  }

  @Override
  public JeiFluidComponent jeiComponent() {
    return new JeiFluidComponent(this);
  }

  public RequirementFluid(IOType ioType, FluidIngredient fluid, int amount) {
    this(RequirementTypeRegistration.FLUID.get(), ioType, fluid, amount);
  }

  private RequirementFluid(RequirementType<RequirementFluid> type, IOType ioType, FluidIngredient fluid, int amount) {
    super(type, ioType);
    this.ingredient = fluid;
    this.required = new HybridFluid(new FluidStack(fluid.getAll().getFirst(), amount));
    this.amount = amount;
  }

//    public static RequirementFluid createMekanismGasRequirement(RequirementTypeFluid type, IOType ioType, GasStack gasStack) {
//        return new RequirementFluid(type, ioType, new HybridFluidGas(gasStack));
//    }

  public int getSortingWeight() {
    return PRIORITY_WEIGHT_FLUID;
  }

  @Override
  public RequirementFluid deepCopy() {
    RequirementFluid fluid = new RequirementFluid(this.getActionType(), new FluidIngredient(ingredient.getAll().getFirst()), amount);
    fluid.chance = this.chance;
    fluid.tagMatch = getTagMatch();
    fluid.tagDisplay = getTagDisplay();
    return fluid;
  }

  @Override
  public RequirementFluid deepCopyModified(List<RecipeModifier> modifiers) {
    int amount = Math.round(RecipeModifier.applyModifiers(modifiers, this, this.amount, false));
    RequirementFluid fluid = new RequirementFluid(this.getActionType(), new FluidIngredient(ingredient.getAll().getFirst()), amount);

    fluid.chance = RecipeModifier.applyModifiers(modifiers, this, this.chance, true);
    fluid.tagMatch = getTagMatch();
    fluid.tagDisplay = getTagDisplay();
    return fluid;
  }

  public void setMatchNBTTag(@Nullable CompoundTag tag) {
    this.tagMatch = tag;
  }

  @Nullable
  public CompoundTag getTagMatch() {
    return tagMatch.copy();
  }

  public void setDisplayNBTTag(@Nullable CompoundTag tag) {
    this.tagDisplay = tag;
  }

  @Nullable
  public CompoundTag getTagDisplay() {
    return tagDisplay.copy();
  }

  @Override
  public void setChance(float chance) {
    this.chance = chance;
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
    this.requirementCheck = this.required.copy();
    this.requirementCheck.setAmount(Math.round(RecipeModifier.applyModifiers(context, this, this.requirementCheck.getAmount(), false)));
    this.doesntConsumeInput = contextChance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true));
  }

  @Override
  public void endRequirementCheck() {
    this.requirementCheck = this.required.copy();
    this.doesntConsumeInput = true;
  }

  @Nonnull
  @Override
  public String getMissingComponentErrorMessage(IOType ioType) {
    return String.format("component.missing.fluid.%s", ioType.name().toLowerCase());
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return (cmp.getComponentType().equals(ComponentRegistration.COMPONENT_FLUID.get()) || cmp.getComponentType().equals(ComponentRegistration.COMPONENT_GAS.get())) &&
      cmp instanceof MachineComponent.FluidHatch &&
      cmp.getIOType() == this.getActionType();
  }

  @Nonnull
  @Override
  public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor> restrictions) {
    HybridTank handler = (HybridTank) component.providedComponent();

//        if(Mods.MEKANISM.isPresent()) {
//            Optional<CraftCheck> check = checkStartCraftingWithMekanism(component, context, handler, restrictions);
//            if (check.isPresent()) {
//                return check.get();
//            }
//        }

    return switch (getActionType()) {
      case INPUT -> {
        //If it doesn't consume the item, we only need to see if it's actually there.
        FluidStack drained = handler.drain(this.requirementCheck.copy().asFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
          yield CraftCheck.failure("craftcheck.failure.fluid.input");
        }
        if (FluidStack.isSameFluidSameComponents(drained, required.copy().asFluidStack())) {
          yield CraftCheck.failure("craftcheck.failure.fluid.input");
        }
        this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - drained.getAmount(), 0));
        if (this.requirementCheck.getAmount() <= 0) {
          yield CraftCheck.success();
        }
        yield CraftCheck.failure("craftcheck.failure.fluid.input");
      }
      case OUTPUT -> {
        handler = CopyHandlerHelper.copyTank(handler, context.getMachineController().getLevel().registryAccess());

        for (ComponentOutputRestrictor restrictor : restrictions) {
          if (restrictor instanceof ComponentOutputRestrictor.RestrictionTank) {
            ComponentOutputRestrictor.RestrictionTank tank = (ComponentOutputRestrictor.RestrictionTank) restrictor;

            if (tank.exactComponent.equals(component)) {
              handler.fill(Objects.requireNonNull(tank.inserted == null ? null : tank.inserted.copy().asFluidStack()), IFluidHandler.FluidAction.SIMULATE);
            }
          }
        }
        int filled = handler.fill(Objects.requireNonNull(this.requirementCheck.copy().asFluidStack()), IFluidHandler.FluidAction.EXECUTE); //True or false doesn't really matter tbh
        boolean didFill = filled >= this.requirementCheck.getAmount();
        if (didFill) {
          context.addRestriction(new ComponentOutputRestrictor.RestrictionTank(this.requirementCheck.copy(), component));
        }
        if (didFill) {
          yield CraftCheck.success();
        }
        yield CraftCheck.failure("craftcheck.failure.fluid.output.space");
      }
    };
//    return CraftCheck.skipComponent();
  }

//    @net.minecraftforge.fml.common.Optional.Method(modid = "mekanism")
//    private Optional<CraftCheck> checkStartCraftingWithMekanism(ProcessingComponent<?> component,
//                                                                RecipeCraftingContext context,
//                                                                HybridTank handler,
//                                                                List<ComponentOutputRestrictor> restrictions) {
//        if(handler instanceof HybridGasTank) {
//            HybridGasTank gasTank = (HybridGasTank) handler;
//            switch (getActionType()) {
//                case INPUT:
//                    if(this.requirementCheck instanceof HybridFluidGas) {
//                        GasStack drained = gasTank.drawGas(EnumFacing.UP, this.requirementCheck.getAmount(), false);
//                        if(drained == null) {
//                            return Optional.of(CraftCheck.failure("craftcheck.failure.gas.input"));
//                        }
//                        if(drained.getGas() != ((HybridFluidGas) this.requirementCheck).asGasStack().getGas()) {
//                            return Optional.of(CraftCheck.failure("craftcheck.failure.gas.input"));
//                        }
//                        this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - drained.amount, 0));
//                        if(this.requirementCheck.getAmount() <= 0) {
//                            return Optional.of(CraftCheck.success());
//                        }
//                        return Optional.of(CraftCheck.failure("craftcheck.failure.gas.input"));
//                    }
//                    break;
//                case OUTPUT:
//                    if(this.requirementCheck instanceof HybridFluidGas) {
//                        gasTank = (HybridGasTank) CopyHandlerHelper.copyTank(gasTank);
//
//                        for (ComponentOutputRestrictor restrictor : restrictions) {
//                            if(restrictor instanceof ComponentOutputRestrictor.RestrictionTank) {
//                                ComponentOutputRestrictor.RestrictionTank tank = (ComponentOutputRestrictor.RestrictionTank) restrictor;
//
//                                if(tank.exactComponent.equals(component) && tank.inserted instanceof HybridFluidGas) {
//                                    gasTank.receiveGas(EnumFacing.UP, ((HybridFluidGas) this.requirementCheck).asGasStack(), true);
//                                }
//                            }
//                        }
//
//                        int gasFilled = gasTank.receiveGas(EnumFacing.UP, ((HybridFluidGas) this.requirementCheck).asGasStack(), false);
//                        boolean didFill = gasFilled >= this.requirementCheck.getAmount();
//                        if(didFill) {
//                            context.addRestriction(new ComponentOutputRestrictor.RestrictionTank(this.requirementCheck.copy(), component));
//                        }
//                        if(didFill) {
//                            return Optional.of(CraftCheck.success());
//                        }
//                        return Optional.of(CraftCheck.failure("craftcheck.failure.gas.output.space"));
//                    }
//            }
//        }
//        return Optional.empty();
//    }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    HybridTank handler = (HybridTank) component.providedComponent();
    switch (getActionType()) {
      case INPUT:
//                if(Mods.MEKANISM.isPresent()) {
//                    return startCraftingWithMekanismHandling(handler, chance);
//                }
//
//                //If it doesn't consume the item, we only need to see if it's actually there.
        FluidStack drainedSimulated = handler.drain(this.requirementCheck.copy().asFluidStack(), IFluidHandler.FluidAction.SIMULATE);
        if (drainedSimulated.isEmpty()) {
          return false;
        }
        if (FluidStack.isSameFluidSameComponents(drainedSimulated, required.copy().asFluidStack())) {
          return false;
        }
        if (this.doesntConsumeInput) {
          this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - drainedSimulated.getAmount(), 0));
          return this.requirementCheck.getAmount() <= 0;
        }
        FluidStack actualDrained = handler.drain(this.requirementCheck.copy().asFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        if (actualDrained.isEmpty()) {
          return false;
        }
        if (FluidStack.isSameFluidSameComponents(actualDrained, required.copy().asFluidStack())) {
          return false;
        }
        this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - actualDrained.getAmount(), 0));
        return this.requirementCheck.getAmount() <= 0;
    }
    return false;
  }

//    @net.minecraftforge.fml.common.Optional.Method(modid = "mekanism")
//    private boolean startCraftingWithMekanismHandling(HybridTank handler, ResultChance chance) {
//        if(this.requirementCheck instanceof HybridFluidGas && handler instanceof HybridGasTank) {
//            HybridGasTank gasHandler = (HybridGasTank) handler;
//
//            GasStack drainSimulated = gasHandler.drawGas(EnumFacing.UP, this.requirementCheck.getAmount(), false);
//            if(drainSimulated == null) {
//                return false;
//            }
//            if(drainSimulated.getGas() != ((HybridFluidGas) this.requirementCheck).asGasStack().getGas()) {
//                return false;
//            }
//            if(this.doesntConsumeInput) {
//                this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - drainSimulated.amount, 0));
//                return this.requirementCheck.getAmount() <= 0;
//            }
//            GasStack actualDrain = gasHandler.drawGas(EnumFacing.UP, this.requirementCheck.getAmount(), true);
//            if(actualDrain == null) {
//                return false;
//            }
//            if(actualDrain.getGas() != ((HybridFluidGas) this.requirementCheck).asGasStack().getGas()) {
//                return false;
//            }
//            this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - actualDrain.amount, 0));
//            return this.requirementCheck.getAmount() <= 0;
//        } else {
//            FluidStack drainedSimulated = handler.drainInternal(this.requirementCheck.copy().asFluidStack(), false);
//            if(drainedSimulated == null) {
//                return false;
//            }
//            if(!NBTMatchingHelper.matchNBTCompound(this.tagMatch, drainedSimulated.tag)) {
//                return false;
//            }
//            if(this.doesntConsumeInput) {
//                this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - drainedSimulated.amount, 0));
//                return this.requirementCheck.getAmount() <= 0;
//            }
//            FluidStack actualDrained = handler.drainInternal(this.requirementCheck.copy().asFluidStack(), true);
//            if(actualDrained == null) {
//                return false;
//            }
//            if(!NBTMatchingHelper.matchNBTCompound(this.tagMatch, actualDrained.tag)) {
//                return false;
//            }
//            this.requirementCheck.setAmount(Math.max(this.requirementCheck.getAmount() - actualDrained.amount, 0));
//            return this.requirementCheck.getAmount() <= 0;
//        }
//    }

  @Override
  @Nonnull
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    HybridTank handler = (HybridTank) component.providedComponent();
    switch (getActionType()) {
      case OUTPUT:
//                if(Mods.MEKANISM.isPresent()) {
//                    return finishWithMekanismHandling(handler, context, chance);
//                } else {
        FluidStack outStack = this.requirementCheck.asFluidStack();
        if (outStack != null) {
          int fillableAmount = handler.fill(outStack.copy(), IFluidHandler.FluidAction.SIMULATE);
          if (chance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true))) {
            if (fillableAmount >= outStack.getAmount()) {
              return CraftCheck.success();
            }
            return CraftCheck.failure("craftcheck.failure.fluid.output.space");
          }
          FluidStack copyOut = outStack.copy();
          if (this.tagDisplay != null) {
//            copyOut.setTag(this.tagDisplay.copy());
          }
          if (fillableAmount >= outStack.getAmount() && handler.fill(copyOut.copy(), IFluidHandler.FluidAction.EXECUTE) >= copyOut.getAmount()) {
            return CraftCheck.success();
          }
          return CraftCheck.failure("craftcheck.failure.fluid.output.space");
        }
//                }
    }
    return CraftCheck.skipComponent();
  }

//    @net.minecraftforge.fml.common.Optional.Method(modid = "mekanism")
//    @Nonnull
//    private CraftCheck finishWithMekanismHandling(HybridTank handler, RecipeCraftingContext context, ResultChance chance) {
//        if (this.requirementCheck instanceof HybridFluidGas && handler instanceof HybridGasTank) {
//            GasStack gasOut = ((HybridFluidGas) this.requirementCheck).asGasStack();
//            HybridGasTank gasTankHandler = (HybridGasTank) handler;
//            int fillableGas = gasTankHandler.receiveGas(EnumFacing.UP, gasOut, false);
//            if (fillableGas < gasOut.amount) {
//                return CraftCheck.failure("craftcheck.failure.gas.output.space");
//            }
//            if (chance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true))) {
//                return CraftCheck.success();
//            }
//            if (gasTankHandler.receiveGas(EnumFacing.UP, gasOut, true) >= gasOut.amount) {
//                return CraftCheck.success();
//            }
//            return CraftCheck.failure("craftcheck.failure.gas.output.space");
//        } else {
//            FluidStack outStack = this.requirementCheck.asFluidStack();
//            if(outStack != null) {
//                int fillableAmount = handler.fillInternal(outStack.copy(), false);
//                if (fillableAmount < outStack.amount) {
//                    return CraftCheck.failure("craftcheck.failure.gas.output.space");
//                }
//                if(chance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true))) {
//                    return CraftCheck.success();
//                }
//                FluidStack copyOut = outStack.copy();
//                if(this.tagDisplay != null ){
//                    copyOut.tag = this.tagDisplay.copy();
//                }
//                if (handler.fillInternal(copyOut.copy(), true) >= copyOut.amount) {
//                    return CraftCheck.success();
//                }
//                return CraftCheck.failure("craftcheck.failure.gas.output.space");
//            }
//        }
//        return CraftCheck.skipComponent();
//    }

  @Override
  public String toString() {
    return asJson().toString();
  }

}
