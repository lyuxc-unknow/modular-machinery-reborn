package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.client.requirement.ItemRendering;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementBiome;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class EmiBiomeComponent extends EmiComponent<ResourceLocation, RequirementBiome> implements ItemRendering {
  public EmiBiomeComponent(RequirementBiome requirement) {
    super(requirement, 0, 0, false);
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  public EmiStack getStack() {
    return EmiStack.of(ItemRegistration.BIOME_READER.toStack());
  }

  @Override
  public List<ResourceLocation> ingredients() {
    return Lists.newArrayList(requirement.filter().iterator());
  }

  @Override
  @Nullable
  public ResourceLocation texture() {
    return null;
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    drawStack(guiGraphics, 0, 0, -1);
  }

  @Override
  public List<Component> getTooltip() {
    StringBuilder biomes = new StringBuilder();
    ingredients().forEach(biome -> biomes.append(biome.toString()).append(","));
    int index = biomes.lastIndexOf(",");
    if (index >= biomes.length() - 1)
      biomes.deleteCharAt(index);
    return List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.biome." + requirement.blacklist(),
        biomes.toString()
    ));
  }
}
