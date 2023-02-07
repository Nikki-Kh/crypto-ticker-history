package com.nikh.cth.bean.web;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KucoinTickerRate extends AbstractWebTickerRate {

    private String val;

    @Override
    Float getValue() {
        return Float.parseFloat(val);
    }
}
