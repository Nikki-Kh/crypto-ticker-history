package com.nikh.cth.web.controller;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.utils.ExceptionCode;
import com.nikh.cth.service.TickerRateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/tickers/rates")
public class TickersController {

    @Autowired
    TickerRateService tickerRateService;

    @GetMapping
    ResponseEntity<?> getLastTickerRates(@RequestParam("brkId") Optional<Integer> brkId){
        return ResponseEntity.ok(tickerRateService.getLastTickerRates(brkId.orElse(null)));
    }

    @GetMapping("/history")
    ResponseEntity<?> getTickerRateHistory(@RequestBody TickerRateRequest request) throws ApiException {
        validateTickerRateRequest(request, false);
        return ResponseEntity.ok(tickerRateService.getTickerHistory(request));
    }

    @GetMapping("/interval")
    ResponseEntity<?> getTickerRateIntervalData(@RequestBody TickerRateRequest request) throws ApiException {
        validateTickerRateRequest(request, true);
        return ResponseEntity.ok(tickerRateService.getIntervalData(request));
    }

    private void validateTickerRateRequest(TickerRateRequest request, boolean checkIntervalPeriod) throws ApiException {
        if (request.getBrkId() == null || StringUtils.isEmpty(request.getTickerName())
                || request.getStartDate() == null || request.getEndDate() == null
                || (checkIntervalPeriod && request.getIntervalPeriod() == null)) {
            throw new ApiException("Invalid request", ExceptionCode.INVALID_REQUEST);
        }
    }
}
