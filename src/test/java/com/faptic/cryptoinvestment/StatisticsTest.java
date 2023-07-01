package com.faptic.cryptoinvestment;

import com.faptic.cryptoinvestment.app.Statistics;
import com.faptic.cryptoinvestment.app.Tick;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatisticsTest {

  private static final Tick TICK_0 = Tick.of(0, 0);
  private static final Tick TICK_1 = Tick.of(1, 1);
  private static final Tick TICK_2 = Tick.of(2, 2);
  private static final Tick TICK_3 = Tick.of(3, 3);

  @Test
  public void calculateNormalizedRange_givenValidData_mustSucceed() {
    Statistics s = Statistics.of(TICK_1, TICK_2, TICK_1, TICK_2);
    // act
    float actual = s.calculateNormalizedRange();
    // assert
    Assertions.assertEquals(1, actual, 0.01);
  }

  @Test
  public void calculateNormalizedRange_givenPriceOfZero_willNotThrowException() {
    Statistics s = Statistics.of(TICK_0, TICK_2, TICK_0, TICK_2);
    // act
    float actual = s.calculateNormalizedRange();
    // assert
    Assertions.assertTrue(Float.isInfinite(actual));
  }

  @Test
  public void merge_givenValidData_mustSucceed() {
    Statistics s1 = Statistics.of(TICK_1, TICK_2, TICK_1, TICK_2);
    Statistics s2 = Statistics.of(TICK_2, TICK_3, TICK_2, TICK_3);
    Statistics expected = Statistics.of(TICK_1, TICK_3, TICK_1, TICK_3);
    // act
    Statistics actual = Statistics.merge(List.of(s1, s2));
    // assert
    Assertions.assertEquals(expected, actual);
  }
}
