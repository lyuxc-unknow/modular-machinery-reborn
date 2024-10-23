package es.degrassi.mmreborn.common.integration.jei.ingredient;

import es.degrassi.mmreborn.ModularMachineryReborn;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EnergyIngredientHelper implements IIngredientHelper<Long> {

    @Override
    public IIngredientType<Long> getIngredientType() {
        return CustomIngredientTypes.ENERGY;
    }

    @Override
    public String getDisplayName(Long energy) {
        return Component.translatable("modular_machinery_reborn.jei.ingredient.energy", energy).getString();
    }

    //Safe to remove
    @SuppressWarnings("removal")
    @Override
    public String getUniqueId(Long energy, UidContext context) {
        return "" + energy;
    }

    @Override
    public Object getUid(Long energy, UidContext context) {
        return "" + energy;
    }

    @Override
    public Long copyIngredient(Long energy) {
        return energy.longValue();
    }

    @Override
    public String getErrorInfo(@Nullable Long energy) {
        return "";
    }

    @Override
    public ResourceLocation getResourceLocation(Long ingredient) {
        return ModularMachineryReborn.rl("energy");
    }
}
