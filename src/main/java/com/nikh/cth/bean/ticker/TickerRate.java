package com.nikh.cth.bean.ticker;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class TickerRate {

    @JsonIgnore
    Integer id;
    Integer brkId;
    String tickerName;
    Float value;
    LocalDateTime createdWhen;
    @JsonIgnore
    LocalDateTime updWhen;

}
