package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockInformationVariable {
  private Map<String, BlockArray.BlockInformation> variables = new HashMap<>();

  public java.util.Map<String, BlockArray.BlockInformation> getDefinedVariables() {
    return variables;
  }

  public static class Deserializer implements JsonDeserializer<BlockInformationVariable> {
    @Override
    public BlockInformationVariable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      BlockInformationVariable var = new BlockInformationVariable();
      JsonObject root = json.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
        JsonElement variableElement = entry.getValue();
        if(variableElement.isJsonArray()) {
          List<BlockArray.BlockStateDescriptor> descriptors = Lists.newArrayList();
          JsonArray elements = variableElement.getAsJsonArray();
          for (int i = 0; i < elements.size(); i++) {
            JsonElement p = elements.get(i);
            if(!p.isJsonPrimitive() || !p.getAsJsonPrimitive().isString()) {
              throw new JsonParseException("Elements of a variable have to be BlockState descriptions! You cannot nest variables!");
            }
            descriptors.add(BlockArray.BlockInformation.getDescriptor(p.getAsString()));
          }
          var.variables.put(entry.getKey(), new BlockArray.BlockInformation(descriptors));
        } else if(variableElement.isJsonPrimitive() && variableElement.getAsJsonPrimitive().isString()) {
          var.variables.put(entry.getKey(), new BlockArray.BlockInformation(Lists.newArrayList(BlockArray.BlockInformation.getDescriptor(variableElement.getAsString()))));
        } else {
          throw new JsonParseException("Variable '" + entry.getKey() + "' has as its value neither an array of BlockState definitions nor a single BlockState as String!");
        }
      }
      return var;
    }
  }
}
