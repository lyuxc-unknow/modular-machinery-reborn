package es.degrassi.mmreborn.common.crafting.modifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ModifierReplacement {
  public static final NamedCodec<ModifierReplacement> CODEC = NamedCodec.record(instance -> instance.group(
      BlockIngredient.CODEC.fieldOf("replacement").forGetter(ModifierReplacement::getIngredient),
      RecipeModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierReplacement::getModifiers),
      DefaultCodecs.BLOCK_POS.fieldOf("position").forGetter(ModifierReplacement::getPosition)
  ).apply(instance, ModifierReplacement::new), "Modifier Replacement");

  private final BlockIngredient info;
  private final List<RecipeModifier> modifier;
  private final List<Component> description;
  @Getter
  private final BlockPos position;

  public ModifierReplacement(BlockIngredient info, List<RecipeModifier> modifier,
                             BlockPos pos) {
    this.info = info;
    this.modifier = modifier;
    this.position = pos;
    this.description = new LinkedList<>();
    description.add(info.getNamesUnified());
    description.addAll(getModifiers().stream().map(RecipeModifier::getDescription).toList());
  }

  public BlockIngredient getIngredient() {
    return info;
  }

  public List<RecipeModifier> getModifiers() {
    return Collections.unmodifiableList(modifier);
  }

  public List<Component> getDescriptionLines() {
    return description;
  }

  public List<String> getDescriptionLinesString() {
    return description.stream().map(Component::getString).toList();
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.add("replacement", info.asJson());
    JsonArray modifiers = new JsonArray();
    modifier.stream().map(RecipeModifier::asJson).forEachOrdered(modifiers::add);
    json.add("modifiers", modifiers);
    JsonArray desc = new JsonArray();
    description.stream().map(Component::getString).map(JsonOps.INSTANCE::createString).forEachOrdered(desc::add);
    json.add("description", desc);
    json.add("position", DefaultCodecs.BLOCK_POS.encodeStart(JsonOps.INSTANCE, position).getOrThrow());
    return json;
  }
}
