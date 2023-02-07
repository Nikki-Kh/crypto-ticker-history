package com.nikh.cth.scheduler;

import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.web.response.KrakenTickerRateResponse;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ApiException;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Builder
@Slf4j
public final class KucoinTickerRateUpdateTask extends TickerRateUpdateTask{

    private final String urlSuffix = "?";

    KucoinTickerRateUpdateTask(Integer brkId, String brokerName, Integer updateInterval, String apiAddr, String apiKey, WebClient webClient, List<String> tickers, TickerRateHistoryDao tickerRateHistoryDao) {
        super(brkId, brokerName, updateInterval, apiAddr, apiKey, tickers, webClient, tickerRateHistoryDao);
    }

    @SneakyThrows
    @Override
    public void run() {
        var result = getLatestTickerRates();
        uploadDataToDatabase(result);
    }

    @Override
    protected List<TickerRate> getLatestTickerRates() {
        try {
            var result = webClient.get().uri(apiAddr + urlSuffix + StringUtils.joinWith(",", tickers))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(KrakenTickerRateResponse.class)
                    .toFuture().get();
            if (!result.hasBody() && !CollectionUtils.isEmpty(result.getBody().getErrors())) {
                throw new ApiException(result.hasBody() ?
                        StringUtils.joinWith(";", result.getBody().getErrors()) : "Empty response", 2);
            }

        }
        catch (ExecutionException | InterruptedException | ApiException  e) {
            log.error("Failed to get response from 3rd party call. Broker: {}", brokerName, e);
            return Collections.EMPTY_LIST;
        }

        //TODO: cast response and call function
        return Collections.EMPTY_LIST;
    }

    @Override
    protected void uploadDataToDatabase(List<TickerRate> tickerRates) {

    }
}
