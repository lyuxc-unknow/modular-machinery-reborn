package es.degrassi.mmreborn;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.command.MMRCommand;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineJsonReloadListener;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.HashMap;
import java.util.Map;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Mod(ModularMachineryReborn.MODID)
public class ModularMachineryReborn {
  public static final String MODID = "modular_machinery_reborn";
  public static final Logger LOGGER = LogManager.getLogger("Modular Machinery Reborn");

  public static final Map<ResourceLocation, DynamicMachine> MACHINES = new HashMap<>();

  public ModularMachineryReborn(final IEventBus MOD_BUS) {
    ConfigHolder<MMRConfig> config = AutoConfig.register(MMRConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));

    config.registerSaveListener((holder, mmrConfig) -> {
      MMRLogger.setDebugLevel(mmrConfig.general.debugLevel.getLevel());
      Config.load();
      EnergyHatchSize.loadFromConfig();
      FluidHatchSize.loadFromConfig();
      ItemBusSize.loadFromConfig();
      EnergyDisplayUtil.loadFromConfig();
      return InteractionResult.SUCCESS;
    });

    MMRLogger.init();

    Registration.register(MOD_BUS);

    MOD_BUS.register(new ModularMachineryRebornClient());
    MOD_BUS.addListener(this::registerCapabilities);

    final IEventBus GAME_BUS = NeoForge.EVENT_BUS;
    GAME_BUS.addListener(this::registerReloadListener);
    GAME_BUS.addListener(this::registerCommands);
    GAME_BUS.addListener(this::onReloadStart);

    EnergyHatchSize.loadFromConfig();
    FluidHatchSize.loadFromConfig();
    ItemBusSize.loadFromConfig();
    EnergyDisplayUtil.loadFromConfig();
  }

  private void registerCapabilities(final RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      EntityRegistration.CONTROLLER.get(),
      (be, side) -> be.getInventory()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      EntityRegistration.ITEM_INPUT_BUS.get(),
      (be, side) -> be.getInventory()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      EntityRegistration.ITEM_OUTPUT_BUS.get(),
      (be, side) -> be.getInventory()
    );
    event.registerBlockEntity(
      Capabilities.FluidHandler.BLOCK,
      EntityRegistration.FLUID_INPUT_HATCH.get(),
      (be, side) -> be.getTank()
    );
    event.registerBlockEntity(
      Capabilities.FluidHandler.BLOCK,
      EntityRegistration.FLUID_OUTPUT_HATCH.get(),
      (be, side) -> be.getTank()
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      EntityRegistration.ENERGY_INPUT_HATCH.get(),
      (be, side) -> be
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      EntityRegistration.ENERGY_OUTPUT_HATCH.get(),
      (be, side) -> be
    );
  }

  private void registerReloadListener(final AddReloadListenerEvent event) {
    event.addListener(new MachineJsonReloadListener());
  }

  @Contract("_ -> new")
  public static @NotNull ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  private void registerCommands(final RegisterCommandsEvent event) {
    event.getDispatcher().register(MMRCommand.register("modularmachineryreborn"));
    event.getDispatcher().register(MMRCommand.register("modularmachinery"));
    event.getDispatcher().register(MMRCommand.register("modular_machinery_reborn"));
    event.getDispatcher().register(MMRCommand.register("modular_machinery"));
    event.getDispatcher().register(MMRCommand.register("mmr"));
    event.getDispatcher().register(MMRCommand.register("mm"));
  }

  private void onReloadStart(final CommandEvent event) {
    if (event.getParseResults().getReader().getString().equals("reload") && event.getParseResults().getContext().getSource().hasPermission(2)) {
      MMRLogger.reset();
      Config.load();
      EnergyHatchSize.loadFromConfig();
      FluidHatchSize.loadFromConfig();
      ItemBusSize.loadFromConfig();
      EnergyDisplayUtil.loadFromConfig();
      if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
        MMRCommand.reloadMachines(player.server, player);
//        MMRCommand.reloadRecipes(player.server, player);
      }
    }
  }

  public static Registry<RequirementType<?>> getRequirementRegistrar() {
    return RequirementTypeRegistration.REQUIREMENTS_REGISTRY;
  }

  public static Registry<ComponentType> getComponentRegistrar() {
    return ComponentRegistration.COMPONENTS_REGISTRY;
  }
}
