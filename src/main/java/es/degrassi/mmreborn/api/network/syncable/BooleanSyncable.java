package es.degrassi.mmreborn.api.network.syncable;

import es.degrassi.mmreborn.api.network.AbstractSyncable;
import es.degrassi.mmreborn.api.network.data.BooleanData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BooleanSyncable extends AbstractSyncable<BooleanData, Boolean> {

    @Override
    public BooleanData getData(short id) {
        return new BooleanData(id, get());
    }

    public static BooleanSyncable create(Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
        return new BooleanSyncable() {
            @Override
            public Boolean get() {
                return supplier.get();
            }

            @Override
            public void set(Boolean value) {
                consumer.accept(value);
            }
        };
    }
}
