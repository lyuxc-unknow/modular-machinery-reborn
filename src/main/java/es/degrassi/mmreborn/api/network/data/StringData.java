package es.degrassi.mmreborn.api.network.data;

import es.degrassi.mmreborn.api.network.Data;
import es.degrassi.mmreborn.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class StringData extends Data<String> {

    public StringData(short id, String value) {
        super(DataRegistration.STRING_DATA.get(), id, value);
    }

    public StringData(short id, RegistryFriendlyByteBuf buffer) {
        this(id, buffer.readUtf());
    }

    @Override
    public void writeData(RegistryFriendlyByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeUtf(getValue());
    }
}
