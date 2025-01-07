package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.common.util.LootTableHelper;
import lombok.Getter;
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

public class RequirementLootTable implements IRequirement<ItemComponent> {
  public static final NamedCodec<RequirementLootTable> CODEC = NamedCodec.record(lootTableRequirementInstance ->
      lootTableRequirementInstance.group(
          DefaultCodecs.RESOURCE_LOCATION.fieldOf("table").forGetter(RequirementLootTable::getLootTable),
          NamedCodec.FLOAT.optionalFieldOf("luck", 0.0F).forGetter(RequirementLootTable::getLuck),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(lootTableRequirementInstance, RequirementLootTable::new), "Loottable requirement"
  );

  @Getter
  private final ResourceLocation lootTable;
  @Getter
  private final float luck;
  @Getter
  private final PositionedRequirement position;
  private List<ItemStack> toOutput = Collections.emptyList();


  public RequirementLootTable(ResourceLocation lootTable, float luck, PositionedRequirement position) {
    this.lootTable = lootTable;
    this.luck = luck;
    this.position = position;
    LootTableHelper.addTable(lootTable);
  }

  @Override
  public RequirementType<RequirementLootTable> getType() {
    return RequirementTypeRegistration.LOOT_TABLE.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public boolean test(ItemComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<ItemComponent> list) {
    list.processOnEnd(this::processOutput);
  }

  private CraftingResult processOutput(ItemComponent component, ICraftingContext context) {
    if(context.getMachineTile().getLevel() == null || context.getMachineTile().getLevel().getServer() == null)
      return CraftingResult.pass();
    IOInventory inv = component.getContainerProvider();
    if(toOutput.isEmpty()) {
      LootTable table = context.getMachineTile().getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, this.lootTable));
      LootParams params = new LootParams.Builder((ServerLevel) context.getMachineTile().getLevel())
          .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(context.getMachineTile().getBlockPos()))
          .withParameter(LootContextParams.BLOCK_ENTITY, context.getMachineTile())
          .withLuck(RecipeModifier.applyModifiers(context.getModifiers(getType()), getType(),
              IOType.OUTPUT, luck, false))
          .create(Registration.MODULAR_MACHINERY_LOOT_PARAMETER_SET);
      toOutput = table.getRandomItems(params);
    }

    Iterator<ItemStack> iterator = toOutput.iterator();
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();

      int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), inv, true);

      if (inserted < stack.getCount()) {
        return CraftingResult.error(Component.translatable("craftcheck.failure.item.output.space"));
      }

      ItemUtils.tryPlaceItemInInventory(stack.copy(), inv, false);
      iterator.remove();
    }
    return CraftingResult.success();
  }

  @Override
  public RequirementLootTable deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementLootTable(
        lootTable,
        RecipeModifier.applyModifiers(modifiers, new RecipeRequirement<>(this), luck, false),
        new PositionedRequirement(getPosition().x(), getPosition().y())
    );
  }

  @Override
  public IOType getMode() {
    return IOType.OUTPUT;
  }

  @Override
  public RequirementLootTable deepCopy() {
    return new RequirementLootTable(lootTable, luck, new PositionedRequirement(getPosition().x(), getPosition().y()));
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.item.output");
  }

  @Override
  public boolean isComponentValid(ItemComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
