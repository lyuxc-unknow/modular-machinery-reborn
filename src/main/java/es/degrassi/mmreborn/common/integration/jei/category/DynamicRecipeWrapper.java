package es.degrassi.mmreborn.common.integration.jei.category;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.client.ClientScheduler;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.machine.IOType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.Getter;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@Getter
public class DynamicRecipeWrapper implements IRecipeCategoryExtension<MachineRecipe> {

  private final MachineRecipe recipe;
  public final Map<IOType, Map<Class<?>, List<ComponentRequirement<?, ?>>>> finalOrderedComponents = new HashMap<>();

  public DynamicRecipeWrapper(MachineRecipe recipe) {
    this.recipe = recipe;

//    for (IOType type : IOType.values()) {
//      finalOrderedComponents.put(type, new HashMap<>());
//    }
    for (ComponentRequirement<?, ?> req : recipe.getCraftingRequirements()) {
      ComponentRequirement.JEIComponent<?> comp = req.provideJEIComponent();
      finalOrderedComponents
        .computeIfAbsent(req.getActionType(), map -> new HashMap<>())
        .computeIfAbsent(comp.getJEIRequirementClass(), clazz -> new LinkedList<>()).add(req);
//      finalOrderedComponents.get(req.getActionType())
//        .computeIfAbsent(comp.getJEIRequirementClass(), clazz -> new LinkedList<>()).add(req);
    }
  }

  @Nonnull
  public List<Component> getTooltipStrings(MachineRecipe recipe, double mouseX, double mouseY) {
    List<Component> tooltips = Lists.newArrayList();
    CategoryDynamicRecipe recipeCategory = MMRJeiPlugin.getCategory(recipe.getOwningMachine());
    if(recipeCategory != null) {
      if(recipeCategory.rectangleProcessArrow.contains(mouseX, mouseY)) {
        tooltips.add(Component.translatable("tooltip.machinery.duration", recipe.getRecipeTotalTickTime()));
      }
    }

    return tooltips;
  }

  public void drawInfo(MachineRecipe recipe, int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    CategoryDynamicRecipe recipeCategory = MMRJeiPlugin.getCategory(recipe.getOwningMachine());
    if(recipeCategory == null) return;

    int totalDur = this.recipe.getRecipeTotalTickTime();
    int tick = (int) (ClientScheduler.getClientTick() % totalDur);
    int pxPart = Mth.ceil(((float) tick/* + Animation.getPartialTickTime()*/) / ((float) totalDur) * RecipeLayoutHelper.PART_PROCESS_ARROW_ACTIVE.xSize);
    MMRJeiPlugin.jeiHelpers.getGuiHelper()
      .createDrawable(RecipeLayoutHelper.LOCATION_JEI_ICONS, 84, 15, pxPart, RecipeLayoutHelper.PART_PROCESS_ARROW_ACTIVE.zSize)
      .draw(guiGraphics, recipeCategory.rectangleProcessArrow.x, recipeCategory.rectangleProcessArrow.y);

    int offsetY = recipeCategory.realHeight;

//        int lineHeight = RequirementTip.LINE_HEIGHT;
//        int splitHeight = RequirementTip.SPLIT_HEIGHT;

    List<List<String>> tooltips = new ArrayList<>();
//        for (RequirementTip tip : RegistriesMM.REQUIREMENT_TIPS_REGISTRY) {
//            Collection<ComponentRequirement<?, ?>> requirements = tip.filterRequirements(this.recipe, this.recipe.getCraftingRequirements());
//            if (!requirements.isEmpty()) {
//                tooltips.add(tip.buildTooltip(this.recipe, requirements));
//            }
//        }

//        for (List<String> tTip : tooltips) {
//            offsetY -= lineHeight * tTip.size();
//            offsetY -= splitHeight;
//        }

    Font font = Minecraft.getInstance().font;
//        for (List<String> tTip : tooltips) {
//            for (String tip : tTip) {
//                guiGraphics.drawString(font, tip, 8, offsetY, 0x222222);
//                offsetY += lineHeight;
//            }
//            offsetY += splitHeight;
//        }


    //TODO Rework this along with the ingredient for energy stuffs
    long totalEnergyIn = 0;
    for (ComponentRequirement req : this.recipe.getCraftingRequirements().stream()
      .filter(r -> r instanceof RequirementEnergy)
      .filter(r -> r.getActionType() == IOType.INPUT)
      .toList()) {
      totalEnergyIn += ((RequirementEnergy) req).getRequiredEnergyPerTick();
    }
    long totalEnergyOut = 0;
    for (ComponentRequirement req : this.recipe.getCraftingRequirements().stream()
      .filter(r -> r instanceof RequirementEnergy)
      .filter(r -> r.getActionType() == IOType.OUTPUT)
      .toList()) {
      totalEnergyOut += ((RequirementEnergy) req).getRequiredEnergyPerTick();
    }

    long finalTotalEnergyIn = totalEnergyIn;
    recipeCategory.inputComponents.stream()
      .filter(r -> r instanceof RecipeLayoutPart.Energy)
      .forEach(part -> ((RecipeLayoutPart.Energy) part).drawEnergy(guiGraphics, finalTotalEnergyIn));
    long finalTotalEnergyOut = totalEnergyOut;
    recipeCategory.outputComponents.stream()
      .filter(r -> r instanceof RecipeLayoutPart.Energy)
      .forEach(part -> ((RecipeLayoutPart.Energy) part).drawEnergy(guiGraphics, finalTotalEnergyOut));
  }

  public void getIngredients() {
    Map<IIngredientType<?>, Map<IOType, List<ComponentRequirement<?, ?>>>> componentMap = new HashMap<>();
    for (ComponentRequirement<?, ?> req : this.recipe.getCraftingRequirements()) {
      if(req instanceof RequirementEnergy) continue; //Ignore. They're handled differently. I should probably rework this...

      ComponentRequirement.JEIComponent<?> comp = req.provideJEIComponent();
//            IIngredientType<?> type = MMRJeiPlugin.ingredientManager.getIngredientType(comp.getJEIRequirementClass());
//            componentMap.computeIfAbsent(type, t -> new HashMap<>())
//                    .computeIfAbsent(req.getActionType(), tt -> new LinkedList<>()).add(req);
    }

    for (IIngredientType<?> type : componentMap.keySet()) {
      Map<IOType, List<ComponentRequirement<?, ?>>> ioGroup = componentMap.get(type);
      for (IOType ioType : ioGroup.keySet()) {
        List<ComponentRequirement<?, ?>> components = ioGroup.get(ioType);
        List<List<Object>> componentObjects = new ArrayList<>(components.size());
        for (ComponentRequirement req : components) {
          componentObjects.add(req.provideJEIComponent().getJEIIORequirements());
        }
        switch (ioType) {
          case INPUT:
//                        ingredients.setInputLists(type, componentObjects);
            break;
          case OUTPUT:
//                        ingredients.setOutputLists(type, componentObjects);
            break;
        }
      }
    }
  }

}
