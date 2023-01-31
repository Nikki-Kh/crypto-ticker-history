package com.nikh.cth.service;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;

import java.util.List;

public interface TickerRateService {

    List<TickerRate> getLastTickerRates(Integer brkId);
    List<TickerRate> getTickerHistory(TickerRateRequest request);
    List<TickerRateIntervalData> getIntervalData(TickerRateRequest request);

}
