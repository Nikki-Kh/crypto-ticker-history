package com.nikh.cth.unit.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.service.impl.BrokerServiceImpl;
import com.nikh.cth.web.controller.BrokersController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BrokersController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {BrokerServiceImpl.class, BrokersController.class})
class BrokersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BrokerServiceImpl brokerService;

    ObjectMapper mapper = new ObjectMapper();

    private Broker broker;

    @BeforeAll
    void beforeAll() {
        mapper.registerModule(new JavaTimeModule());
        broker = Broker.builder().brkId(1).brkName("Kraken").apiAddr("https://api.kraken.com").updInterval(100).build();
    }



    @Test
    void getBrokers() throws Exception {

        List<Broker> brokers = new ArrayList<>();
        brokers.add(broker);

        when(brokerService.getBrokers()).thenReturn(brokers);
        mockMvc.perform(get("/brokers"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(brokers)));
    }

    @Test
    void getBrokerTickers() throws Exception {
        List<String> tickers = List.of("TICKER-1", "TICKER-2");
        when(brokerService.getBrokerTickers(1)).thenReturn(tickers);

        mockMvc.perform(get("/brokers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(tickers)));
    }
}