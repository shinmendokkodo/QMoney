
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {
  private RestTemplate restTemplate;
  private static final String TIINGO_TOKEN = "0e61dbe4346af3c6b8a46db3e0fcd9907211072a";
  private static final String TIINGO_URL_FORMAT = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$TOKEN";

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
        throws JsonProcessingException, StockQuoteServiceException, RuntimeException {
    return fetchCandles(symbol, from, to);
  }

  private List<Candle> fetchCandles(String symbol, LocalDate from, LocalDate to) 
        throws JsonProcessingException, StockQuoteServiceException, RuntimeException {
    if (from.compareTo(to) >= 0) {
      throw new RuntimeException();
    }

    List<Candle> tiingoCandles = new ArrayList<>();

    try {
      String tiingoResponse = restTemplate.getForObject(buildUri(symbol, from, to), 
          String.class);
      TiingoCandle[] candles = getObjectMapper()
          .readValue(tiingoResponse, TiingoCandle[].class);
      tiingoCandles = Arrays.asList(candles);
    } catch (NullPointerException ex) {
      throw new StockQuoteServiceException("Tiingo: API Error", ex);
    }
    
    return tiingoCandles;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    return TIINGO_URL_FORMAT.replace("$TOKEN", TIINGO_TOKEN)
          .replace("$SYMBOL", symbol)
          .replace("$STARTDATE", startDate.toString())
          .replace("$ENDDATE", endDate.toString());
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
