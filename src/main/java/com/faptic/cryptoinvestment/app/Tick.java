package com.faptic.cryptoinvestment.app;

import lombok.Data;

/*
Assumptions:
- price is always > 0
*/
@Data
public class Tick {

  private long timestamp;
  /*
  float might not be the best type to use;
  we might want to use something like BigDecimal.
   */
  private float price;

  public static Tick of(long timestamp, float price) {
    Tick t = new Tick();
    t.setTimestamp(timestamp);
    t.setPrice(price);
    return t;
  }
}
