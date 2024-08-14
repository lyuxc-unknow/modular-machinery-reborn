package es.degrassi.mmreborn.api.codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class OptionalFieldCodec<A> extends NamedMapCodec<Optional<A>> {

  public static <A> NamedMapCodec<Optional<A>> of(String fieldName, NamedCodec<A> elementCodec, String name) {
    return new OptionalFieldCodec<>(fieldName, elementCodec, name);
  }

  private final String fieldName;
  private final NamedCodec<A> elementCodec;
  private final String name;

  private OptionalFieldCodec(String fieldName, NamedCodec<A> elementCodec, String name) {
    this.fieldName = FieldCodec.toSnakeCase(fieldName);
    this.elementCodec = elementCodec;
    this.name = name;
  }

  public OptionalFieldCodec<A> aliases(String... aliases) {
    this.aliases.addAll(Arrays.asList(aliases));
    return this;
  }

  @Override
  public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
    T value = FieldCodec.tryGetValue(ops, input, fieldName);
    if(value == null) {
      for(String alias : this.aliases) {
        value = input.get(alias);
        if(value != null)
          break;
      }
    }
    if(value == null) {
      if(MMRConfig.get().general.logMissingOptional)
        MMRLogger.INSTANCE.debug("Missing optional property: \"{}\" of type: {}, using default value", fieldName, name);
      return DataResult.success(Optional.empty());
    }
    DataResult<A> result = elementCodec.read(ops, value);
    if(result.result().isPresent())
      return result.map(Optional::of);
    if(result.error().isPresent())
      MMRLogger.INSTANCE.warn("Couldn't parse \"{}\" for key \"{}\", using default value\nError: {}", name, fieldName, result.error().get().message());
    return DataResult.success(Optional.empty());
  }

  @Override
  public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
    if (input.isPresent())
      return prefix.add(fieldName, elementCodec.encodeStart(ops, input.get()));
    return prefix;
  }

  @Override
  public <T> Stream<T> keys(DynamicOps<T> ops) {
    return Stream.of(ops.createString(this.fieldName));
  }

  @Override
  public String name() {
    return this.name;
  }
}
