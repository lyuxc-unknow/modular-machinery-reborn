package es.degrassi.mmreborn.api.network.data;

import es.degrassi.mmreborn.api.network.Data;
import es.degrassi.mmreborn.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class IntegerData extends Data<Integer> {

    public IntegerData(short id, int value) {
        super(DataRegistration.INTEGER_DATA.get(), id, value);
    }

    public IntegerData(short id, RegistryFriendlyByteBuf buffer) {
        this(id, buffer.readInt());
    }

    @Override
    public void writeData(RegistryFriendlyByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeInt(getValue());
    }
}
