package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import static es.degrassi.mmreborn.ModularMachineryReborn.rootLC;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.item.StructureCreatorItemMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class Registration {
  public static final LootContextParamSet MODULAR_MACHINERY_LOOT_PARAMETER_SET = LootContextParamSets.register(
      "modular_machinery_reborn", builder ->
      builder.optional(LootContextParams.ORIGIN).optional(LootContextParams.BLOCK_ENTITY)
  );

  public static void register(final IEventBus bus) {
    DataComponentRegistration.register(bus);
    ComponentRegistration.register(bus);
    DataRegistration.register(bus);
    ProcessorTypeRegistration.register(bus);
    RequirementTypeRegistration.register(bus);
    BlockRegistration.register(bus);
    ItemRegistration.register(bus);
    EntityRegistration.register(bus);
    ContainerRegistration.register(bus);
    RecipeRegistration.register(bus);
    CreativeTabsRegistration.register(bus);
  }
}
