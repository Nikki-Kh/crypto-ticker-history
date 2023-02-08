package com.nikh.cth.bean.web;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KucoinTickerRate {

    private String  symbol;
    private String  symbolName;
    private Float  buy;
    private Float  sell;
    private Float  changeRate;
    private Float  changePrice;
    private Float  high;
    private Float  low;
    private Float  vol;
    private Float  volValue;
    private Float  last;
    private Float  averagePrice;
    private Float  takerFeeRate;
    private Float  makerFeeRate;
    private Float  takerCoefficient;
    private Float  makerCoefficient;

    public Float getValue() {
        return last;
    }
}
