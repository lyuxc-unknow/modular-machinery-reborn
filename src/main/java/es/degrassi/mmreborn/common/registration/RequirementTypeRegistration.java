package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementBiome;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementChunkload;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDuration;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementLootTable;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementTime;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import java.util.function.Supplier;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RequirementTypeRegistration {
  public static final DeferredRegister<RequirementType<? extends ComponentRequirement<?, ?>>> MACHINE_REQUIREMENTS = DeferredRegister.create(RequirementType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Registry<RequirementType<? extends ComponentRequirement<?, ?>>> REQUIREMENTS_REGISTRY = MACHINE_REQUIREMENTS.makeRegistry(builder -> {});

  public static final Supplier<RequirementType<RequirementItem>> ITEM = MACHINE_REQUIREMENTS.register("item", () -> RequirementType.create(RequirementItem.CODEC));
  public static final Supplier<RequirementType<RequirementFluid>> FLUID = MACHINE_REQUIREMENTS.register("fluid", () -> RequirementType.create(RequirementFluid.CODEC));
  public static final Supplier<RequirementType<RequirementEnergy>> ENERGY = MACHINE_REQUIREMENTS.register("energy", () -> RequirementType.create(RequirementEnergy.CODEC));
  public static final Supplier<RequirementType<RequirementDuration>> DURATION = MACHINE_REQUIREMENTS.register("duration", () -> RequirementType.create(RequirementDuration.CODEC));
  public static final Supplier<RequirementType<RequirementDimension>> DIMENSION = MACHINE_REQUIREMENTS.register("dimension", () -> RequirementType.create(RequirementDimension.CODEC));
  public static final Supplier<RequirementType<RequirementBiome>> BIOME = MACHINE_REQUIREMENTS.register("biome", () -> RequirementType.create(RequirementBiome.CODEC));
  public static final Supplier<RequirementType<RequirementWeather>> WEATHER = MACHINE_REQUIREMENTS.register("weather", () -> RequirementType.create(RequirementWeather.CODEC));
  public static final Supplier<RequirementType<RequirementTime>> TIME = MACHINE_REQUIREMENTS.register("time", () -> RequirementType.create(RequirementTime.CODEC));
  public static final Supplier<RequirementType<RequirementChunkload>> CHUNKLOAD = MACHINE_REQUIREMENTS.register("chunkload", () -> RequirementType.create(RequirementChunkload.CODEC));
  public static final Supplier<RequirementType<RequirementLootTable>> LOOT_TABLE = MACHINE_REQUIREMENTS.register("loot_table", () -> RequirementType.create(RequirementLootTable.CODEC));

  public static void register(IEventBus bus) {
    MACHINE_REQUIREMENTS.register(bus);
  }
}
