package com.nikh.cth.bean.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerToTickerDBEntry {

    private Integer brkId;
    private String tickerArrayStr;

}
