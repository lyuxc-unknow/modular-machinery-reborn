package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.prop.ChemicalHatchSize;
import es.degrassi.mmreborn.common.item.ChemicalHatchItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistration {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(ModularMachineryReborn.MODID);

  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_TINY = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.TINY.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_TINY.get(), ChemicalHatchSize.TINY));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_SMALL = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.SMALL.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_SMALL.get(), ChemicalHatchSize.SMALL));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_NORMAL = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.NORMAL.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_NORMAL.get(), ChemicalHatchSize.NORMAL));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_REINFORCED = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.REINFORCED.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_REINFORCED.get(), ChemicalHatchSize.REINFORCED));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_BIG = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.BIG.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_BIG.get(), ChemicalHatchSize.BIG));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_HUGE = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.HUGE.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_HUGE.get(), ChemicalHatchSize.HUGE));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_LUDICROUS = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.LUDICROUS.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_LUDICROUS.get(), ChemicalHatchSize.LUDICROUS));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_INPUT_HATCH_VACUUM = ITEMS.register("chemicalinputhatch_" + ChemicalHatchSize.VACUUM.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_INPUT_HATCH_VACUUM.get(), ChemicalHatchSize.VACUUM));

  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_TINY = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.TINY.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_TINY.get(), ChemicalHatchSize.TINY));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_SMALL = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.SMALL.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_SMALL.get(), ChemicalHatchSize.SMALL));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_NORMAL = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.NORMAL.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_NORMAL.get(), ChemicalHatchSize.NORMAL));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_REINFORCED = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.REINFORCED.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_REINFORCED.get(), ChemicalHatchSize.REINFORCED));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_BIG = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.BIG.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_BIG.get(), ChemicalHatchSize.BIG));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_HUGE = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.HUGE.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_HUGE.get(), ChemicalHatchSize.HUGE));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_LUDICROUS = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.LUDICROUS.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_LUDICROUS.get(), ChemicalHatchSize.LUDICROUS));
  public static final DeferredHolder<Item, ChemicalHatchItem> CHEMICAL_OUTPUT_HATCH_VACUUM = ITEMS.register("chemicaloutputhatch_" + ChemicalHatchSize.VACUUM.getSerializedName(),
      () -> new ChemicalHatchItem(BlockRegistration.CHEMICAL_OUTPUT_HATCH_VACUUM.get(), ChemicalHatchSize.VACUUM));
  
  public static void register(final IEventBus bus) {
    ITEMS.register(bus);
  }
}
