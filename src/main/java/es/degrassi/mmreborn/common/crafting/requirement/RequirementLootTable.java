package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.common.util.LootTableHelper;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public class RequirementLootTable extends ComponentRequirement<ResourceLocation, RequirementLootTable> {
  public static final NamedCodec<RequirementLootTable> CODEC = NamedCodec.record(lootTableRequirementInstance ->
      lootTableRequirementInstance.group(
          DefaultCodecs.RESOURCE_LOCATION.fieldOf("table").forGetter(RequirementLootTable::getLootTable),
          NamedCodec.FLOAT.optionalFieldOf("luck", 0.0F).forGetter(RequirementLootTable::getLuck),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(RequirementLootTable::getPosition)
      ).apply(lootTableRequirementInstance, RequirementLootTable::new), "Loottable requirement"
  );

  private final ResourceLocation lootTable;
  private final float luck;
  private List<ItemStack> toOutput = Collections.emptyList();

  public RequirementLootTable(ResourceLocation lootTable, float luck, PositionedRequirement position) {
    super(RequirementTypeRegistration.LOOT_TABLE.get(), IOType.OUTPUT, position);
    this.lootTable = lootTable;
    this.luck = luck;
    LootTableHelper.addTable(lootTable);
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_ITEM.get()) &&
        cmp instanceof MachineComponent.ItemBus &&
        cmp.getIOType() == getActionType();
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return true;
  }

  @Override
  public @NotNull CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IOInventory inv = (IOInventory) component.providedComponent();
    if(toOutput.isEmpty()) {
      LootTable table = context.getMachineController().getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, this.lootTable));
      LootParams params = new LootParams.Builder((ServerLevel) context.getMachineController().getLevel())
          .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(context.getMachineController().getBlockPos()))
          .withParameter(LootContextParams.BLOCK_ENTITY, context.getMachineController())
          .withLuck(RecipeModifier.applyModifiers(context.getModifiers(getRequirementType()), getRequirementType(),
              IOType.OUTPUT, luck, false))
          .create(Registration.MODULAR_MACHINERY_LOOT_PARAMETER_SET);
      toOutput = table.getRandomItems(params);
    }

    Iterator<ItemStack> iterator = toOutput.iterator();
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();

      int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), inv, true);

      if (inserted < stack.getCount()) {
        return CraftCheck.failure("craftcheck.failure.item.output.space");
      }

      ItemUtils.tryPlaceItemInInventory(stack.copy(), inv, false);
      iterator.remove();
    }
    return CraftCheck.success();
  }

  @Override
  public @NotNull CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor<?>> restrictions) {
    return CraftCheck.success();
  }

  @Override
  public RequirementLootTable deepCopy() {
    return new RequirementLootTable(lootTable, luck, new PositionedRequirement(getPosition().x(), getPosition().y()));
  }

  @Override
  public RequirementLootTable deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementLootTable(
        lootTable,
        RecipeModifier.applyModifiers(modifiers, this, luck, false),
        new PositionedRequirement(getPosition().x(), getPosition().y())
    );
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return "component.missing.item.output";
  }
}
