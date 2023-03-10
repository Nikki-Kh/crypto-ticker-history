package com.nikh.cth.service;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.error.ApiException;

import java.util.List;
import java.util.Map;

public interface TickerRateService {

    Map<Integer, List<TickerRate>> getLastTickerRates(Integer brkId);

    List<TickerRate> getTickerHistory(TickerRateRequest request);

    List<TickerRateIntervalData> getIntervalData(TickerRateRequest request) throws ApiException;

}
