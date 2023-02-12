package com.nikh.cth.bean.response;

import com.nikh.cth.bean.ticker.TickerRate;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TickerRateResponse {

    Map<String, List<TickerRate>> tickerRates;

}
