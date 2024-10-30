package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.ModularMachineryReborn;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeTabsRegistration {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularMachineryReborn.MODID);

  public static final Supplier<CreativeModeTab> MODULAR_MACHINERY_REBORN_TAB = CREATIVE_TABS.register("modular_machinery_reborn_mekanism", () -> CreativeModeTab.builder()
    .title(Component.translatable("itemGroup.modular_machinery_reborn_mekanism.group"))
    .icon(MekanismBlocks.CREATIVE_CHEMICAL_TANK.asItem()::getDefaultInstance)
    .displayItems((params, output) -> ItemRegistration.ITEMS.getEntries().forEach(entry -> output.accept(entry.get()))).build()
  );

  public static void register(final IEventBus bus) {
    CREATIVE_TABS.register(bus);
  }
}
