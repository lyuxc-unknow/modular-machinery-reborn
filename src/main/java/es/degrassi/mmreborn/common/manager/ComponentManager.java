package es.degrassi.mmreborn.common.manager;

import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@ParametersAreNonnullByDefault
public class ComponentManager implements INBTSerializable<CompoundTag> {
  @Getter
  private final MachineControllerEntity controller;

  private final Map<BlockPos, MachineComponent<?>> foundComponents = new LinkedHashMap<>();
  private final Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> foundComponentsValues = new LinkedHashMap<>();
  private final Map<BlockPos, ModifierReplacement> foundModifiers = new LinkedHashMap<>();

  public ComponentManager(MachineControllerEntity entity) {
    this.controller = entity;
  }

  public final void reset() {
    foundComponents.clear();
    foundModifiers.clear();
    foundComponentsValues.clear();
  }

  public final void updateComponents() {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY || controller.hasActiveRecipe()) return;
    if (controller.getLevel().getGameTime() % 20 == 0) {
      reset();
      foundComponents.putAll(gatherComponents());
      foundComponentsValues.putAll(filter());
      foundModifiers.putAll(gatherModifiers());
      controller.getProcessor().setMachineInventoryChanged();
    }
    controller.setChanged();
  }

  private Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> filter() {
    Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> foundComponentsValues = new LinkedHashMap<>();
    for (MachineComponent<?> comp : foundComponents.values()) {
      foundComponentsValues
          .computeIfAbsent(comp.getComponentType(), t -> new LinkedHashMap<>())
          .computeIfAbsent(comp.getIOType(), io -> new LinkedList<>())
          .add(comp);
    }
    return foundComponentsValues;
  }

  public final List<MachineComponent<?>> getFoundComponentsList() {
    if (foundComponents.isEmpty()) updateComponents();
    return foundComponents.values()
        .stream()
        .toList();
  }

  public List<ModifierReplacement> getFoundModifiersList() {
    if (foundModifiers.isEmpty()) updateComponents();
    return foundModifiers.values().stream().toList();
  }

  public final Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    return foundComponents;
  }

  public final Map<BlockPos, ModifierReplacement> getFoundModifiersMap() {
    return foundModifiers;
  }

  private Map<BlockPos, MachineComponent<?>> gatherComponents() {
    Map<BlockPos, MachineComponent<?>> map = new LinkedHashMap<>();
    Map<BlockPos, BlockIngredient> filteredMap = controller.getFoundMachine().getPattern().getBlocksFiltered(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    BlockPos controllerPos = controller.getBlockPos();
    Level level = controller.getLevel();
    if (level == null) return map;
    for (BlockPos potentialPosition : filteredMap.keySet()) {
      BlockPos realPos = controllerPos.offset(potentialPosition);
      BlockEntity te = level.getBlockEntity(realPos);
      if (te instanceof MachineComponentEntity<?> entity) {
        var component = entity.provideComponent();
        if (entity instanceof ControllerAccessible accessible && accessible.getControllerPos() != null)
          accessible.setControllerPos(controllerPos.immutable());
        if (component != null) {
          map.put(realPos, component);
        }
      }
    }
    return map;
  }

  private Map<BlockPos, ModifierReplacement> gatherModifiers() {
    Map<BlockPos, ModifierReplacement> map = new LinkedHashMap<>();
    if (controller.getLevel() == null) return map;
    controller.getFoundMachine()
        .getPattern()
        .getPattern()
        .getModifiers(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
        .forEach((potentialPosition, modifier) -> {
          BlockPos realPos = controller.getBlockPos().offset(potentialPosition);
          BlockInWorld biw = new BlockInWorld(controller.getLevel(), realPos, false);
          if (modifier.getIngredient().getAll().stream().anyMatch(state -> state.test(biw)))
            map.put(realPos, modifier);
        });
    return map;
  }

  public List<RecipeModifier> getModifiers(RequirementType<?> type) {
    if (foundModifiers.isEmpty()) updateComponents();
    return foundModifiers.values()
        .stream()
        .map(ModifierReplacement::getModifiers)
        .flatMap(List::stream)
        .filter(mod -> mod.getTarget().equals(type))
        .toList();
  }

  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> Optional<C> getComponent(IRequirement<C> requirement, ICraftingContext context) {
    if (foundComponentsValues.isEmpty()) updateComponents();
    AtomicReference<C> merged = new AtomicReference<>(null);
    Optional.ofNullable(foundComponentsValues.get(requirement.getComponentType()))
        .map(m -> m.get(requirement.getMode()))
        .stream()
        .flatMap(List::stream)
        .map(m -> (C) m)
        .filter(m -> requirement.test(m, context) || requirement.isComponentValid(m, context))
        .forEachOrdered(c -> {
          if (merged.get() == null)
            merged.set(c);
          else if (c.getIOType().isInput()) {
            if (merged.get().canMerge(c))
              merged.set(merged.get().merge(c));
          }
        });
    return Optional.ofNullable(merged.get());
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag nbt = new CompoundTag();
    CompoundTag componentsByType = new CompoundTag();
    foundComponentsValues.forEach((type, map) -> {
      CompoundTag listByMode = new CompoundTag();
      map.forEach((mode, list) -> listByMode.putInt(mode.getSerializedName(), list.size()));
      componentsByType.put(type.getId().toString(), listByMode);
    });
    nbt.put("components", componentsByType);
    ListTag modifiers = foundModifiers.values()
        .stream()
        .map(ModifierReplacement::asTag)
        .collect(ListTag::new, ListTag::add, ListTag::add);
    nbt.put("modifiers", modifiers);
    return nbt;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

  }
}
