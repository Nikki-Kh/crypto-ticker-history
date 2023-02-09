package com.nikh.cth.bean.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TickerRateRequest {
    @JsonProperty(required = true)
    Integer brkId;
    @JsonProperty(required = true)
    String tickerName;
    @JsonProperty(required = true)
    LocalDateTime startDate;
    @JsonProperty(required = true)
    LocalDateTime endDate;
    String intervalPeriod;

}
