package com.nikh.cth.unit.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikh.cth.bean.request.TickerRateRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TickersController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TickerRateServiceImpl.class, TickersController.class, ExceptionHandlerAdvice.class})
class ExceptionHandlerAdviceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TickerRateServiceImpl tickerRateService;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testApiException() throws Exception {

        var now = LocalDateTime.now();

        var req2 = new TickerRateRequest();
        req2.setBrkId(1);
        req2.setTickerName("tr1");
        req2.setStartDate(now.minus(1, ChronoUnit.HOURS));
        req2.setEndDate(now.plus(2,ChronoUnit.HOURS));

        mockMvc.perform(get("/tickers/rates/interval")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req2)))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof ApiException));
    }

    @Test
    void testDefaultException() throws Exception {
        var now = LocalDateTime.now();

        var req2 = new TickerRateRequest();
        req2.setBrkId(1);
        req2.setTickerName("tr1");
        req2.setStartDate(now.minus(1, ChronoUnit.HOURS));
        req2.setEndDate(now.plus(2,ChronoUnit.HOURS));
        req2.setIntervalPeriod("1h");

        when(tickerRateService.getIntervalData(any())).thenThrow(new IllegalArgumentException("Some message"));

        mockMvc.perform(get("/tickers/rates/interval")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req2)))
                .andExpect(status().isBadGateway())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IllegalArgumentException));
    }
}