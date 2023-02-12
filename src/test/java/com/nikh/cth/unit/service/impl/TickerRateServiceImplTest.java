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

import static java.time.temporal.ChronoUnit.SECONDS;
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
    void testIntervalDataSimpleCase() throws ApiException {
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
        req.setBrkId(1);
        req.setTickerName("t1");
        req.setStartDate(now.minus(10, ChronoUnit.MINUTES));
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

    @Test
    void testIntervalDataInnerDataIsMissing() throws ApiException {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("t1").value(1.19f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("t1").value(1.03f).createdWhen(now.minus(1, MINUTES)).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(1).tickerName("t1").value(1.34f).createdWhen(now.minus(2, MINUTES)).updWhen(now).build();
        var tr9 = TickerRate.builder().brkId(1).tickerName("t1").value(1.71f).createdWhen(now.minus(8, MINUTES)).updWhen(now).build();
        var tr10 = TickerRate.builder().brkId(1).tickerName("t1").value(1.15f).createdWhen(now.minus(9, MINUTES)).updWhen(now).build();
        var tr11 = TickerRate.builder().brkId(1).tickerName("t1").value(1.22f).createdWhen(now.minus(10, MINUTES)).updWhen(now).build();
        var tickerHistory = List.of(tr11, tr10, tr9, tr3, tr2, tr1);

        var req = new TickerRateRequest();
        req.setBrkId(1);
        req.setTickerName("t1");
        req.setStartDate(now.minus(10, ChronoUnit.MINUTES));
        req.setEndDate(now.plus(1, ChronoUnit.MINUTES));
        req.setIntervalPeriod("3m");

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.ASC))).thenReturn(tickerHistory);
        var result = tickerRateService.getIntervalData(req);
        assertEquals(4, result.size());
        assertNull(result.get(1).getMinRate());
        assertNull(result.get(1).getMaxRate());
        assertNull(result.get(1).getAvgRate());
        assertEquals( "No data for this interval", result.get(1).getDetails());

        assertEquals(1.34f, result.get(2).getMinRate());
        assertEquals(1.34f, result.get(2).getMaxRate());
        assertEquals(1.34f, result.get(2).getAvgRate());
        assertEquals(1.03f, result.get(3).getMinRate());
        assertEquals(1.19f, result.get(3).getMaxRate());
        assertEquals(1.11f, result.get(3).getAvgRate());
    }


    @Test
    void testIntervalDataBigIntervalAndDataIsMissing() throws ApiException {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("t1").value(1.19f).createdWhen(now).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("t1").value(1.03f).createdWhen(now.minus(1, MINUTES)).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(1).tickerName("t1").value(1.34f).createdWhen(now.minus(2, MINUTES)).updWhen(now).build();
        var tr9 = TickerRate.builder().brkId(1).tickerName("t1").value(1.71f).createdWhen(now.minus(8, MINUTES)).updWhen(now).build();
        var tr10 = TickerRate.builder().brkId(1).tickerName("t1").value(1.15f).createdWhen(now.minus(9, MINUTES)).updWhen(now).build();
        var tr11 = TickerRate.builder().brkId(1).tickerName("t1").value(1.22f).createdWhen(now.minus(10, MINUTES)).updWhen(now).build();
        var tickerHistory = List.of(tr11, tr10, tr9, tr3, tr2, tr1);

        var req = new TickerRateRequest();
        req.setBrkId(1);
        req.setTickerName("t1");
        req.setStartDate(now.minus(13, ChronoUnit.MINUTES));
        req.setEndDate(now.plus(6, ChronoUnit.MINUTES));
        req.setIntervalPeriod("3m");

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.ASC))).thenReturn(tickerHistory);
        var result = tickerRateService.getIntervalData(req);
        assertEquals(7, result.size());
        assertNull(result.get(0).getMinRate());
        assertNull(result.get(0).getMaxRate());
        assertNull(result.get(0).getAvgRate());
        assertEquals( "No data for this interval", result.get(0).getDetails());

        assertNull(result.get(2).getMinRate());
        assertNull(result.get(2).getMaxRate());
        assertNull(result.get(2).getAvgRate());
        assertEquals( "No data for this interval", result.get(2).getDetails());

        assertNull(result.get(5).getMinRate());
        assertNull(result.get(5).getMaxRate());
        assertNull(result.get(5).getAvgRate());
        assertEquals( "No data for this interval", result.get(0).getDetails());

        assertNull(result.get(6).getMinRate());
        assertNull(result.get(6).getMaxRate());
        assertNull(result.get(6).getAvgRate());
        assertEquals( "No data for this interval", result.get(6).getDetails());
    }

    @Test
    void testIntervalDataComplexTest() throws ApiException {
        var now = LocalDateTime.now();
        var tr1 = TickerRate.builder().brkId(1).tickerName("t1").value(1.83f).createdWhen(now.minus(8, SECONDS)).updWhen(now).build();
        var tr2 = TickerRate.builder().brkId(1).tickerName("t1").value(4.37f).createdWhen(now.minus(19, SECONDS)).updWhen(now).build();
        var tr3 = TickerRate.builder().brkId(1).tickerName("t1").value(2.02f).createdWhen(now.minus(22, SECONDS)).updWhen(now).build();
        var tr4 = TickerRate.builder().brkId(1).tickerName("t1").value(3.57f).createdWhen(now.minus(25, SECONDS)).updWhen(now).build();
        var tr5 = TickerRate.builder().brkId(1).tickerName("t1").value(4.93f).createdWhen(now.minus(29, SECONDS)).updWhen(now).build();
        var tr6 = TickerRate.builder().brkId(1).tickerName("t1").value(1.9f).createdWhen(now.minus(38, SECONDS)).updWhen(now).build();
        var tr7 = TickerRate.builder().brkId(1).tickerName("t1").value(2.65f).createdWhen(now.minus(41, SECONDS)).updWhen(now).build();
        var tr8 = TickerRate.builder().brkId(1).tickerName("t1").value(5.33f).createdWhen(now.minus(44, SECONDS)).updWhen(now).build();
        var tr9 = TickerRate.builder().brkId(1).tickerName("t1").value(4.8f).createdWhen(now.minus(46, SECONDS)).updWhen(now).build();
        var tr10 = TickerRate.builder().brkId(1).tickerName("t1").value(2.33f).createdWhen(now.minus(60, SECONDS)).updWhen(now).build();
        var tr11 = TickerRate.builder().brkId(1).tickerName("t1").value(3.23f).createdWhen(now.minus(69, SECONDS)).updWhen(now).build();
        var tr12 = TickerRate.builder().brkId(1).tickerName("t1").value(4.73f).createdWhen(now.minus(71, SECONDS)).updWhen(now).build();
        var tr13 = TickerRate.builder().brkId(1).tickerName("t1").value(1.37f).createdWhen(now.minus(77, SECONDS)).updWhen(now).build();
        var tr14 = TickerRate.builder().brkId(1).tickerName("t1").value(1.92f).createdWhen(now.minus(88, SECONDS)).updWhen(now).build();
        var tr15 = TickerRate.builder().brkId(1).tickerName("t1").value(4.48f).createdWhen(now.minus(96, SECONDS)).updWhen(now).build();
        var tr16 = TickerRate.builder().brkId(1).tickerName("t1").value(2.17f).createdWhen(now.minus(105, SECONDS)).updWhen(now).build();

        var tickerHistory = List.of(tr16, tr15, tr14, tr13, tr12, tr11, tr10, tr9,
                                                    tr8, tr7, tr6, tr5, tr4, tr3, tr2, tr1);

        var req = new TickerRateRequest();
        req.setBrkId(1);
        req.setTickerName("t1");
        req.setStartDate(now.minus(107, ChronoUnit.SECONDS));
        req.setEndDate(now);
        req.setIntervalPeriod("10s");

        when(tickerRateHistoryDao.getTickerHistory(eq(req), eq(SortOrder.ASC))).thenReturn(tickerHistory);

        var result = tickerRateService.getIntervalData(req);
        assertEquals(11, result.size());

        assertNull(result.get(2).getMinRate());
        assertNull(result.get(2).getMaxRate());
        assertNull(result.get(2).getAvgRate());
        assertEquals( "No data for this interval", result.get(2).getDetails());

        assertNull(result.get(5).getMinRate());
        assertNull(result.get(5).getMaxRate());
        assertNull(result.get(5).getAvgRate());
        assertEquals( "No data for this interval", result.get(5).getDetails());

        assertNull(result.get(10).getMinRate());
        assertNull(result.get(10).getMaxRate());
        assertNull(result.get(10).getAvgRate());
        assertEquals( "No data for this interval", result.get(10).getDetails());


        assertEquals(2.17f, result.get(0).getMinRate());
        assertEquals(2.17f, result.get(0).getMaxRate());
        assertEquals(2.17f, result.get(0).getAvgRate());

        assertEquals(1.92f, result.get(1).getMinRate());
        assertEquals(4.48f, result.get(1).getMaxRate());
        assertEquals(3.2f, result.get(1).getAvgRate());

        assertEquals(1.37f, result.get(3).getMinRate());
        assertEquals(4.73f, result.get(3).getMaxRate());
        assertEquals(3.11f,  result.get(3).getAvgRate());

        assertEquals(2.33f, result.get(4).getMinRate());
        assertEquals(2.33f, result.get(4).getMaxRate());
        assertEquals(2.33f, result.get(4).getAvgRate());

        assertEquals(1.9f, result.get(6).getMinRate());
        assertEquals(5.33f, result.get(6).getMaxRate());
        assertEquals(3.67f, result.get(6).getAvgRate());

        assertEquals(4.93f, result.get(7).getMinRate());
        assertEquals(4.93f, result.get(7).getMaxRate());
        assertEquals(4.93f, result.get(7).getAvgRate());

        assertEquals(2.02f, result.get(8).getMinRate());
        assertEquals(4.37f, result.get(8).getMaxRate());
        assertEquals(3.32f, result.get(8).getAvgRate());

        assertEquals(1.83f, result.get(9).getMinRate());
        assertEquals(1.83f, result.get(9).getMaxRate());
        assertEquals(1.83f, result.get(9).getAvgRate());




    }


}