package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class JeiComponent<C, T extends ComponentRequirement<C, T>> implements IIngredientRenderer<C> {
  protected static final ResourceLocation LOCATION_JEI_ICONS = ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/jeirecipeicons.png");

  protected final T requirement;
  protected final int uOffset, vOffset;

  protected JeiComponent(T requirement, int uOffset, int vOffset) {
    this.requirement = requirement;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
  }

  public abstract int getWidth();
  public abstract int getHeight();

  public ResourceLocation texture() {
    return LOCATION_JEI_ICONS;
  }

  @Override
  public void render(GuiGraphics guiGraphics, @NotNull C ingredient) {
    guiGraphics.blit(texture(), -1, -1, 0, uOffset, vOffset, getWidth(), getHeight(), TextureSizeHelper.getWidth(texture()), TextureSizeHelper.getHeight(texture()));
  }

  @Override
  @SuppressWarnings("removal")
  public @NotNull List<Component> getTooltip(@NotNull C ingredient, @NotNull TooltipFlag tooltipFlag) {
    return new LinkedList<>();
  }

  public abstract List<C> ingredients();
}
