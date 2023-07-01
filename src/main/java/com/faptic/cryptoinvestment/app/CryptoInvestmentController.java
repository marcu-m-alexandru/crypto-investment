package com.faptic.cryptoinvestment.app;

import com.faptic.cryptoinvestment.TimePeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CryptoInvestmentController {

  private final CryptoInvestmentService cryptoInvestmentService;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleException(Exception ex) {
    return ex.getMessage();
  }

  @GetMapping("/statistics/{id}")
  @Operation(summary = "Get all available statistics for a given crypto",
      parameters = {
          @Parameter(name = "id", description = "Symbol of cryptocurrency, e.g. BTC")
      })
  public Map<TimePeriod, ArrayList<Statistics>> findById(@PathVariable String id) {
    return cryptoInvestmentService.findById(id);
  }

  @GetMapping("/statistics/highest-normalized-range-day")
  @Operation(summary = "Return the crypto with the highest normalized range for a specific day",
      parameters = {
          @Parameter(name = "localDate", description = "Date in the form of yyyy-MM-dd (e.g. 2022-01-31)")
      })
  public String findCryptoNameByHighestNormalizedRangeDay(@RequestParam LocalDate localDate) {
    return cryptoInvestmentService.findCryptoNameByHighestNormalizedRangeDay(localDate);
  }

  @PostMapping("/import-data")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Import all values from given csv file",
      parameters = {
          @Parameter(name = "cryptoName", description = "Symbol of cryptocurrency, e.g. BTC"),
          @Parameter(name = "fileName", description = "Full absolute path of the file to be imported")
      })
  public void importData(@RequestParam String cryptoName, @RequestParam String fileName)
      throws IOException {

    cryptoInvestmentService.importData(cryptoName, fileName);
  }
}
