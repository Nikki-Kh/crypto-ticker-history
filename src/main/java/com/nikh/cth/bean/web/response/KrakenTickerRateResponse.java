package com.nikh.cth.bean.web.response;

import com.nikh.cth.bean.web.KrakenTickerRate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KrakenTickerRateResponse {

    private Map<String, KrakenTickerRate> result;
    private List<String> errors;

}
