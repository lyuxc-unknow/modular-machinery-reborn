package es.degrassi.mmreborn.common.integration.jei.category;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.item.ItemBlueprint;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CategoryDynamicRecipe implements IRecipeCategory<DynamicRecipeWrapper> {
  private final DynamicMachine machine;
  private final String category;
  private final String title;
  private final IDrawable sizeEmptyDrawable;

  final int realHeight;

  LinkedList<RecipeLayoutPart<?>> inputComponents = Lists.newLinkedList();
  LinkedList<RecipeLayoutPart<?>> outputComponents = Lists.newLinkedList();

  private Point offsetProcessArrow;
  Rectangle rectangleProcessArrow;

  public CategoryDynamicRecipe(DynamicMachine machine) {
    this.machine = machine;
    this.category = MMRJeiPlugin.getCategoryStringFor(machine);
    this.title = machine.getLocalizedName();

    Point maxPoint = buildRecipeComponents();
    this.realHeight = maxPoint.y;
    this.sizeEmptyDrawable = MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(maxPoint.x, this.realHeight);
  }

  private Point buildRecipeComponents() {
//    if (ModularMachineryReborn.RECIPES.get(this.machine) == null) return new Point(0, 0);
    Iterable<MachineRecipe> recipes = Optional
      .ofNullable(Minecraft.getInstance().level)
      .map(level -> level
        .getRecipeManager()
        .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
        .stream()
        .map(RecipeHolder::value)
        .filter(recipe -> recipe.getOwningMachine() != null)
        .filter(recipe -> recipe.getOwningMachine().equals(machine))
        .toList()
      ).orElse(List.of());
//    Iterable<MachineRecipe> recipes = Optional.ofNullable(MachineRecipe.RECIPES.get(machine)).orElse(List.of());

    Map<IOType, Map<Class<?>, Integer>> componentCounts = new HashMap<>();
    Map<Class<?>, ComponentRequirement.JEIComponent<?>> componentsFound = new HashMap<>();
    Font font = Minecraft.getInstance().font;
    int offsetX = 8;
    int offsetY = 0;
    int highestY = 0;
    int longestTooltip = 0;
    int widestTooltip = 0;

    for (MachineRecipe recipe : recipes) {
      Map<IOType, Map<Class<?>, Integer>> tempComp = new HashMap<>();
      for (ComponentRequirement<?, ?> req : recipe.getCraftingRequirements()) {
        ComponentRequirement.JEIComponent<?> jeiComp = req.provideJEIComponent();
        int amt = tempComp.computeIfAbsent(req.getActionType(), ioType -> new HashMap<>())
          .computeIfAbsent(jeiComp.getJEIRequirementClass(), clazz -> 0);
        amt++;
        tempComp.get(req.getActionType()).put(jeiComp.getJEIRequirementClass(), amt);


        if (!componentsFound.containsKey(jeiComp.getJEIRequirementClass())) {
          componentsFound.put(jeiComp.getJEIRequirementClass(), jeiComp);
        }
      }
      for (Map.Entry<IOType, Map<Class<?>, Integer>> cmpEntry : tempComp.entrySet()) {
        for (Map.Entry<Class<?>, Integer> cntEntry : cmpEntry.getValue().entrySet()) {
          int current = componentCounts.computeIfAbsent(cmpEntry.getKey(), ioType -> new HashMap<>())
            .computeIfAbsent(cntEntry.getKey(), clazz -> 0);
          if (cntEntry.getValue() > current) {
            componentCounts.get(cmpEntry.getKey()).put(cntEntry.getKey(), cntEntry.getValue());
          }
        }
      }

      int tipLength = 0;
//            for (RequirementTip tip : RegistriesMM.REQUIREMENT_TIPS_REGISTRY) {
//                Collection<ComponentRequirement<?, ?>> requirements = tip.filterRequirements(recipe, recipe.getCraftingRequirements());
//                if (!requirements.isEmpty()) {
//                    List<String> tooltip = tip.buildTooltip(recipe, requirements);
//                    if (!tooltip.isEmpty()) {
//                        for (String tipString : tooltip) {
//                            int length = fr.getStringWidth(tipString);
//                            if (length > widestTooltip) {
//                                widestTooltip = length;
//                            }
//                        }
//                        tipLength += RequirementTip.LINE_HEIGHT * tooltip.size();
//                        tipLength += RequirementTip.SPLIT_HEIGHT;
//                    }
//                }
//            }
      if (tipLength > longestTooltip) {
        longestTooltip = tipLength;
      }
    }

    List<Class<?>> classes = Lists.newLinkedList(componentsFound.keySet());
    classes.sort((o1, o2) -> {
      RecipeLayoutPart<?> part1 = componentsFound.get(o1).getTemplateLayout();
      RecipeLayoutPart<?> part2 = componentsFound.get(o2).getTemplateLayout();
      return part2.getComponentHorizontalSortingOrder() - part1.getComponentHorizontalSortingOrder();
    });

    for (Class<?> clazz : classes) {
      Map<Class<?>, Integer> compMap = componentCounts.get(IOType.INPUT);
      if (compMap != null && compMap.containsKey(clazz)) {
        ComponentRequirement.JEIComponent<?> component = componentsFound.get(clazz);
        RecipeLayoutPart<?> layoutHelper = component.getTemplateLayout();
        int amt = compMap.get(clazz);

        int partOffsetX = offsetX;
        int originalOffsetX = offsetX;
        int partOffsetY = offsetY;
        for (int i = 0; i < amt; i++) {
          if (i > 0 && i % layoutHelper.getMaxHorizontalCount() == 0) {
            partOffsetY += layoutHelper.getComponentHeight() + layoutHelper.getComponentVerticalGap();
            partOffsetX = originalOffsetX;
          }
          inputComponents.add(component.getLayoutPart(new Point(partOffsetX, partOffsetY)));
          partOffsetX += layoutHelper.getComponentWidth() + layoutHelper.getComponentHorizontalGap();
          if (partOffsetX > offsetX) {
            offsetX = partOffsetX;
          }
          if (partOffsetY + layoutHelper.getComponentHeight() > highestY) {
            highestY = partOffsetY + layoutHelper.getComponentHeight();
          }
        }
      }
    }

    offsetX += 4;
    int tempArrowOffsetX = offsetX;
    offsetX += RecipeLayoutHelper.PART_PROCESS_ARROW.xSize;
    offsetX += 4;

    classes = Lists.newLinkedList(componentsFound.keySet());
    classes.sort((o1, o2) -> {
      RecipeLayoutPart<?> part1 = componentsFound.get(o1).getTemplateLayout();
      RecipeLayoutPart<?> part2 = componentsFound.get(o2).getTemplateLayout();
      return part1.getComponentHorizontalSortingOrder() - part2.getComponentHorizontalSortingOrder();
    });

    for (Class<?> clazz : classes) {
      Map<Class<?>, Integer> compMap = componentCounts.get(IOType.OUTPUT);
      if (compMap != null && compMap.containsKey(clazz)) {
        ComponentRequirement.JEIComponent<?> component = componentsFound.get(clazz);
        RecipeLayoutPart<?> layoutHelper = component.getTemplateLayout();
        int amt = compMap.get(clazz);

        int partOffsetX = offsetX;
        int originalOffsetX = offsetX;
        int partOffsetY = offsetY;
        for (int i = 0; i < amt; i++) {
          if (i > 0 && i % layoutHelper.getMaxHorizontalCount() == 0) {
            partOffsetY += layoutHelper.getComponentHeight() + layoutHelper.getComponentVerticalGap();
            partOffsetX = originalOffsetX;
          }
          outputComponents.add(component.getLayoutPart(new Point(partOffsetX, partOffsetY)));
          partOffsetX += layoutHelper.getComponentWidth() + layoutHelper.getComponentHorizontalGap();
          if (partOffsetX > offsetX) {
            offsetX = partOffsetX;
          }
          if (partOffsetY + layoutHelper.getComponentHeight() > highestY) {
            highestY = partOffsetY + layoutHelper.getComponentHeight();
          }
        }
      }
    }


    int halfY = highestY / 2;
    offsetProcessArrow = new Point(tempArrowOffsetX, halfY / 2);
    rectangleProcessArrow = new Rectangle(offsetProcessArrow.x, offsetProcessArrow.y,
      RecipeLayoutHelper.PART_PROCESS_ARROW.xSize, RecipeLayoutHelper.PART_PROCESS_ARROW.zSize);

    //Texts for input consumed/produced
    highestY += longestTooltip;

    widestTooltip += 8; //Initial offset
    if (widestTooltip > offsetX) {
      offsetX = widestTooltip;
    }

    return new Point(offsetX, highestY);
  }

  public String getUid() {
    return this.category;
  }

  @Override
  public RecipeType<DynamicRecipeWrapper> getRecipeType() {
    return RecipeType.create(ModularMachineryReborn.MODID, machine.getRegistryName().getPath(), DynamicRecipeWrapper.class);
  }

  @Override
  public Component getTitle() {
    return Component.literal(this.title);
  }

  @Override
  public IDrawable getBackground() {
    return this.sizeEmptyDrawable;
  }

  @Override
  public IDrawable getIcon() {
    ItemStack stack = new ItemStack(ItemRegistration.BLUEPRINT.get());
    stack.set(Registration.MACHINE_DATA, machine.getRegistryName());
    return MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
  }

  public void drawExtras(GuiGraphics minecraft) {
    RecipeLayoutHelper.PART_PROCESS_ARROW.drawable.draw(minecraft, offsetProcessArrow.x, offsetProcessArrow.y);
    for (RecipeLayoutPart slot : this.inputComponents) {
      slot.drawBackground(minecraft);
    }
    for (RecipeLayoutPart slot : this.outputComponents) {
      slot.drawBackground(minecraft);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, DynamicRecipeWrapper recipe, IFocusGroup focuses) {
    List<ComponentRequirement<?, ?>> foundClasses = new LinkedList<>();
    for (IOType type : IOType.values()) {
      for (ComponentRequirement<?, ?> clazz : recipe.getRecipe().getRecipeRequirements()) {
        if (clazz.getRequirementType().equals(RequirementTypeRegistration.ENERGY.get())) { //Nope nope nope, fck you, Energy-component.
          continue;
        }
        if (!foundClasses.contains(clazz)) {
          foundClasses.add(clazz);
        }
      }
    }

    for (ComponentRequirement<?, ?> req : foundClasses) {
      int amtCompInputs = 0;
//            IGuiIngredientGroup<?> clazzGroup = builder..getIngredientsGroup(clazz);
      int compSlotIndex = 0;
      for (RecipeLayoutPart slot : this.inputComponents) {
        builder.addSlot(
            RecipeIngredientRole.INPUT,
            slot.getOffset().x,
            slot.getOffset().y
          )
          .setCustomRenderer(slot, slot.provideIngredientRenderer())
          .addIngredient(slot, req.provideJEIComponent());
//                  .addIngredients(slot.);
//                        compSlotIndex,
//                        true,
//                        slot.provideIngredientRenderer(),
//                        slot.getOffset().x,
//                        slot.getOffset().y,
//                        slot.getComponentWidth(),
//                        slot.getComponentHeight(),
//                        slot.getRendererPaddingX(),
//                        slot.getRendererPaddingY());
        compSlotIndex++;
        amtCompInputs++;
      }
      for (RecipeLayoutPart slot : this.outputComponents) {
        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            slot.getOffset().x,
            slot.getOffset().y
          )
          .setCustomRenderer(slot, slot.provideIngredientRenderer())
          .addIngredient(slot, req.provideJEIComponent())
          .addTooltipCallback((slotView, tooltips) -> {

          });
//                clazzGroup.init(
//                        compSlotIndex,
//                        false,
//                        slot.provideIngredientRenderer(),
//                        slot.getOffset().x,
//                        slot.getOffset().y,
//                        slot.getComponentWidth(),
//                        slot.getComponentHeight(),
//                        slot.getRendererPaddingX(),
//                        slot.getRendererPaddingY());
        compSlotIndex++;
      }

//            clazzGroup.set(ingredients);
      int finalAmtInputs = amtCompInputs;

//            clazzGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
//                Map<Class<?>, List<ComponentRequirement<?, ?>>> components = recipeWrapper.finalOrderedComponents
//                        .get(input ? IOType.INPUT : IOType.OUTPUT);
//                if(components != null) {
//                    List<ComponentRequirement<?, ?>> compList = components.get(clazz);
//
//                    int index = input ? slotIndex : slotIndex - finalAmtInputs;
//                    if(index < 0 || index >= compList.size()) {
//                        return;
//                    }
//                    ComponentRequirement.JEIComponent jeiComp = compList.get(index).provideJEIComponent();
//                    jeiComp.onJEIHoverTooltip(index, input, ingredient, tooltip);
//                }
//            });
    }
  }
}
