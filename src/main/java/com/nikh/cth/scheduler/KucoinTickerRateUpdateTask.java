package com.nikh.cth.scheduler;

import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.web.response.KucoinTickerRateResponse;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.utils.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public final class KucoinTickerRateUpdateTask extends TickerRateUpdateTask{

    private final String urlSuffix = "/api/v1/market/allTickers";

    KucoinTickerRateUpdateTask(Integer brkId, String brokerName, Integer updateInterval, String apiAddr, String apiKey, WebClient webClient, List<String> tickers, TickerRateHistoryDao tickerRateHistoryDao) {
        super(brkId, brokerName, updateInterval, apiAddr, apiKey, tickers, webClient, tickerRateHistoryDao);
    }

    @Override
    protected List<TickerRate> getLatestTickerRates() {
        try {
            var url = apiAddr.concat(urlSuffix);
            var result = webClient.get().uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(KucoinTickerRateResponse.class)
                    .toFuture().get();
            if (!result.hasBody() ||
                    result.getBody().getData() == null ||
                    CollectionUtils.isEmpty(result.getBody().getData().getTicker())) {
                throw new ApiException("Empty response", ExceptionCode.HTTP_CALL_FAILED);
            }
            if (CollectionUtils.isEmpty(result.getBody().getData().getTicker())) {
                log.warn("Broker update job retrieved empty result.\n Broker: {};\n Url: {}", brokerName, url);
            }
            return result.getBody().getData().getTicker().stream()
                    .filter(it -> tickers.contains(it.getSymbol()))
                    .map(it -> TickerRate.builder()
                                    .brkId(brkId)
                                    .tickerName(it.getSymbol())
                                    .value(it.getValue())
                                    .build()
                    )
                    .collect(Collectors.toList());
        }
        catch (ExecutionException | InterruptedException | ApiException  e) {
            log.error("Failed to get response from 3rd party call. Broker: {}", brokerName, e);
            return Collections.EMPTY_LIST;
        }
    }

}
