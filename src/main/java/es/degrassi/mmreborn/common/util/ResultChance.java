package es.degrassi.mmreborn.common.util;

import net.minecraft.util.RandomSource;

public class ResultChance {
  public static ResultChance GUARANTEED = new ResultChance(0L) {
    @Override
    public boolean canProduce(float chance) {
      return true;
    }
  };

  private final RandomSource rand;

  public ResultChance(long seed) {
    this.rand = RandomSource.create(seed);
  }

  public boolean canProduce(float chance) {
    return chance <= rand.nextFloat();
  }

}
