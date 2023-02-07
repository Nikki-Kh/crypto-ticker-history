package com.nikh.cth.bean.web.response;

import com.nikh.cth.bean.web.KucoinTickerRate;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KucoinTickerRateResponse {

    Long timestamp;
    List<KucoinTickerRate> ticker;
}
