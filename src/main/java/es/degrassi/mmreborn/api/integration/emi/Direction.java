package es.degrassi.mmreborn.api.integration.emi;

public enum Direction {
  LEFT, RIGHT, BOTTOM, TOP;

  public boolean horizontal() {
    return this == LEFT || this == RIGHT;
  }

  public boolean endToStart() {
    return this == RIGHT || this == TOP;
  }
}
