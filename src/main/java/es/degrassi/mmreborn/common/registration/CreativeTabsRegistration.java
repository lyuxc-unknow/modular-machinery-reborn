package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.item.ItemBlueprint;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabsRegistration {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularMachineryReborn.MODID);

  public static final Supplier<CreativeModeTab> MODULAR_MACHINERY_REBORN_TAB = CREATIVE_TABS.register("modular_machinery_reborn", () -> CreativeModeTab.builder()
    .title(Component.translatable("itemGroup.modular_machinery_reborn.group"))
    .icon(ItemRegistration.MODULARIUM.get()::getDefaultInstance)
    .displayItems((params, output) -> {
      ItemRegistration.ITEMS.getEntries().forEach(entry -> {
        if (entry.get() instanceof ItemBlueprint blueprint) {
          ModularMachineryReborn.MACHINES.keySet().forEach(id -> {
            ItemStack stack = blueprint.getDefaultInstance();
            stack.set(Registration.MACHINE_DATA, id);
            output.accept(stack);
          });
        } else
          output.accept(entry.get());
      });
    }).build()
  );

  public static void register(final IEventBus bus) {
    CREATIVE_TABS.register(bus);
  }
}
