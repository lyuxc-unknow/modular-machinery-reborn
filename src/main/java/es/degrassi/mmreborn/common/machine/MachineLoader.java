package es.degrassi.mmreborn.common.machine;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.degrassi.mmreborn.common.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.util.BlockArray;
import es.degrassi.mmreborn.common.util.BlockInformationVariable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MachineLoader {
  public static final Gson GSON = new GsonBuilder()
//    .registerTypeHierarchyAdapter(DynamicMachine.class, new DynamicMachine.MachineDeserializer())
    .registerTypeHierarchyAdapter(BlockInformationVariable.class, new BlockInformationVariable.Deserializer())
    .registerTypeHierarchyAdapter(ModifierReplacement.class, new ModifierReplacement.Deserializer())
    .registerTypeHierarchyAdapter(RecipeModifier.class, new RecipeModifier.Deserializer())
    .create();

  private static Map<String, Exception> failedAttempts = new HashMap<>();
  public static Map<String, BlockArray.BlockInformation> variableContext = new HashMap<>();

  public static Map<FileType, List<File>> discoverDirectory(File directory) {
    Map<FileType, List<File>> candidates = new HashMap<>();
    for (FileType type : FileType.values()) {
      candidates.put(type, Lists.newLinkedList());
    }
    LinkedList<File> directories = Lists.newLinkedList();
    directories.add(directory);
    while (!directories.isEmpty()) {
      File dir = directories.remove(0);
      for (File f : dir.listFiles()) {
        if(f.isDirectory()) {
          directories.addLast(f);
        } else {
          //I am *not* taking chances with this ordering
          if(FileType.VARIABLES.accepts(f.getName())) {
            candidates.get(FileType.VARIABLES).add(f);
          } else if(FileType.MACHINE.accepts(f.getName())) {
            candidates.get(FileType.MACHINE).add(f);
          }
        }
      }
    }
    return candidates;
  }


  public static List<DynamicMachine> loadMachines(List<File> machineCandidates) {
    List<DynamicMachine> loadedMachines = Lists.newArrayList();
    for (File f : machineCandidates) {
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
        loadedMachines.add(GSON.fromJson(isr, DynamicMachine.class));
      } catch (Exception exc) {
        failedAttempts.put(f.getPath(), exc);
      }
    }
    return loadedMachines;
  }

  public static Map<String, Exception> captureFailedAttempts() {
    Map<String, Exception> failed = failedAttempts;
    failedAttempts = new HashMap<>();
    return failed;
  }

  public static void prepareContext(List<File> files) {
    variableContext.clear();

    for (File f : files) {
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f))) {
        Map<String, BlockArray.BlockInformation> variables = GSON.fromJson(isr, BlockInformationVariable.class).getDefinedVariables();
        for (String key : variables.keySet()) {
          variableContext.put(key, variables.get(key));
        }
      } catch (Exception exc) {
        failedAttempts.put(f.getPath(), exc);
      }
    }
  }

  public enum FileType {
    VARIABLES,
    MACHINE;

    public boolean accepts(String fileName) {
      return switch (this) {
        case VARIABLES -> fileName.endsWith(".var.json");
        case MACHINE -> fileName.endsWith(".json");
      };
    }

  }
}
