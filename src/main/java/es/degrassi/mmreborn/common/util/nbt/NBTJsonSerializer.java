package es.degrassi.mmreborn.common.util.nbt;

import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NBTJsonSerializer {

  public static String serializeNBT(Tag nbtBase) {
    StringBuilder sb = new StringBuilder();
    switch (nbtBase.getId()) {
      case Tag.TAG_BYTE, Tag.TAG_DOUBLE, Tag.TAG_FLOAT, Tag.TAG_LONG, Tag.TAG_INT, Tag.TAG_SHORT, Tag.TAG_STRING: {
        sb.append(StringTag.quoteAndEscape(nbtBase.toString()));
        break;
      }
      case Tag.TAG_LIST: {
        StringBuilder stringbuilder = new StringBuilder("[");
        ListTag listTag = (ListTag) nbtBase;

        for (int i = 0; i < listTag.size(); ++i) {
          if (i != 0) {
            stringbuilder.append(',');
          }

          stringbuilder.append(serializeNBT(listTag.get(i)));
        }
        sb.append(stringbuilder.append(']'));
        break;
      }
      case Tag.TAG_COMPOUND: {
        StringBuilder stringbuilder = new StringBuilder("{");
        CompoundTag cmpTag = (CompoundTag) nbtBase;
        Set<String> collection = cmpTag.getAllKeys();

        for (String s : collection) {
          if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
          }

          stringbuilder.append(StringTag.quoteAndEscape(s)).append(':').append(serializeNBT(Objects.requireNonNull(cmpTag.get(s))));
        }

        sb.append(stringbuilder.append('}'));
        break;
      }
    }
    return sb.toString();
  }

}
