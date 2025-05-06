package org.htw.drawdigits.util;

import java.util.LinkedList;
import java.util.List;

public class DigitCanvas {
  private LinkedList<Draw> draws = new LinkedList<>();

  public List<Draw> getDraws() {
    return draws;
  }

  public void addDraw(Draw draw) {
    this.draws.add(draw);
  }

  public void undoDraw() {
    this.draws.removeLast();
  }

  public void reset() {
    draws = new LinkedList<>();
  }
}
