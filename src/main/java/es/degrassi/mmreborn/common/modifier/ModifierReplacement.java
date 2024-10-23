package es.degrassi.mmreborn.common.modifier;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.api.PartialBlockState;
import es.degrassi.mmreborn.common.util.MiscUtils;
import java.util.Collections;
import java.util.List;

public class ModifierReplacement {

  private final PartialBlockState info;
  private final List<RecipeModifier> modifier;
  private final List<String> description;

  public ModifierReplacement(PartialBlockState info, List<RecipeModifier> modifier, String description) {
    this.info = info;
    this.modifier = modifier;
    this.description = description.isEmpty() ? Lists.newArrayList() : MiscUtils.splitStringBy(description, "\n");
  }

  public PartialBlockState getBlockInformation() {
    return info;
  }

  public List<RecipeModifier> getModifiers() {
    return Collections.unmodifiableList(modifier);
  }

  public List<String> getDescriptionLines() {
    return description;
  }
}
