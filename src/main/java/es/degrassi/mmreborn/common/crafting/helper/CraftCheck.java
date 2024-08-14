/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package es.degrassi.mmreborn.common.crafting.helper;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

@Getter
public class CraftCheck {

  private static final CraftCheck SUCCESS = new CraftCheck(ResultType.SUCCESS, "");
  private static final CraftCheck PARTIAL_SUCCESS = new CraftCheck(ResultType.PARTIAL_SUCCESS, "");
  private static final CraftCheck INVALID_SKIP = new CraftCheck(ResultType.INVALID_SKIP, "");

  private final ResultType type;
  private final String unlocalizedMessage;

  protected CraftCheck(ResultType type, String unlocalizedMessage) {
    this.type = type;
    this.unlocalizedMessage = unlocalizedMessage;
  }

  public static CraftCheck success() {
    return SUCCESS;
  }

  public static CraftCheck partialSuccess() {
    return PARTIAL_SUCCESS;
  }

  public static CraftCheck skipComponent() {
    return INVALID_SKIP;
  }

  public static CraftCheck failure(String unlocMessage) {
    return new CraftCheck(ResultType.FAILURE_MISSING_INPUT, unlocMessage);
  }

  public boolean isSuccess() {
    return this.type == ResultType.SUCCESS;
  }

  public boolean isInvalid() {
    return this.type == ResultType.INVALID_SKIP;
  }

  public CompoundTag serialize() {
    CompoundTag tag = new CompoundTag();
    tag.putInt("type", this.type.ordinal());
    tag.putString("message", this.unlocalizedMessage);
    return tag;
  }

  public static CraftCheck deserialize(CompoundTag tag) {
    ResultType type = ResultType.values()[tag.getInt("type")];
    String unlocMessage = tag.getString("message");
    return new CraftCheck(type, unlocMessage);
  }

  public enum ResultType {

    //requirement check succeeded.
    SUCCESS,

    //Meaning this has successfully done something, however other components might need to be checked to see
    //if this is an actual success or if just some parts are successful
    //NOTE: Only to be used in PerTick-subclasses for per-tick operations!
    PARTIAL_SUCCESS,

    //requirement check failed
    FAILURE_MISSING_INPUT,

    //component is not suitable to be checked for given requirement-check (i.e. component type != requirement type)
    INVALID_SKIP

  }

}
