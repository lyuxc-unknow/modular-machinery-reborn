package es.degrassi.mmreborn.common.crafting.helper;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CraftingStatus {
  public static final CraftingStatus SUCCESS = new CraftingStatus(Type.CRAFTING, "");
  public static final CraftingStatus MISSING_STRUCTURE = new CraftingStatus(Type.MISSING_STRUCTURE, "");
  public static final CraftingStatus NO_RECIPE = new CraftingStatus(Type.NO_RECIPE, "");

  @MethodsReturnNonnullByDefault
  public static final StreamCodec<RegistryFriendlyByteBuf, CraftingStatus> STREAM_CODEC = new StreamCodec<>() {
    @Override
    public CraftingStatus decode(RegistryFriendlyByteBuf buffer) {
      Type type = buffer.readEnum(Type.class);
      String unlocalizedMessage = buffer.readUtf();
      return new CraftingStatus(type, unlocalizedMessage);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CraftingStatus value) {
      buffer.writeEnum(value.status);
      buffer.writeUtf(value.unlocMessage);
    }
  };

  @Getter
  private final Type status;
  private final String unlocMessage;

  private CraftingStatus(Type status, String unlocMessage) {
    this.status = status;
    this.unlocMessage = unlocMessage;
  }

  public String getUnlocMessage() {
    return !unlocMessage.isEmpty() ? unlocMessage : this.status.getUnlocalizedDescription();
  }

  public boolean isCrafting() {
    return this.status == Type.CRAFTING;
  }

  public boolean isFailure() {
    return this.status == Type.FAILURE;
  }

  public boolean isMissingStructure() {
    return this.status == Type.MISSING_STRUCTURE;
  }

  public static CraftingStatus working() {
    return SUCCESS;
  }

  public static CraftingStatus failure(String unlocMessage) {
    return new CraftingStatus(Type.FAILURE, unlocMessage);
  }

  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("type", this.status.getSerializedName());
    tag.putString("message", this.unlocMessage);
    return tag;
  }

  public static CraftingStatus deserialize(CompoundTag tag) {
    Type type = Type.fromString(tag.getString("type"));
    String unlocMessage = tag.getString("message");
    return new CraftingStatus(type, unlocMessage);
  }

  public static CraftingStatus of(Type type, String message) {
    return new CraftingStatus(type, message);
  }

  public static CraftingStatus of(String type, String message) {
    return new CraftingStatus(Type.fromString(type), message);
  }

  public enum Type implements StringRepresentable {
    MISSING_STRUCTURE,
    NO_RECIPE,
    FAILURE,
    CRAFTING;

    public static Type fromString(String value) {
      return switch (value.toLowerCase(Locale.ROOT)) {
        case "missing_structure" -> MISSING_STRUCTURE;
        case "crafting" -> CRAFTING;
        case "no_recipe" -> NO_RECIPE;
        case "failure" -> FAILURE;
        default -> null;
      };
    }

    public boolean isFailure() {
      return this == FAILURE;
    }

    public String getUnlocalizedDescription() {
      return "gui.controller.status." + getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
}
