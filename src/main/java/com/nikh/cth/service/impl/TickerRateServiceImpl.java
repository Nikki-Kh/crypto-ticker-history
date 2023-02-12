package com.nikh.cth.service.impl;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.utils.SortOrder;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.utils.ExceptionCode;
import com.nikh.cth.service.TickerRateService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TickerRateServiceImpl implements TickerRateService {

    @Autowired
    TickerRateHistoryDao tickerRateHistoryDao;


    @Override
    public Map<Integer, List<TickerRate>> getLastTickerRates(Integer brkId) {
        var rates =  tickerRateHistoryDao.getLastTickerRates(brkId);
        return rates.stream().collect(Collectors.groupingBy(TickerRate::getBrkId));
    }

    @Override
    public List<TickerRate> getTickerHistory(TickerRateRequest request) {
        return tickerRateHistoryDao.getTickerHistory(request, SortOrder.DESC);
    }

    @Override
    public List<TickerRateIntervalData> getIntervalData(TickerRateRequest request) throws ApiException {
        var chronoPair = parseIntervalExp(request.getIntervalPeriod());
        var tickerHistory = tickerRateHistoryDao.getTickerHistory(request, SortOrder.ASC);
        if (tickerHistory.isEmpty()) {
            return Collections.emptyList();
        }
        return getIntervalData(tickerHistory, chronoPair, request.getStartDate(), request.getEndDate());
    }

    private Pair<Integer, ChronoUnit> parseIntervalExp( String exp) throws ApiException {
        var period = Integer.parseInt(exp.substring(0, exp.length() - 1));
        char chronoUnitChar = exp.charAt(exp.length() - 1);
        ChronoUnit chronoUnit = switch (chronoUnitChar) {
            case 's' -> ChronoUnit.SECONDS;
            case 'm' -> ChronoUnit.MINUTES;
            case 'h' -> ChronoUnit.HOURS;
            default -> throw new ApiException("Invalid request", ExceptionCode.INVALID_REQUEST);
        };
        return Pair.of(period, chronoUnit);
    }

    private List<TickerRateIntervalData> getIntervalData(List<TickerRate> tickerHistory,
                                                          Pair<Integer, ChronoUnit> chronoPair,
                                                          LocalDateTime start, LocalDateTime end) {
        var result = new ArrayList<TickerRateIntervalData>();
        var thIterator = tickerHistory.iterator();
        TickerRate tickerRate = thIterator.next();
        Float minRate = null;
        Float maxRate = null;
        Float avgRate = null;
        LocalDateTime intStart = start;
        LocalDateTime intEnd = intStart.plus(chronoPair.getLeft(), chronoPair.getRight());
        int count = 0;
        while (intStart.isBefore(end)){
            if (tickerRate == null) {
                result.add(getIntervalData(minRate, maxRate, avgRate, count, start));
                minRate = maxRate = avgRate = null;
                count = 0;
                intStart = intEnd;
                intEnd = intStart.plus(chronoPair.getLeft(), chronoPair.getRight());
            } else {
                var crWhen = tickerRate.getCreatedWhen();
                if (!crWhen.isBefore(intStart) && crWhen.isBefore(intEnd)){
                    if (minRate == null) {
                        minRate = maxRate = avgRate = tickerRate.getValue();
                    } else {
                        minRate = Float.min(minRate, tickerRate.getValue());
                        maxRate = Float.max(maxRate, tickerRate.getValue());
                        avgRate += tickerRate.getValue();
                    }
                    count++;
                    tickerRate = thIterator.hasNext() ? thIterator.next() : null;
                }
                else {
                    result.add(getIntervalData(minRate, maxRate, avgRate, count, start));
                    minRate = maxRate = avgRate = null;
                    count = 0;
                    intStart = intEnd;
                    intEnd = intStart.plus(chronoPair.getLeft(), chronoPair.getRight());
                }
            }
        }
        return result;
    }


    private TickerRateIntervalData getIntervalData(Float minRate, Float maxRate, Float avgRate,
                                                   int count, LocalDateTime start) {
        return  TickerRateIntervalData.builder()
                .minRate(minRate)
                .maxRate(maxRate)
                .avgRate( count == 0 ? null : avgRate/count)
                .startDate(start)
                .details(count == 0 ? "No data for this interval" : null)
                .build();
    }
}
