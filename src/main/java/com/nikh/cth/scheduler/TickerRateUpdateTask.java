package com.nikh.cth.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.dao.TickerRateHistoryDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Data
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
        if (!result.isEmpty()) {
            uploadDataToDatabase(result);
        }
    }

    protected abstract List<TickerRate> getLatestTickerRates();

    protected void uploadDataToDatabase(List<TickerRate> rates) {
        tickerRateHistoryDao.insertNewTickerRates(rates);
    }
}
