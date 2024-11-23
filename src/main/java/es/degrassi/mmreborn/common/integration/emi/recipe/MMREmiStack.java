package es.degrassi.mmreborn.common.integration.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiComponent;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MMREmiStack extends EmiStack implements EmiIngredient {
  public static MMREmiStack of(RequirementType<?> requirement) {
    return new MMREmiStack(requirement);
  }

  protected final RequirementType<? extends ComponentRequirement<?, ?>> requirementType;
  @Getter
  protected final ResourceLocation id;
  protected EmiComponent<?, ?> component;

  private MMREmiStack(RequirementType<? extends ComponentRequirement<?, ?>> requirement) {
    this.requirementType = requirement;
    this.id = ModularMachineryReborn.getRequirementRegistrar().getKey(requirement);
  }

  public static MMREmiStack of(EmiComponent<?, ?> emiComponent) {
    MMREmiStack stack = new MMREmiStack(emiComponent.getRequirement().getRequirementType());
    stack.component = emiComponent;
    return stack;
  }

  @Override
  public EmiStack copy() {
    MMREmiStack stack = new MMREmiStack(requirementType);
    if (component != null) {
      stack.component = component;
    }
    return stack;
  }

  @Override
  public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
    if (component != null) {
      component.render(draw, x, y, delta);
    }
  }

  @Override
  public boolean isEmpty() {
    return component == null;
  }

  @Override
  public DataComponentPatch getComponentChanges() {
    return null;
  }

  @Override
  public Object getKey() {
    return id;
  }

  @Override
  public List<Component> getTooltipText() {
    if (component == null) return List.of();
    return component.getTooltip();
  }

  @Override
  public Component getName() {
    return Component.translatable("emi.stack." + id.getNamespace() + "." + id.getPath());
  }
}
