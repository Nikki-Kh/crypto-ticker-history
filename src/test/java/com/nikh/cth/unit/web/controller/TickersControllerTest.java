package com.nikh.cth.unit.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.service.impl.TickerRateServiceImpl;
import com.nikh.cth.web.advice.ExceptionHandlerAdvice;
import com.nikh.cth.web.controller.TickersController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TickersController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TickerRateServiceImpl.class, TickersController.class, ExceptionHandlerAdvice.class})
class TickersControllerTest {

    @MockBean
    TickerRateServiceImpl tickerRateService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getLastTickerRatesWithBrokerId() throws Exception {

        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("tr1").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("tr2").value(1.3f).createdWhen(now).updWhen(now).build();
        var tickerRates = List.of(tr1, tr2);

        when(tickerRateService.getLastTickerRates(eq(1))).thenReturn(tickerRates);

        mockMvc.perform(get("/tickers/rates?brkId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(tickerRates)));
    }
    @Test
    void getLastTickerRatesWithoutBrokerId() throws Exception {

        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("tr1").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("tr2").value(1.3f).createdWhen(now).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(2).tickerName("tr3").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr4 = TickerRate.builder().brkId(2).tickerName("tr4").value(1.3f).createdWhen(now).updWhen(now).build();
        var tickerRates = List.of(tr1, tr2, tr3, tr4);

        when(tickerRateService.getLastTickerRates(null)).thenReturn(tickerRates);

        mockMvc.perform(get("/tickers/rates"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(tickerRates)));
    }

    @Test
    void getTickerRateHistory() throws Exception {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("tr1").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("tr1").value(1.3f).createdWhen(now.plus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(2).tickerName("tr2").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr4 = TickerRate.builder().brkId(2).tickerName("tr2").value(1.3f).createdWhen(now.plus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        var tickerRates1 = List.of(tr2, tr1);
        var tickerRates2 = List.of(tr4, tr3);

        var req1 = new TickerRateRequest();
        req1.setBrkId(1);
        req1.setTickerName("tr1");
        req1.setStartDate(now.minus(1,ChronoUnit.MINUTES));
        req1.setEndDate(now.plus(2,ChronoUnit.MINUTES));

        var req2 = new TickerRateRequest();
        req1.setBrkId(2);
        req1.setTickerName("tr2");
        req1.setStartDate(now.minus(1,ChronoUnit.MINUTES));
        req1.setEndDate(now.plus(2,ChronoUnit.MINUTES));

        when(tickerRateService.getTickerHistory(eq(req1))).thenReturn(tickerRates1);
        when(tickerRateService.getTickerHistory(eq(req2))).thenReturn(tickerRates2);

        mockMvc.perform(get("/tickers/rates/history")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req1)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(tickerRates1)));

        mockMvc.perform(get("/tickers/rates/history")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req2)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(tickerRates2)));

    }

    @Test
    void getTickerRateIntervalData() throws ApiException, Exception {

        var now = LocalDateTime.now();

        var req1 = new TickerRateRequest();
        req1.setBrkId(1);
        req1.setTickerName("tr1");
        req1.setStartDate(now.minus(1,ChronoUnit.HOURS));
        req1.setEndDate(now.plus(2,ChronoUnit.HOURS));
        req1.setIntervalPeriod("1h");

        var trid1 = TickerRateIntervalData.builder()
                .minRate(1f).maxRate(1f).avgRate(1f).startDate(now.minus(1,ChronoUnit.HOURS)).build();
        var trid2 = TickerRateIntervalData.builder()
                .minRate(1.5f).maxRate(1.9f).avgRate(1.8f).startDate(now).build();
        var trid3 = TickerRateIntervalData.builder()
                .minRate(1.9f).maxRate(2.4f).avgRate(2.15f).startDate(now.plus(1,ChronoUnit.HOURS)).build();

        var intervalDataList = List.of(trid1, trid2, trid3);

        when(tickerRateService.getIntervalData(eq(req1))).thenReturn(intervalDataList);
        mockMvc.perform(get("/tickers/rates/interval")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req1)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(intervalDataList)));

    }


}