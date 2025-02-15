package es.degrassi.mmreborn.api.network;


public abstract class Data<T> implements IData<T> {

    private final DataType<?, T> type;
    private final short id;
    private final T value;

    public Data(DataType<?, T> type, short id, T value) {
        this.type = type;
        this.id = id;
        this.value = value;
    }

    @Override
    public short getID() {
        return this.id;
    }

    @Override
    public DataType<?, T> getType() {
        return this.type;
    }

    @Override
    public T getValue() {
        return this.value;
    }
}
