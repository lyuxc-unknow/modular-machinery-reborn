package es.degrassi.mmreborn.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ByteBufUtils {
  public static void writeItemStack(FriendlyByteBuf byteBuf, @Nonnull ItemStack stack) {
    boolean defined = !stack.isEmpty();
    byteBuf.writeBoolean(defined);
    if(defined) {
//      CompoundTag tag = new CompoundTag();
//      stack.writeToNBT(tag);
//      writeNBTTag(byteBuf, tag);
      byteBuf.writeJsonWithCodec(ItemStack.CODEC, stack);
    }
  }

  @Nonnull
  public static ItemStack readItemStack(FriendlyByteBuf byteBuf) {
    boolean defined = byteBuf.readBoolean();
    if(defined) {
      return byteBuf.readJsonWithCodec(ItemStack.CODEC);
    } else {
      return ItemStack.EMPTY;
    }
  }

//  public static void writeNBTTag(RegistryFriendlyByteBuf byteBuf, @Nonnull CompoundTag tag) {
//    try (DataOutputStream dos = new DataOutputStream(new ByteBufOutputStream(byteBuf))) {
//      NbtIo.write(tag, dos);
//    } catch (Exception ignored) {}
//  }
//
//  @Nonnull
//  public static CompoundTag readNBTTag(RegistryFriendlyByteBuf byteBuf) {
//    try (DataInputStream dis = new DataInputStream(new ByteBufInputStream(byteBuf))) {
//      return NbtIo.read(dis);
//    } catch (Exception ignored) {}
//    throw new IllegalStateException("Could not load NBT Tag from incoming byte buffer!");
//  }
}
