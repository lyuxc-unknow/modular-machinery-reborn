package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;

public record JeiPositionedRequirement(int x, int y) {
  public static final NamedCodec<JeiPositionedRequirement> POSITION_CODEC =
      NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("x").forGetter(JeiPositionedRequirement::x),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("y").forGetter(JeiPositionedRequirement::y)
  ).apply(instance, JeiPositionedRequirement::new), "Jei Position");

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("x", x);
    json.addProperty("y", y);
    return json;
  }
}
