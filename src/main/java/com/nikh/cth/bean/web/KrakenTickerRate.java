package com.nikh.cth.bean.web;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KrakenTickerRate extends AbstractWebTickerRate {
    List<String> a;
    List<String> b;
    String o;

    @Override
    Float getValue() {
        return Float.parseFloat(a.get(0));
    }
}
