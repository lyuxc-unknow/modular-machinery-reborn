package es.degrassi.mmreborn.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.network.DataType;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.manager.crafting.ProcessorType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class RegistrarCodec<V> implements NamedCodec<V> {

  /**
   * Vanilla registries
   **/
  public static final NamedCodec<Item> ITEM = of(BuiltInRegistries.ITEM, false);
  public static final NamedCodec<Block> BLOCK = of(BuiltInRegistries.BLOCK, false);
  public static final NamedCodec<Fluid> FLUID = of(BuiltInRegistries.FLUID, false);
  public static final NamedCodec<EntityType<?>> ENTITY = of(BuiltInRegistries.ENTITY_TYPE, false);
  public static final NamedCodec<MobEffect> EFFECT = of(BuiltInRegistries.MOB_EFFECT, false);

  /**
   * MMR registries
   **/
  public static final NamedCodec<RequirementType<?>> REQUIREMENT_NEW =
      of(ModularMachineryReborn.getRequirementRegistrar(), true);
  public static final NamedCodec<ComponentType> COMPONENT = of(ModularMachineryReborn.getComponentRegistrar(), true);
//    public static final NamedCodec<GuiElementType<?>> GUI_ELEMENT = of(ICustomMachineryAPI.INSTANCE.guiElementRegistrar(), true);
//    public static final NamedCodec<MachineAppearanceProperty<?>> APPEARANCE_PROPERTY = of(ICustomMachineryAPI.INSTANCE.appearancePropertyRegistrar(), true);
  public static final NamedCodec<DataType<?, ?>> DATA = of(ModularMachineryReborn.dataRegistrar(), true);
  public static final NamedCodec<ProcessorType<?>> CRAFTING_PROCESSOR = of(ModularMachineryReborn.processorRegistrar(), true);

  public static final NamedCodec<ResourceLocation> MMR_LOC_CODEC = NamedCodec.STRING.comapFlatMap(
    s -> {
      try {
        if (s.contains(":"))
          return DataResult.success(ResourceLocation.tryParse(s));
        else
          return DataResult.success(ModularMachineryReborn.rl(s));
      } catch (Exception e) {
        return DataResult.error(e::getMessage);
      }
    },
    ResourceLocation::toString,
    "MMR Resource location"
  );

  public static <V> RegistrarCodec<V> of(Registry<V> registrar, boolean isMMR) {
    return new RegistrarCodec<>(registrar, isMMR);
  }

  private final Registry<V> registrar;
  private final boolean isMMR;

  private RegistrarCodec(Registry<V> registrar, boolean isMMR) {
    this.registrar = registrar;
    this.isMMR = isMMR;
  }

  @Override
  public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
    return (this.isMMR ? MMR_LOC_CODEC : DefaultCodecs.RESOURCE_LOCATION).decode(ops, input).flatMap(keyValuePair ->
      !this.registrar.containsKey(keyValuePair.getFirst())
        ? DataResult.error(() -> "Unknown registry key in " + this.registrar.key() + ": " + keyValuePair.getFirst())
        : DataResult.success(keyValuePair.mapFirst(this.registrar::get))
    );
  }

  @Override
  public <T> DataResult<T> encode(DynamicOps<T> ops, V input, T prefix) {
    return DefaultCodecs.RESOURCE_LOCATION.encode(ops, this.registrar.getKey(input), prefix);
  }

  @Override
  public String name() {
    return this.registrar.key().location().toString();
  }
}
