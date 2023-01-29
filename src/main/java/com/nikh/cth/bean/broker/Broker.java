package com.nikh.cth.bean.broker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Broker {

    Integer brkId;
    String brkName;
    @JsonIgnore
    String apiAddr;
    @JsonIgnore
    String apiKey;
    Integer updInterval;
    LocalDateTime createdWhen;
    LocalDateTime updatedWhen;
}
