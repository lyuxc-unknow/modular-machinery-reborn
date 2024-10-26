package es.degrassi.mmreborn.api;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import java.util.List;
import net.minecraft.world.level.block.Rotation;

public class BlockIngredient implements IIngredient<PartialBlockState> {

  public static final BlockIngredient AIR = new BlockIngredient(PartialBlockState.AIR);
  public static final BlockIngredient ANY = new BlockIngredient(PartialBlockState.ANY);
  public static final BlockIngredient MACHINE = new BlockIngredient(PartialBlockState.MACHINE);

  public static final NamedCodec<BlockIngredient> CODEC = PartialBlockState.CODEC_LIST.xmap(BlockIngredient::new, ingredient -> ingredient.partialBlockStates, "Block ingredient");

  private final List<PartialBlockState> partialBlockStates;

  public BlockIngredient(PartialBlockState partialBlockState) {
    partialBlockStates = List.of(partialBlockState);
  }
  public BlockIngredient(List<PartialBlockState> partialBlockState) {
    partialBlockStates = partialBlockState;
  }

  public BlockIngredient copy() {
    return new BlockIngredient(partialBlockStates.stream().map(PartialBlockState::copy).toList());
  }

  @Override
  public List<PartialBlockState> getAll() {
    return this.partialBlockStates;
  }

  @Override
  public boolean test(PartialBlockState partialBlockState) {
    return this.partialBlockStates.stream().anyMatch(state -> state.getBlockState() == partialBlockState.getBlockState());
  }

  @Override
  public String toString() {
    return this.partialBlockStates.toString();
  }

  public BlockIngredient copyWithRotation(Rotation rotation) {
    return new BlockIngredient(getAll().stream().map(state -> state.copyWithRotation(rotation)).toList());
  }

  @Override
  public JsonObject asJson() {
    return new JsonObject();
  }
}
