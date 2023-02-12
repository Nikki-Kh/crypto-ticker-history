package com.nikh.cth.bean.web.response;

import com.nikh.cth.bean.web.KucoinTickerRate;
import com.nikh.cth.bean.web.KucoinTickerRateResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KucoinTickerRateResponse {

    Long code;
    KucoinTickerRateResponseData data;
}
