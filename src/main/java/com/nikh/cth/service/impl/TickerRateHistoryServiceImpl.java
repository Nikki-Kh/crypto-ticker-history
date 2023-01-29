package com.nikh.cth.service.impl;

import com.nikh.cth.bean.request.TickerRateIntervalRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.service.TickerRateHistoryService;

import java.util.List;

public class TickerRateHistoryServiceImpl implements TickerRateHistoryService {
    @Override
    public List<TickerRate> getLastTickerRates(Integer brkId) {
        return null;
    }

    @Override
    public List<TickerRate> getTickerHistory(Integer brkId) {
        return null;
    }

    @Override
    public List<TickerRateIntervalData> getIntervalData(TickerRateIntervalRequest request) {
        return null;
    }
}
