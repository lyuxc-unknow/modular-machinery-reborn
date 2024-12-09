package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.PartialBlockState;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ControllerItem extends ItemBlockMachineComponent {
  public static final ResourceLocation DUMMY = ModularMachineryReborn.rl("dummy");

  public ControllerItem() {
    super(
        BlockRegistration.CONTROLLER.get(),
        new Properties()
            .component(Registration.MACHINE_DATA, DUMMY)
    );
  }

  public static Optional<DynamicMachine> getMachine(ItemStack stack) {
    return Optional.ofNullable(stack.get(Registration.MACHINE_DATA)).flatMap(id -> Optional.ofNullable(ModularMachineryReborn.MACHINES.get(id))).or(() -> Optional.of(DynamicMachine.DUMMY));
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.tooltip.0"));
    tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.tooltip.1"));
    getMachine(stack).ifPresentOrElse(machine -> {
      if (tooltipFlag.hasShiftDown()) {
        tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.required"));
        machine.getPattern().getPattern().asList().stream().flatMap(List::stream).flatMap(s -> s.chars().mapToObj(c -> (char) c)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).forEach((key, amount) -> {
          BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(key);
          if (ingredient != null && amount > 0) {
            String k;
            String value;
            if (ingredient.isTag()) {
              k = "tag";
              value = ingredient.getTags()
                  .stream()
                  .map(TagKey::location)
                  .map(ResourceLocation::toString)
                  .map(s -> "#" + s)
                  .toList()
                  .toString();
            } else {
              k = "block";
              value = ingredient.getAll()
                  .stream()
                  .map(PartialBlockState::toString)
                  .toList()
                  .toString();
            }
            tooltipComponents.add(
                Component.translatable(
                    "modular_machinery_reborn.controller.required." + k,
                    String.format("%dx %s", amount, value)
                )
            );
          }
        });
      } else {
        tooltipComponents.add(
            Component.empty()
                .append(Component.translatable("modular_machinery_reborn.controller.shift").withStyle(ChatFormatting.AQUA))
                .append(" ")
                .append(Component.translatable("modular_machinery_reborn.controller.shift.blocks").withStyle(ChatFormatting.GRAY))
        );
      }
    }, () -> tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.no_machine")));
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    return getMachine(stack).map(DynamicMachine::getMachineColor).orElse(super.getColorFromItemstack(stack, tintIndex));
  }

  public static ItemStack makeMachineItem(ResourceLocation machine) {
    if (ModularMachineryReborn.MACHINES_BLOCK.containsKey(machine))
      return ModularMachineryReborn.MACHINES_BLOCK.get(machine).asItem().getDefaultInstance();

    ItemStack stack = ItemRegistration.CONTROLLER.get().getDefaultInstance();
    stack.set(Registration.MACHINE_DATA, machine);
    return stack;
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    if (!context.canPlace()) return InteractionResult.FAIL;
    BlockPlaceContext context2 = updatePlacementContext(context);
    if (context2 == null) return InteractionResult.FAIL;
    BlockState state = getPlacementState(context2);
    if (state == null) return InteractionResult.FAIL;
    if (!this.placeBlock(context2, state)) return InteractionResult.FAIL;
    BlockPos pos = context2.getClickedPos();
    Level level = context2.getLevel();
    Player player = context2.getPlayer();
    ItemStack stack = context2.getItemInHand();
    BlockState state2 = level.getBlockState(pos);
    if (state2.is(state.getBlock())) {
      updateCustomBlockEntityTag(pos, level, player, stack, state2);
      state2.getBlock().setPlacedBy(level, pos, state2, player, stack);
      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.PLACED_BLOCK.trigger(sPlayer, pos, stack);
      }
    }

    if (state2.getBlock() instanceof BlockController controller) {
      SoundType soundType = controller.getSoundType(state, level, pos, player);
      level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1F) / 2F, soundType.getPitch() * 0.8F);
      level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
    }

    if (player == null || !player.getAbilities().instabuild) {
      stack.shrink(1);
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public @NotNull Component getName(@NotNull ItemStack pStack) {
    return getMachine(pStack).map(DynamicMachine::getName).orElse(super.getName(pStack));
  }
}
