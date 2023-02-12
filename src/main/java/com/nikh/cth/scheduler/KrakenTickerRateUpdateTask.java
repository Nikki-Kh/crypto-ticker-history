package com.nikh.cth.scheduler;

import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.web.response.KrakenTickerRateResponse;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.utils.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public final class KrakenTickerRateUpdateTask extends TickerRateUpdateTask{

    private final String urlSuffix = "/0/public/Ticker?pair=";

    KrakenTickerRateUpdateTask(Integer brkId, String brokerName, Integer updateInterval, String apiAddr, String apiKey, WebClient webClient, List<String> tickers, TickerRateHistoryDao tickerRateHistoryDao) {
        super(brkId, brokerName, updateInterval, apiAddr, apiKey, tickers, webClient, tickerRateHistoryDao);
    }

    @Override
    protected List<TickerRate> getLatestTickerRates(){
        try {
            var url = apiAddr.concat(urlSuffix).concat(StringUtils.joinWith(",", tickers.toArray()));
            var result = webClient.get().uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(KrakenTickerRateResponse.class)
                    .toFuture().get();
            if (!result.hasBody() || !CollectionUtils.isEmpty(Objects.requireNonNull(result.getBody()).getErrors())) {
                throw new ApiException(result.hasBody() ?
                        StringUtils.joinWith(";", Objects.requireNonNull(result.getBody()).getErrors()) : "Empty response", ExceptionCode.HTTP_CALL_FAILED);
            }
            if (MapUtils.isEmpty(result.getBody().getResult())) {
                log.warn("Broker update job retrieved empty result.\n Broker: {};\n Url: {}", brokerName, url);
            }
            return result.getBody().getResult().entrySet().stream()
                    .map(entry -> TickerRate.builder()
                                    .brkId(brkId)
                                    .tickerName(entry.getKey())
                                    .value(entry.getValue().getValue())
                                    .build())
                    .collect(Collectors.toList());
        }
        catch (ExecutionException | InterruptedException | ApiException  e) {
            log.error("Failed to get response from 3rd party call. Broker: {}", brokerName, e);
            return Collections.EMPTY_LIST;
        }
    }

}
