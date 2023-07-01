package com.faptic.cryptoinvestment.app;

import com.faptic.cryptoinvestment.GlobalState;
import com.faptic.cryptoinvestment.TimePeriod;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoInvestmentService {

  private final GlobalState globalState;

  public void importData(String cryptoName, String fileName) throws IOException {
    ArrayList<Tick> ticks = parseCsv(fileName);

    ArrayList<Statistics> ticksAsStatistics = new ArrayList<>();
    for (Tick t : ticks) {
      ticksAsStatistics.add(toStatistics(t));
    }

    importData(cryptoName, ticksAsStatistics);
  }

  public void importData(String cryptoName, List<Statistics> statistics) {
    globalState.getCryptoMap().putIfAbsent(cryptoName, new HashMap<>());
    HashMap<TimePeriod, ArrayList<Statistics>> cryptoStatistics = globalState.getCryptoMap()
        .get(cryptoName);

    ArrayList<Statistics> dayStatistics = calculateStatisticsGroupBy(
        (start, end) -> start.getDayOfMonth() != end.getDayOfMonth(), statistics);
    cryptoStatistics.putIfAbsent(TimePeriod.DAY, new ArrayList<>());
    ArrayList<Statistics> existingDayStatistics = cryptoStatistics.get(TimePeriod.DAY);
    // assuming that no overlap occurs
    existingDayStatistics.addAll(dayStatistics);
    existingDayStatistics.sort(Comparator.comparingLong(o -> o.getOldest().getTimestamp()));

    ArrayList<Statistics> monthStatistics = calculateStatisticsGroupBy(
        (start, end) -> start.getMonthValue() != end.getMonthValue(), dayStatistics);
    cryptoStatistics.putIfAbsent(TimePeriod.MONTH, new ArrayList<>());
    ArrayList<Statistics> existingMonthStatistics = cryptoStatistics.get(TimePeriod.MONTH);
    existingMonthStatistics.addAll(monthStatistics);
    existingMonthStatistics.sort(Comparator.comparingLong(o -> o.getOldest().getTimestamp()));
  }

  public ArrayList<Statistics> calculateStatisticsGroupBy(
      BiPredicate<LocalDate, LocalDate> groupingPredicate, List<Statistics> statisticsList) {

    ArrayList<Statistics> result = new ArrayList<>();
    LocalDate d1 = toLocalDate(statisticsList.get(0));
    int i1 = 0;
    int i2 = 0;
    for (int n = statisticsList.size(); i2 < n; i2++) {
      LocalDate d2 = toLocalDate(statisticsList.get(i2));
      if (groupingPredicate.test(d1, d2)) {
        result.add(Statistics.merge(statisticsList.subList(i1, i2)));
        i1 = i2;
        d1 = toLocalDate(statisticsList.get(i2));
      }
    }
    i2--;
    if (i1 != i2) {
      result.add(Statistics.merge(statisticsList.subList(i1, i2)));
    }

    return result;
  }

  public Map<TimePeriod, ArrayList<Statistics>> findById(String id) {
    return globalState.getCryptoMap().get(id);
  }

  public String findCryptoNameByHighestNormalizedRangeDay(LocalDate localDate) {
    HashMap<String, Float> normalizedRangeMap = new HashMap<>();
    HashMap<String, HashMap<TimePeriod, ArrayList<Statistics>>> cryptoMap = globalState.getCryptoMap();
    cryptoMap.forEach((cryptoName, statisticsMap) -> {
      ArrayList<Statistics> statisticsDay = statisticsMap.get(TimePeriod.DAY);
      if (statisticsDay != null) {
        for (Statistics s : statisticsDay) {
          if (toLocalDate(s).equals(localDate)) {
            normalizedRangeMap.put(cryptoName, s.calculateNormalizedRange());
            break;
          }
        }
      }
    });

    float maxNormalizedRange = Float.NEGATIVE_INFINITY;
    String maxCryptoName = null;
    for (Entry<String, Float> entry : normalizedRangeMap.entrySet()) {
      String cryptoName = entry.getKey();
      float nr = entry.getValue();
      if (maxNormalizedRange < nr) {
        maxNormalizedRange = nr;
        maxCryptoName = cryptoName;
      }
    }

    return maxCryptoName;
  }

  /*
   Assumptions:
   - ticks are already sorted ascending by timestamp
   - no duplicates
   - data is valid
   */
  private static ArrayList<Tick> parseCsv(String fileName) throws IOException {
    ArrayList<Tick> ticks = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      br.readLine(); // header is not needed, we discard it.

      for (String line; null != (line = br.readLine()); ) {
        try {
          String[] parts = line.split(",");
          Tick t = new Tick();
          t.setTimestamp(Long.parseLong(parts[0]));
          t.setPrice(Float.parseFloat(parts[2]));
          ticks.add(t);
        } catch (NumberFormatException e) {
          log.error("Could not parse line: {}", line);
        }
      }
    }

    return ticks;
  }

  private static LocalDate toLocalDate(Statistics statistics) {
    return Instant.ofEpochMilli(statistics.getOldest().getTimestamp())
        .atOffset(ZoneOffset.UTC).toLocalDate();
  }

  private static Statistics toStatistics(Tick tick) {
    Statistics s = new Statistics();
    s.setOldest(tick);
    s.setNewest(tick);
    s.setMin(tick);
    s.setMax(tick);

    return s;
  }
}
