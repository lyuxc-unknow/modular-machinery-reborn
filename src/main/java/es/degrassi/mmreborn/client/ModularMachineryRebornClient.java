package es.degrassi.mmreborn.client;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.TagUtil;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiComponentEvent;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiRequirementToStackEvent;
import es.degrassi.mmreborn.api.integration.jei.RegisterJeiComponentEvent;
import es.degrassi.mmreborn.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmreborn.client.model.ControllerModelLoader;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.EnergyHatchScreen;
import es.degrassi.mmreborn.client.screen.FluidHatchScreen;
import es.degrassi.mmreborn.client.screen.ItemBusScreen;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementLootTable;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiBiomeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiEnergyComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiFluidComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiItemComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiLootTableComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiTimeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiWeatherComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiChunkloadComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiDimensionComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiBiomeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiChunkloadComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiDimensionComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiEnergyComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiFluidComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiItemComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiLootTableComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiTimeComponent;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiWeatherComponent;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.integration.emi.EmiComponentRegistry;
import es.degrassi.mmreborn.common.integration.emi.EmiStackRegistry;
import es.degrassi.mmreborn.common.integration.jei.JeiComponentRegistry;
import es.degrassi.mmreborn.common.item.ItemDynamicColor;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.LootTableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ModularMachineryRebornClient {
  public static ClientScheduler clientScheduler = new ClientScheduler();
  private static Map<ModelResourceLocation, BakedModel> models;
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
  public void registerModelLoader(final ModelEvent.RegisterGeometryLoaders event) {
    event.register(ModularMachineryReborn.rl("controller"), ControllerModelLoader.INSTANCE);
  }

  @SubscribeEvent
  public void onBackingCompleted(final ModelEvent.BakingCompleted event) {
    models = event.getModels();
  }

  public static Map<ModelResourceLocation, BakedModel> getAllModels() {
    return models;
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
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("default/controller")));
    for (String folder : MMRConfig.get().modelFolders.get()) {
      Minecraft.getInstance().getResourceManager().listResources("models/" + folder, s -> s.getPath().endsWith(".json")).forEach((rl, resource) -> {
        ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().substring(7).replace(".json", ""));
        event.register(ModelResourceLocation.standalone(modelRL));
      });
    }
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
    if (ModList.get().isLoaded("emi")) {
      EmiComponentRegistry.init();
      EmiStackRegistry.init();
    } else if (ModList.get().isLoaded("jei")) {
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
    event.register(RequirementTypeRegistration.LOOT_TABLE.get(), JeiLootTableComponent::new);
  }

  @SubscribeEvent
  public void registerEmiComponents(final RegisterEmiComponentEvent event) {
    event.register(RequirementTypeRegistration.ENERGY.get(), EmiEnergyComponent::new);
    event.register(RequirementTypeRegistration.ITEM.get(), EmiItemComponent::new);
    event.register(RequirementTypeRegistration.FLUID.get(), EmiFluidComponent::new);
    event.register(RequirementTypeRegistration.BIOME.get(), EmiBiomeComponent::new);
    event.register(RequirementTypeRegistration.TIME.get(), EmiTimeComponent::new);
    event.register(RequirementTypeRegistration.CHUNKLOAD.get(), EmiChunkloadComponent::new);
    event.register(RequirementTypeRegistration.DIMENSION.get(), EmiDimensionComponent::new);
    event.register(RequirementTypeRegistration.WEATHER.get(), EmiWeatherComponent::new);
    event.register(RequirementTypeRegistration.LOOT_TABLE.get(), EmiLootTableComponent::new);
  }

  @SubscribeEvent
  public void registerEmiStacks(final RegisterEmiRequirementToStackEvent event) {
    event.register(
        RequirementTypeRegistration.ITEM.get(),
        this::emiStackFromItemRequirement
    );
    event.register(
        RequirementTypeRegistration.FLUID.get(),
        requirement -> List.of(EmiStack.of(requirement.required.asFluidStack().getFluid(), requirement.amount))
    );
    event.register(
        RequirementTypeRegistration.LOOT_TABLE.get(),
        requirement -> LootTableHelper
            .getLootsForTable(requirement.getLootTable())
            .stream()
            .map(LootTableHelper.LootData::stack)
            .map(EmiStack::of)
            .toList()
    );
  }

  private List<EmiStack> emiStackFromItemRequirement(RequirementItem requirement) {
    List<EmiStack> stacks = new ArrayList<>();
    for (Ingredient.Value value : requirement.getIngredient().ingredient().values) {
      if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) {
        for (Item stack : TagUtil.getItems(tag).toList()) {
          stacks.add(EmiStack.of(stack, requirement.ingredient.count()));
        }
      } else if (value instanceof Ingredient.ItemValue(ItemStack item)) {
        stacks.add(EmiStack.of(item, requirement.ingredient.count()));
      }
    }
    return stacks;
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
