package es.degrassi.mmreborn.common.util.nbt;

import java.util.regex.Pattern;
import net.minecraft.nbt.StringTag;

public class NBTPatternString extends StringTag {

    private final Pattern strPattern;

    public NBTPatternString(String data) {
        this(data, Pattern.compile(data, Pattern.CASE_INSENSITIVE));
    }

    private NBTPatternString(String data, Pattern strPattern) {
        super(data);
        this.strPattern = strPattern;
    }

    @Override
    public NBTPatternString copy() {
        return new NBTPatternString(this.getAsString(), this.strPattern);
    }

    public boolean testString(String toTest) {
        return strPattern.matcher(toTest).matches();
    }

}
