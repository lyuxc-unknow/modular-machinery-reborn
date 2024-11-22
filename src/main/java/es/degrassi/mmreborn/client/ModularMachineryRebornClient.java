package es.degrassi.mmreborn.client;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.integration.jei.RegisterJeiComponentEvent;
import es.degrassi.mmreborn.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.EnergyHatchScreen;
import es.degrassi.mmreborn.client.screen.FluidHatchScreen;
import es.degrassi.mmreborn.client.screen.ItemBusScreen;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiBiomeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiChunkloadComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiDimensionComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiEnergyComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiFluidComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiItemComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiTimeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiWeatherComponent;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.integration.jei.JeiComponentRegistry;
import es.degrassi.mmreborn.common.item.ItemDynamicColor;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class ModularMachineryRebornClient {
  public static ClientScheduler clientScheduler = new ClientScheduler();

  private static final List<Block> blockModelsToRegister = Lists.newLinkedList();
  private static final List<Item> itemModelsToRegister = Lists.newLinkedList();

  public static ModularMachineryRebornClient instance;

  public ModularMachineryRebornClient() {
    NeoForge.EVENT_BUS.register(clientScheduler);
    instance = this;
  }

  @SubscribeEvent
  public void registerBlockEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(EntityRegistration.CONTROLLER.get(), ControllerRenderer::new);
  }

  @SubscribeEvent
  public void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
    event.register(
        ModularMachineryRebornClient::blockColor,
        BlockRegistration.CONTROLLER.get(),

        BlockRegistration.CASING_PLAIN.get(),
        BlockRegistration.CASING_VENT.get(),
        BlockRegistration.CASING_FIREBOX.get(),
        BlockRegistration.CASING_GEARBOX.get(),
        BlockRegistration.CASING_REINFORCED.get(),
        BlockRegistration.CASING_CIRCUITRY.get(),

        BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(),

        BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(),

        BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
        BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
        BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
        BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
        BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
        BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
        BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get(),

        BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
        BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(),

        BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
        BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
        BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
        BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
        BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
        BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
        BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get(),

        BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(),

        BlockRegistration.DIMENSIONAL_DETECTOR.get(),
        BlockRegistration.BIOME_READER.get(),
        BlockRegistration.WEATHER_SENSOR.get(),
        BlockRegistration.TIME_COUNTER.get(),
        BlockRegistration.CHUNKLOADER.get()
    );
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(block -> event.register(ModularMachineryRebornClient::blockColor, block));
  }

  @SubscribeEvent
  public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    event.register(
        ModularMachineryRebornClient::itemColor,
        ItemRegistration.MODULARIUM.get(),

        ItemRegistration.CONTROLLER.get(),

        ItemRegistration.CASING_PLAIN.get(),
        ItemRegistration.CASING_VENT.get(),
        ItemRegistration.CASING_FIREBOX.get(),
        ItemRegistration.CASING_GEARBOX.get(),
        ItemRegistration.CASING_REINFORCED.get(),
        ItemRegistration.CASING_CIRCUITRY.get(),

        ItemRegistration.ENERGY_INPUT_HATCH_TINY.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_BIG.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(),

        ItemRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(),

        ItemRegistration.ITEM_INPUT_BUS_TINY.get(),
        ItemRegistration.ITEM_INPUT_BUS_SMALL.get(),
        ItemRegistration.ITEM_INPUT_BUS_NORMAL.get(),
        ItemRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
        ItemRegistration.ITEM_INPUT_BUS_BIG.get(),
        ItemRegistration.ITEM_INPUT_BUS_HUGE.get(),
        ItemRegistration.ITEM_INPUT_BUS_LUDICROUS.get(),

        ItemRegistration.ITEM_OUTPUT_BUS_TINY.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_BIG.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
        ItemRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(),

        ItemRegistration.FLUID_INPUT_HATCH_TINY.get(),
        ItemRegistration.FLUID_INPUT_HATCH_SMALL.get(),
        ItemRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
        ItemRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
        ItemRegistration.FLUID_INPUT_HATCH_BIG.get(),
        ItemRegistration.FLUID_INPUT_HATCH_HUGE.get(),
        ItemRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.FLUID_INPUT_HATCH_VACUUM.get(),

        ItemRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(),

        ItemRegistration.DIMENSIONAL_DETECTOR.get(),
        ItemRegistration.BIOME_READER.get(),
        ItemRegistration.WEATHER_SENSOR.get(),
        ItemRegistration.TIME_COUNTER.get(),
        ItemRegistration.CHUNKLOADER.get()
    );
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(block -> event.register(ModularMachineryRebornClient::itemColor, block));
  }

  public static int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
    if (level == null || pos == null)
      return 0;
    switch (tintIndex) {
      case 1 -> level.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER);
      case 2 -> level.getBlockTint(pos, BiomeColors.GRASS_COLOR_RESOLVER);
      case 3 -> level.getBlockTint(pos, BiomeColors.FOLIAGE_COLOR_RESOLVER);
      case 4 -> {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof ColorableMachineComponentEntity machineTile) {
          return machineTile.getMachineColor();
        }
      }
    }
    return Config.machineColor;
  }

  public static int itemColor(ItemStack stack, int tintIndex) {
    if (stack.getItem() instanceof ItemDynamicColor colorableItem) {
      return colorableItem.getColorFromItemstack(stack, tintIndex);
    }
    return Config.machineColor;
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public void onModelRegister(ModelEvent.RegisterAdditional event) {
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("block/nope")));
    for (Block block : blockModelsToRegister) {
      Item i = block.asItem();
    }
    for (Item item : itemModelsToRegister) {
      String name = item.getClass().getSimpleName().toLowerCase();
      if (item instanceof BlockItem) {
        name = ((BlockItem) item).getBlock().getClass().getSimpleName().toLowerCase();
      }
      event.register(new ModelResourceLocation(ModularMachineryReborn.rl(name), "inventory"));
    }
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public void clientSetup(final FMLClientSetupEvent event) {
//    if (ModList.get().isLoaded("cloth_config")) {
//      ModLoadingContext.get().registerExtensionPoint(
//          IConfigScreenFactory.class,
//          () -> (minecraft, parent) ->
//              AutoConfig.getConfigScreen(MMRConfig.class, parent).get()
//      );
//    }

    if (ModList.get().isLoaded("jei")) {
      JeiComponentRegistry.init();
    }
  }

  @SubscribeEvent
  public void registerJeiComponents(final RegisterJeiComponentEvent event) {
    event.register(RequirementTypeRegistration.ENERGY.get(), JeiEnergyComponent::new);
    event.register(RequirementTypeRegistration.FLUID.get(), JeiFluidComponent::new);
    event.register(RequirementTypeRegistration.ITEM.get(), JeiItemComponent::new);
    event.register(RequirementTypeRegistration.TIME.get(), JeiTimeComponent::new);
    event.register(RequirementTypeRegistration.BIOME.get(), JeiBiomeComponent::new);
    event.register(RequirementTypeRegistration.CHUNKLOAD.get(), JeiChunkloadComponent::new);
    event.register(RequirementTypeRegistration.DIMENSION.get(), JeiDimensionComponent::new);
    event.register(RequirementTypeRegistration.WEATHER.get(), JeiWeatherComponent::new);
  }

  public void registerBlockModel(Block block) {
    blockModelsToRegister.add(block);
  }

  public void registerItemModel(Item item) {
    itemModelsToRegister.add(item);
  }

  @SubscribeEvent
  public void registerMenuScreens(final RegisterMenuScreensEvent event) {
    event.register(ContainerRegistration.CONTROLLER.get(), ControllerScreen::new);
    event.register(ContainerRegistration.ENERGY_HATCH.get(), EnergyHatchScreen::new);
    event.register(ContainerRegistration.FLUID_HATCH.get(), FluidHatchScreen::new);
    event.register(ContainerRegistration.ITEM_BUS.get(), ItemBusScreen::new);
  }

  @NotNull
  public static MachineControllerEntity getClientSideMachineControllerEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof MachineControllerEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Controller container without clicking on a Custom Machine block");
  }

  public static EnergyHatchEntity getClientSideEnergyHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof EnergyHatchEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Energy Hatch container without clicking on a Custom Machine block");
  }

  public static FluidTankEntity getClientSideFluidHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof FluidTankEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Fluid Hatch container without clicking on a Custom Machine block");
  }

  public static TileItemBus getClientSideItemBusEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof TileItemBus controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Item Bus container without clicking on a Custom Machine block");
  }
}
