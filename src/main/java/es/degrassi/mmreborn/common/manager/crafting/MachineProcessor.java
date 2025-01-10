package es.degrassi.mmreborn.common.manager.crafting;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.IProcessor;
import es.degrassi.mmreborn.api.crafting.IProcessorTemplate;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.registration.ProcessorTypeRegistration;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Consumer;

@Accessors(fluent = true, makeFinal = true)
public class MachineProcessor implements IProcessor, ISyncableStuff {
  @Getter
  private final MachineControllerEntity tile;
  private boolean initialized = false;
  @Getter
  private final MachineProcessorCore core;

  public MachineProcessor(MachineControllerEntity tile) {
    this.tile = tile;
    this.core = new MachineProcessorCore(this, tile);
  }

  @Override
  public ProcessorType<MachineProcessor> getType() {
    return ProcessorTypeRegistration.MACHINE_PROCESSOR.get();
  }

  @Override
  public void tick() {
    if (!this.initialized)
      this.init();

    this.core.tick();

    if (this.tile.getStatus() == MachineStatus.RUNNING && this.core.getCurrentRecipe() == null) {
      this.tile.setStatus(MachineStatus.IDLE);
    }
  }

  private void init() {
    this.initialized = true;
    this.core.init();
  }

  public void setRunning() {
    this.tile.setStatus(MachineStatus.RUNNING);

//    if(this.cores.size() == 1) {
    RecipeHolder<MachineRecipe> currentRecipe = this.core.getCurrentRecipe();
    if (currentRecipe == null)
      return;
//    }
  }

  public void setError(Component message) {
    if (this.core.getError() != null || core.getCurrentRecipe() == null)
      this.tile.setStatus(MachineStatus.ERRORED, message);
  }

  @Override
  public void reset() {
    this.core.reset();
    if (this.tile.getStatus().isMissingStructure())
      this.tile.setStatus(this.tile.getStatus());
    else
      this.tile.setStatus(MachineStatus.IDLE);
  }

  @Override
  public void setMachineInventoryChanged() {
    this.core.setComponentChanged();
  }

  @Override
  public void setSearchImmediately() {
    this.core.setSearchImmediately();
  }

  @Override
  public CompoundTag serialize() {
    CompoundTag nbt = new CompoundTag();
    nbt.putString("type", getType().getId().toString());
//    ListTag cores = new ListTag();
//    this.cores.forEach(core -> cores.add(core.serialize()));
//    nbt.put("cores", cores);
    nbt.put("core", core.serialize());
    return nbt;
  }

  @Override
  public void deserialize(CompoundTag nbt) {
    if (nbt.contains("type", Tag.TAG_STRING) && !nbt.getString("type").equals(getType().getId().toString()))
      return;
    if (nbt.contains("core", Tag.TAG_COMPOUND)) {
      core.deserialize(nbt.getCompound("core"));
    }
//    if(nbt.contains("cores", Tag.TAG_LIST)) {
//      ListTag cores = nbt.getList("cores", Tag.TAG_COMPOUND);
//      if(this.cores.size() == cores.size()) {
//        for(int i = 0; i < this.cores.size(); i++)
//          this.cores.get(i).deserialize(cores.getCompound(i));
//      }
//    }
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    this.core.getStuffToSync(container);
  }

  public record Template() implements IProcessorTemplate<MachineProcessor> {
    public static final Template DEFAULT = new Template();
    public static final NamedCodec<Template> CODEC = NamedCodec.unit(DEFAULT);


    @Override
    public ProcessorType<MachineProcessor> getType() {
      return ProcessorTypeRegistration.MACHINE_PROCESSOR.get();
    }

    @Override
    public MachineProcessor build(MachineControllerEntity tile) {
      return new MachineProcessor(tile);
    }
  }
}
