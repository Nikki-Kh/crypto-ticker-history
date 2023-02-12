package com.nikh.cth.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.dao.TickerRateHistoryDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Slf4j
@AllArgsConstructor
public abstract class TickerRateUpdateTask implements Runnable{

    protected Integer brkId;
    protected String brokerName;
    protected Integer updateInterval;
    protected String apiAddr;
    protected String apiKey;
    protected List<String> tickers;

    @JsonIgnore
    protected WebClient webClient;
    @JsonIgnore
    protected TickerRateHistoryDao tickerRateHistoryDao;


    @Override
    public void run() {
        var result = getLatestTickerRates();
        log.info("Timestamp: {};\n Broker: {};\n Result: {}", LocalDateTime.now(), brokerName, result);
        if (!result.isEmpty()) {
            var count = uploadDataToDatabase(result);
        }
    }

    protected abstract List<TickerRate> getLatestTickerRates();

    protected int uploadDataToDatabase(List<TickerRate> rates) {
        return tickerRateHistoryDao.insertNewTickerRates(rates);
    }
}
