package com.nikh.cth.bean.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KucoinTickerRateResponseData {

    Long time;
    List<KucoinTickerRate> ticker;

}
