package es.degrassi.mmreborn.api.network.data;

import es.degrassi.mmreborn.api.network.Data;
import es.degrassi.mmreborn.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class LongData extends Data<Long> {

    public LongData(short id, long value) {
        super(DataRegistration.LONG_DATA.get(), id, value);
    }

    public LongData(short id, RegistryFriendlyByteBuf buffer) {
        this(id, buffer.readLong());
    }

    @Override
    public void writeData(RegistryFriendlyByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeLong(getValue());
    }
}
