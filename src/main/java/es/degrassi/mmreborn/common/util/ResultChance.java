package es.degrassi.mmreborn.common.util;

import java.util.Random;

public class ResultChance {
  public static ResultChance GUARANTEED = new ResultChance(0L) {
    @Override
    public boolean canProduce(float chance) {
      return true;
    }
  };

  private final Random rand;

  public ResultChance(long seed) {
    this.rand = new Random(seed);
  }

  public boolean canProduce(float chance) {
    return chance <= rand.nextFloat();
  }

}
