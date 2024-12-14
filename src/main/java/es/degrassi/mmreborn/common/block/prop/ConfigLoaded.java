package es.degrassi.mmreborn.common.block.prop;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import java.util.Arrays;
import java.util.Map;

public interface ConfigLoaded {
  Map<Class<? extends Enum<?>>, ValueSetter<?>> setters = Maps.newLinkedHashMap();

  static <T extends Enum<T> & ConfigLoaded> void add(Class<T> clazz, ValueSetter<T> consumer) {
    setters.put(clazz, consumer);
  }

  @SafeVarargs
  static void add(Pair<Class<? extends Enum<?>>, ValueSetter<?>>... consumers) {
    Arrays.asList(consumers)
        .forEach(consumer -> setters.put(consumer.getFirst(), consumer.getSecond()));
  }

  static void load() {
    setters.forEach(ConfigLoaded::loadFromConfig);
  }

  @SuppressWarnings("unchecked")
  static <T extends Enum<T> & ConfigLoaded> void loadFromConfig(Class<?> clazz, ValueSetter<T> consumer) {
    for (T value : ((Class<T>) clazz).getEnumConstants()) {
      consumer.set(value);
    }
  }

  interface ValueSetter<T extends Enum<T> & ConfigLoaded> {
    void set(T value);
  }
}
