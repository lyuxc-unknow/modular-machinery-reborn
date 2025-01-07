package es.degrassi.mmreborn.api.network.data;

import es.degrassi.mmreborn.api.network.Data;
import es.degrassi.mmreborn.common.registration.DataRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class NbtData extends Data<CompoundTag> {

    public NbtData(short id, CompoundTag value) {
        super(DataRegistration.NBT_DATA.get(), id, value);
    }

    public NbtData(short id, RegistryFriendlyByteBuf buffer) {
        this(id, buffer.readNbt());
    }

    @Override
    public void writeData(RegistryFriendlyByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeNbt(getValue());
    }
}
