package com.nikh.cth.unit.service.impl;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.utils.SortOrder;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.service.impl.TickerRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.time.temporal.ChronoUnit.MINUTES;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TickerRateServiceImpl.class, TickerRateHistoryDao.class})
class TickerRateServiceImplTest {

    @Autowired
    TickerRateServiceImpl tickerRateService;

    @MockBean
    TickerRateHistoryDao tickerRateHistoryDao;

    @Test
    void testGetLastTickerRates() {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("t1").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(2).tickerName("t2").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr2_1 = TickerRate.builder().brkId(2).tickerName("t3").value(1.3f).createdWhen(now.minus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        var lastRates = List.of(tr1, tr2, tr2_1);

        when(tickerRateHistoryDao.getLastTickerRates(null)).thenReturn(lastRates);
        var result1 = tickerRateService.getLastTickerRates(null);
        assertEquals(result1.size(), 2);
        assertTrue(result1.containsKey(1) && result1.containsKey(2));
        assertTrue(result1.get(1).size() == 1 && result1.get(2).containsAll(List.of(tr2, tr2_1)));

        var tr3 = TickerRate.builder().brkId(3).tickerName("t2").value(1.3f).createdWhen(now.minus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        when(tickerRateHistoryDao.getLastTickerRates(eq(3))).thenReturn(List.of(tr3));
        var result2 = tickerRateService.getLastTickerRates(3);
        assertEquals(1, result2.size());
        assertTrue(result2.containsKey(3));
        assertTrue(result2.get(3).contains(tr3));
    }

    @Test
    void getTickerHistory() {

        var now = LocalDateTime.now();
        var tr2 = TickerRate.builder().brkId(2).tickerName("t2").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(2).tickerName("t2").value(1.3f).createdWhen(now.minus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        var tickerHistory = List.of(tr2, tr3);

        var req = new TickerRateRequest();
        req.setBrkId(2);
        req.setTickerName("t2");
        req.setStartDate(now.minus(2, ChronoUnit.MINUTES));
        req.setEndDate(now.plus(2, ChronoUnit.MINUTES));

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.DESC))).thenReturn(tickerHistory);
        var result = tickerRateService.getTickerHistory(req);
        assertEquals(result.size(), 2);
        assertEquals(tr2, result.get(0));
        assertEquals(tr3, result.get(1));

    }

    @Test
    void testGetIntervalDataFailsWithApiException() {

        var now = LocalDateTime.now();
        var tr2 = TickerRate.builder().brkId(2).tickerName("t2").value(1.0f).createdWhen(now).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(2).tickerName("t2").value(1.3f).createdWhen(now.minus(1, ChronoUnit.MINUTES)).updWhen(now).build();
        var tickerHistory = List.of(tr2, tr3);

        var req = new TickerRateRequest();
        req.setBrkId(2);
        req.setTickerName("t2");
        req.setStartDate(now.minus(2, ChronoUnit.MINUTES));
        req.setEndDate(now.plus(2, ChronoUnit.MINUTES));
        req.setIntervalPeriod("3d");

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.DESC))).thenReturn(tickerHistory);
        assertThrows(ApiException.class, () -> tickerRateService.getIntervalData(req));
    }

    @Test
    void testIntervalData() throws ApiException {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("t1").value(1.19f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("t1").value(1.03f).createdWhen(now.minus(1, MINUTES)).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(1).tickerName("t1").value(1.34f).createdWhen(now.minus(2, MINUTES)).updWhen(now).build();
        var tr4 = TickerRate.builder().brkId(1).tickerName("t1").value(1.86f).createdWhen(now.minus(3, MINUTES)).updWhen(now).build();
        var tr5 = TickerRate.builder().brkId(1).tickerName("t1").value(1.7f).createdWhen(now.minus(4, MINUTES)).updWhen(now).build();
        var tr6 = TickerRate.builder().brkId(1).tickerName("t1").value(1.94f).createdWhen(now.minus(5, MINUTES)).updWhen(now).build();
        var tr7 = TickerRate.builder().brkId(1).tickerName("t1").value(1.63f).createdWhen(now.minus(6, MINUTES)).updWhen(now).build();
        var tr8 = TickerRate.builder().brkId(1).tickerName("t1").value(1.55f).createdWhen(now.minus(7, MINUTES)).updWhen(now).build();
        var tr9 = TickerRate.builder().brkId(1).tickerName("t1").value(1.71f).createdWhen(now.minus(8, MINUTES)).updWhen(now).build();
        var tr10 = TickerRate.builder().brkId(1).tickerName("t1").value(1.15f).createdWhen(now.minus(9, MINUTES)).updWhen(now).build();
        var tr11 = TickerRate.builder().brkId(1).tickerName("t1").value(1.22f).createdWhen(now.minus(10, MINUTES)).updWhen(now).build();
        var tickerHistory = List.of(tr11, tr10, tr9, tr8, tr7, tr6, tr5, tr4, tr3, tr2, tr1);

        var req = new TickerRateRequest();
        req.setBrkId(2);
        req.setTickerName("t2");
        req.setStartDate(now.minus(11, ChronoUnit.MINUTES));
        req.setEndDate(now.plus(1, ChronoUnit.MINUTES));
        req.setIntervalPeriod("3m");

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.ASC))).thenReturn(tickerHistory);
        var result = tickerRateService.getIntervalData(req);
        assertEquals(4, result.size());
        assertEquals(List.of(1.15f, 1.55f, 1.34f, 1.03f),
                result.stream().map(TickerRateIntervalData::getMinRate).collect(Collectors.toList()));
        assertEquals(List.of(1.71f, 1.94f, 1.86f, 1.19f),
                result.stream().map(TickerRateIntervalData::getMaxRate).collect(Collectors.toList()));
    }
}