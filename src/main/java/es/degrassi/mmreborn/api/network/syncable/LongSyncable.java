package es.degrassi.mmreborn.api.network.syncable;

import es.degrassi.mmreborn.api.network.AbstractSyncable;
import es.degrassi.mmreborn.api.network.data.LongData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LongSyncable extends AbstractSyncable<LongData, Long> {

    @Override
    public LongData getData(short id) {
        return new LongData(id, get());
    }

    public static LongSyncable create(Supplier<Long> supplier, Consumer<Long> consumer) {
        return new LongSyncable() {
            @Override
            public Long get() {
                return supplier.get();
            }

            @Override
            public void set(Long value) {
                consumer.accept(value);
            }
        };
    }
}
