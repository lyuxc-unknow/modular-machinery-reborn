package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.ItemInputBusEntity;
import es.degrassi.mmreborn.common.entity.ItemOutputBusEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockOutputBus extends BlockMachineComponent {
  private final ItemBusSize size;
  public BlockOutputBus(ItemBusSize size) {
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
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    switch (size) {
      case TINY ->        drops.add(ItemRegistration.ITEM_OUTPUT_BUS_TINY.get().getDefaultInstance());
      case SMALL ->       drops.add(ItemRegistration.ITEM_OUTPUT_BUS_SMALL.get().getDefaultInstance());
      case NORMAL ->      drops.add(ItemRegistration.ITEM_OUTPUT_BUS_NORMAL.get().getDefaultInstance());
      case REINFORCED ->  drops.add(ItemRegistration.ITEM_OUTPUT_BUS_REINFORCED.get().getDefaultInstance());
      case BIG ->         drops.add(ItemRegistration.ITEM_OUTPUT_BUS_BIG.get().getDefaultInstance());
      case HUGE ->        drops.add(ItemRegistration.ITEM_OUTPUT_BUS_HUGE.get().getDefaultInstance());
      case LUDICROUS ->   drops.add(ItemRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get().getDefaultInstance());
    }
    return drops;
  }

  @Override
  public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof TileInventory entity) {
      IOInventory inv = entity.getInventory();
      for (int i = 0; i < inv.getSlots(); i++) {
        ItemStack stack = inv.getStackInSlot(i);
        if(!stack.isEmpty()) {
          popResource(level, pos, stack);
          inv.setStackInSlot(i, ItemStack.EMPTY);
        }
      }
    }
    super.playerDestroy(level, player, pos, state, blockEntity, tool);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if(player.getAbilities().instabuild && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof TileInventory entity) {
      IOInventory inv = entity.getInventory();
      for (int i = 0; i < inv.getSlots(); i++) {
        ItemStack stack = inv.getStackInSlot(i);
        if(!stack.isEmpty()) {
          popResource(level, pos, stack);
          inv.setStackInSlot(i, ItemStack.EMPTY);
        }
      }
    }
    return super.playerWillDestroy(level, pos, state, player);
  }
  @Override
  public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> tooltip, TooltipFlag pTooltipFlag) {
    super.appendHoverText(pStack, pContext, tooltip, pTooltipFlag);
    tooltip.add(
      size.getSlotCount() == 1 ?
        Component.translatable("tooltip.itembus.slot") :
        Component.translatable("tooltip.itembus.slots", size.getSlotCount())
    );
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if(level.isClientSide()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof ItemInputBusEntity) {
//            playerIn.openGui(ModularMachinery.MODID, CommonProxy.GuiType.ITEM_INVENTORY.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
      return ItemInteractionResult.SUCCESS;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new ItemOutputBusEntity(blockPos, blockState, size);
  }
}
