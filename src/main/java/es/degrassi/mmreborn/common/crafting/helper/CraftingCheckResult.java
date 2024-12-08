package es.degrassi.mmreborn.common.crafting.helper;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CraftingCheckResult {

  public static final CraftingCheckResult SUCCESS = new CraftingCheckResult(1);
  public static CraftingCheckResult empty() {
    return new CraftingCheckResult();
  }

  private final Map<String, Integer> unlocErrorMessages = new HashMap<>();
  @Getter
  private float validity = 0F;

  private CraftingCheckResult() {}

  private CraftingCheckResult(float validity) {
    this.validity = validity;
  }

  void setValidity(float validity) {
    this.validity = validity;
  }

  void addError(String unlocError) {
    if (!unlocError.isEmpty()) {
      int count = this.unlocErrorMessages.getOrDefault(unlocError, 0);
      count++;
      this.unlocErrorMessages.put(unlocError, count);
    }
  }

  public List<String> getUnlocalizedErrorMessages() {
    return this.unlocErrorMessages.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  public boolean isFailure() {
    return !this.unlocErrorMessages.isEmpty() && validity != 1;
  }
}
