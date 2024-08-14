package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCasing extends BlockMachineComponent {
//  public static final EnumProperty<CasingType> CASING = EnumProperty.create("casing", CasingType.class);
//  private String identifier;
//  private final CasingType type;
  public BlockCasing(/*CasingType type*/) {
    super(
      Properties.of()
        .strength(2F, 10F)
        .sound(SoundType.METAL)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
    );
//    this.type = type;
  }

//  @Override
//  public @NotNull MutableComponent getName() {
//    return Component.translatable(super.getDescriptionId() + "." + type.getSerializedName());
//  }

  //  @Override
//  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//    super.createBlockStateDefinition(builder);
//    builder.add(CASING);
//  }

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
