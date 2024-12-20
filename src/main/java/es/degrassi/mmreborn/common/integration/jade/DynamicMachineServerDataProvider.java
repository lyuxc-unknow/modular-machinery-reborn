package es.degrassi.mmreborn.common.integration.jade;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class DynamicMachineServerDataProvider implements IServerDataProvider<BlockAccessor> {

  public static final DynamicMachineServerDataProvider INSTANCE = new DynamicMachineServerDataProvider();
  public static final ResourceLocation ID = ModularMachineryReborn.rl("machine_server_data_provider");

  @Override
  public void appendServerData(CompoundTag nbt, BlockAccessor accessor) {
    if (accessor.getBlockEntity() instanceof MachineControllerEntity machine && machine.getLevel() != null) {
      CompoundTag tag = new CompoundTag();
      if (machine.isPaused()) {
        tag.putBoolean("paused", true);
      } else {
        tag.put("status", machine.getCraftingStatus().serializeNBT());
        if (machine.getActiveRecipe() != null) {
          tag.putDouble("progress", machine.getRecipeTicks());
          tag.putInt("total", machine.getActiveRecipe().getRecipe().getRecipeTotalTickTime());
        }
      }
      nbt.put(ModularMachineryReborn.MODID, tag);
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
