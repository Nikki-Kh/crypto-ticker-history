package com.nikh.cth.bean.web.response;

import com.nikh.cth.bean.web.KrakenTickerRate;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class KrakenTickerRateResponse {

    Map<String, KrakenTickerRate> result;
    List<String> errors;

}
