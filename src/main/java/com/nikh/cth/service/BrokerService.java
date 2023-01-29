package com.nikh.cth.service;

import com.nikh.cth.bean.broker.Broker;

import java.util.List;

public interface BrokerService {

    List<Broker> getBrokers();

    List<String> getBrokerTickers(Integer brkId);

}
