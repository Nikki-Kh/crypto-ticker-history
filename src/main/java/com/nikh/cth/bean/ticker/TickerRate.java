package com.nikh.cth.bean.ticker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
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
