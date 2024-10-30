package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.client.container.ChemicalHatchContainer;
import es.degrassi.mmreborn.common.block.prop.ChemicalHatchSize;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockChemicalHatch extends BlockMachineComponent {
  protected final ChemicalHatchSize size;
  public BlockChemicalHatch(ChemicalHatchSize size) {
    super(
      Properties.of()
        .dynamicShape()
        .noOcclusion()
        .strength(2F, 10F)
        .sound(SoundType.METAL)
    );
    this.size = size;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag pTooltipFlag) {
    tooltip.add(
      Component.translatable("tooltip.chemicalhatch.tank.info", size.getSize()).withStyle(ChatFormatting.GRAY)
    );
  }

  @Override
  protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
    if (level.isClientSide()) return ItemInteractionResult.sidedSuccess(true);
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ChemicalTankEntity fluidTank) {
      if (player instanceof ServerPlayer serverPlayer) {
        ChemicalHatchContainer.open(serverPlayer, fluidTank);
      }
      return ItemInteractionResult.SUCCESS;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }
}
