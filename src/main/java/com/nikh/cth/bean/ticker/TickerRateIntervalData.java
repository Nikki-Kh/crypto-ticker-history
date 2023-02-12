package com.nikh.cth.bean.ticker;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TickerRateIntervalData {

    LocalDateTime startDate;
    Float minRate;
    Float maxRate;
    Float avgRate;
    String details;
}
