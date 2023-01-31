package com.nikh.cth.bean.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TickerRateRequest {

    Integer brkId;
    String tickerName;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String intervalPeriod;

}
