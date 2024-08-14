package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.item.ItemBlueprint;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockController extends BlockMachineComponent {
  public BlockController() {
    super(
      Properties.of()
        .sound(SoundType.METAL)
        .strength(5F, 10F)
        .dynamicShape()
        .noOcclusion()
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new MachineControllerEntity(blockPos, blockState);
  }

  @Override
  public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof MachineControllerEntity entity) {
      ControllerRenderer.renderers.remove(entity.getBlockPos());
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
    if(player.getAbilities().instabuild && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof MachineControllerEntity entity) {
      ControllerRenderer.renderers.remove(entity.getBlockPos());
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
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    if(builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof MachineControllerEntity entity) {
      IOInventory inv = entity.getInventory();
      for (int i = 0; i < inv.getSlots(); i++) {
        ItemStack stack = inv.getStackInSlot(i);
        if(!stack.isEmpty()) {
          drops.add(stack);
        }
      }
    }
    return drops;
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
    return true;
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof MachineControllerEntity controller) {
      if (player instanceof ServerPlayer serverPlayer) {
        if (player.getItemInHand(hand).getItem() instanceof ItemBlueprint) {
          DynamicMachine machine = ItemBlueprint.getAssociatedMachine(player.getItemInHand(hand));
          if (machine == null) return ItemInteractionResult.FAIL;
          ControllerRenderer.add(machine, pos);
          return ItemInteractionResult.SUCCESS;
        }
        ControllerContainer.open(serverPlayer, controller);
      }
      return ItemInteractionResult.SUCCESS;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    BlockEntity tile = level.getBlockEntity(pos);
    if(tile instanceof MachineControllerEntity entity)
      RedstoneHelper.getRedstoneLevel(entity);
    return 0;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    BlockEntity tile = level.getBlockEntity(pos);
    if(tile instanceof MachineControllerEntity entity)
      RedstoneHelper.getRedstoneLevel(entity);
    return 0;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    BlockEntity tile = level.getBlockEntity(pos);
    if(tile instanceof MachineControllerEntity entity)
      RedstoneHelper.getRedstoneLevel(entity);
    return 0;
  }
}
