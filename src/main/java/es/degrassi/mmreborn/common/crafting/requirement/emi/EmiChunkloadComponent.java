package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementChunkload;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class EmiChunkloadComponent extends EmiComponent<Integer, RequirementChunkload> implements ItemRendering {
  public EmiChunkloadComponent(RequirementChunkload requirement) {
    super(requirement, 0, 0, false);
  }

  @Override
  @Nullable
  public ResourceLocation texture() {
    return null;
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<Integer> ingredients() {
    return List.of(requirement.radius());
  }

  public EmiStack getStack() {
    return EmiStack.of(ItemRegistration.CHUNKLOADER.toStack());
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    drawStack(guiGraphics, 0, 0, -1);
  }

  @Override
  public List<Component> getTooltip() {
    return List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.chunkload",
        requirement.radius()
    ));
  }
}
