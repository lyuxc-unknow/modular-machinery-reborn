package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementChemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JeiChemicalComponent extends JeiComponent<ChemicalStack, RequirementChemical> {
  public JeiChemicalComponent(RequirementChemical requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 16;
  }

  @Override
  public int getHeight() {
    return 61;
  }

  @Override
  public List<ChemicalStack> ingredients() {
    return Lists.newArrayList(requirement.required.copy());
  }

  @Override
  @SuppressWarnings("removal")
  public @NotNull List<Component> getTooltip(@NotNull ChemicalStack ingredient, @NotNull TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
    String mode = requirement.getActionType().isInput() ? "input" : "output";
    tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.chemical." + mode, ingredient.getTextComponent(), ingredient.getAmount()));
    if(requirement.chance < 1F && requirement.chance >= 0F) {
      String keyNever = requirement.getActionType().isInput() ? "tooltip.machinery.chance.in.never" : "tooltip.machinery.chance.out.never";
      String keyChance = requirement.getActionType().isInput() ? "tooltip.machinery.chance.in" : "tooltip.machinery.chance.out";

      String chanceStr = String.valueOf(Mth.floor(requirement.chance * 100F));
      if(requirement.chance == 0F) {
        tooltip.add(Component.translatable(keyNever));
      } else {
        if(requirement.chance < 0.01F) {
          chanceStr = "< 1";
        }
        chanceStr += "%";
        tooltip.add(Component.translatable(keyChance, chanceStr));
      }
    }
    return tooltip;
  }
}
