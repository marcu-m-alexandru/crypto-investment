package com.faptic.cryptoinvestment.app;

import java.util.List;
import lombok.Data;

@Data
public class Statistics {

  private Tick oldest;
  private Tick newest;
  private Tick min;
  private Tick max;

  public float calculateNormalizedRange() {
    return (max.getPrice() - min.getPrice()) / min.getPrice();
  }

  public static Statistics merge(List<Statistics> statisticsList) {
    Statistics result = Statistics.from(statisticsList.get(0));
    // this is just a min / max comparison for each individual field
    // todo can it be improved? or made more readable?
    for (Statistics s : statisticsList) {
      if (result.getOldest().getTimestamp() > s.getOldest().getTimestamp()) {
        result.setOldest(s.getOldest());
      }

      if (result.getNewest().getTimestamp() < s.getNewest().getTimestamp()) {
        result.setNewest(s.getNewest());
      }

      if (result.getMin().getPrice() > s.getMin().getPrice()) {
        result.setMin(s.getMin());
      }

      if (result.getMax().getPrice() < s.getMax().getPrice()) {
        result.setMax(s.getMax());
      }
    }

    return result;
  }

  public static Statistics from(Statistics other) {
    Statistics s = new Statistics();
    s.setOldest(other.getOldest());
    s.setNewest(other.getNewest());
    s.setMin(other.getMin());
    s.setMax(other.getMax());

    return s;
  }

  public static Statistics of(Tick oldest, Tick newest, Tick min, Tick max) {
    Statistics s = new Statistics();
    s.setOldest(oldest);
    s.setNewest(newest);
    s.setMin(min);
    s.setMax(max);

    return s;
  }
}
