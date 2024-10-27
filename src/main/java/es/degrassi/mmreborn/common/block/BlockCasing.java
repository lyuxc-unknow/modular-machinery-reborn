package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class BlockCasing extends BlockMachineComponent {
  public BlockCasing() {
    super(
      Properties.of()
        .strength(2F, 10F)
        .sound(SoundType.METAL)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
    );
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    if (this == BlockRegistration.CASING_PLAIN.get())       drops.add(ItemRegistration.CASING_PLAIN.get().getDefaultInstance());
    if (this == BlockRegistration.CASING_VENT.get())        drops.add(ItemRegistration.CASING_VENT.get().getDefaultInstance());
    if (this == BlockRegistration.CASING_FIREBOX.get())     drops.add(ItemRegistration.CASING_FIREBOX.get().getDefaultInstance());
    if (this == BlockRegistration.CASING_GEARBOX.get())     drops.add(ItemRegistration.CASING_GEARBOX.get().getDefaultInstance());
    if (this == BlockRegistration.CASING_REINFORCED.get())  drops.add(ItemRegistration.CASING_REINFORCED.get().getDefaultInstance());
    if (this == BlockRegistration.CASING_CIRCUITRY.get())   drops.add(ItemRegistration.CASING_CIRCUITRY.get().getDefaultInstance());
    return drops;
  }

  public enum CasingType implements StringRepresentable {
    PLAIN,
    VENT,
    FIREBOX,
    GEARBOX,
    REINFORCED,
    CIRCUITRY;

    @Override
    public @NotNull String getSerializedName() {
      return name().toLowerCase();
    }

    public static CasingType value(String name) {
      return switch(name.toLowerCase(Locale.ROOT)) {
        case "vent" -> VENT;
        case "firebox" -> FIREBOX;
        case "gearbox" -> GEARBOX;
        case "reinforced" -> REINFORCED;
        case "circuitry" -> CIRCUITRY;
        default -> PLAIN;
      };
    }
  }

}
