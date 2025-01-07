package es.degrassi.mmreborn.api.network.syncable;

import es.degrassi.mmreborn.api.network.AbstractSyncable;
import es.degrassi.mmreborn.api.network.data.FloatData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FloatSyncable extends AbstractSyncable<FloatData, Float> {

  @Override
  public FloatData getData(short id) {
    return new FloatData(id, get());
  }

  public static FloatSyncable create(Supplier<Float> supplier, Consumer<Float> consumer) {
    return new FloatSyncable() {
      public Float get() {
        return supplier.get();
      }

      public void set(Float value) {
        consumer.accept(value);
      }
    };
  }
}
