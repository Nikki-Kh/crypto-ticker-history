package com.nikh.cth.service;

import com.nikh.cth.bean.request.TickerRateIntervalRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;

import java.util.List;

public interface TickerRateHistoryService {

    List<TickerRate> getLastTickerRates(Integer brkId);
    List<TickerRate> getTickerHistory(Integer brkId);
    List<TickerRateIntervalData> getIntervalData(TickerRateIntervalRequest request);

}
