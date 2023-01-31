package com.nikh.cth.service.impl;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.cache.TickerRateCache;
import com.nikh.cth.dao.TickerHistoryDao;
import com.nikh.cth.service.TickerRateService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TickerRateServiceImpl implements TickerRateService {

    @Autowired
    TickerRateCache tickerRateCache;
    @Autowired
    TickerHistoryDao tickerHistoryDao;


    @Override
    public List<TickerRate> getLastTickerRates(Integer brkId) {
        return brkId != null ? tickerRateCache.getLatestRatesByBroker(brkId) : tickerRateCache.getAllLatestRates();
    }

    @Override
    public List<TickerRate> getTickerHistory(TickerRateRequest request) {
        return tickerHistoryDao.getTickerHistory(request);
    }

    @Override
    public List<TickerRateIntervalData> getIntervalData(TickerRateRequest request) {
        var tickerHistory = tickerHistoryDao.getTickerHistory(request);
        if (tickerHistory.isEmpty()) {
            return Collections.emptyList();
        }
        var chronoPair = parseIntervalExp(request.getIntervalPeriod());
        return getIntervalData(tickerHistory, chronoPair);
    }

    private Pair<Integer, ChronoUnit> parseIntervalExp( String exp) {
        var period = Integer.parseInt(exp.substring(0, exp.length() - 1));
        char chronoUnitChar = exp.charAt(exp.length() - 1);
        ChronoUnit chronoUnit = switch (chronoUnitChar) {
            case 's' -> ChronoUnit.SECONDS;
            case 'm' -> ChronoUnit.MINUTES;
            case 'h' -> ChronoUnit.HOURS;
            default -> throw new RuntimeException("meh");
        };
        return Pair.of(period, chronoUnit);
    }

    private List<TickerRateIntervalData> getIntervalData(List<TickerRate> tickerHistory, Pair<Integer, ChronoUnit> chronoPair) {
        var result = new ArrayList<TickerRateIntervalData>();
        TickerRateIntervalData tickerRateIntervalData;
        var thIterator = tickerHistory.iterator();
        TickerRate tickerRate = thIterator.next();
        Float minRate = tickerRate.getValue();
        Float maxRate = tickerRate.getValue();
        Float avgRate = tickerRate.getValue();
        LocalDateTime intStart = tickerRate.getCreatedWhen();
        LocalDateTime intEnd = intStart.plus(chronoPair.getLeft(), chronoPair.getRight());
        int count = 1;
        while(thIterator.hasNext()) {
            tickerRate = thIterator.next();
            if (tickerRate.getCreatedWhen().isBefore(intEnd)) {
                minRate = Float.min(minRate, tickerRate.getValue());
                maxRate = Float.max(maxRate, tickerRate.getValue());
                avgRate += tickerRate.getValue();
                count++;
            } else {
                tickerRateIntervalData = TickerRateIntervalData.builder()
                        .minRate(minRate)
                        .maxRate(maxRate)
                        .avgRate(avgRate / count)
                        .startDate(intStart)
                        .build();
                result.add(tickerRateIntervalData);
                minRate = tickerRate.getValue();
                maxRate = tickerRate.getValue();
                avgRate = tickerRate.getValue();
                intStart = intEnd;
                intEnd = intStart.plus(chronoPair.getLeft(), chronoPair.getRight());
                count = 1;
            }
            tickerRateIntervalData = TickerRateIntervalData.builder()
                    .minRate(minRate)
                    .maxRate(maxRate)
                    .avgRate(avgRate / count)
                    .startDate(intStart)
                    .build();
            result.add(tickerRateIntervalData);
        }
        return result;
    }
}
