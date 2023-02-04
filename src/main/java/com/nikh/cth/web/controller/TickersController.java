package com.nikh.cth.web.controller;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.error.ExceptionCode;
import com.nikh.cth.service.TickerRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickers/rates")
public class TickersController {

    @Autowired
    TickerRateService tickerRateService;

    @GetMapping
    ResponseEntity<?> getLastTickerRates(@RequestParam("brkId") Integer brkId){
        return ResponseEntity.ok(tickerRateService.getLastTickerRates(brkId));
    }

    @GetMapping("/history")
    ResponseEntity<?> getTickerRateHistory(@RequestBody TickerRateRequest request){
        return ResponseEntity.ok(tickerRateService.getTickerHistory(request));
    }

    @GetMapping("/interval")
    ResponseEntity<?> getTickerRateIntervalData(@RequestBody TickerRateRequest request) throws ApiException {
        if (request.getIntervalPeriod() == null) {
            throw new ApiException("Invalid request", ExceptionCode.INVALID_REQUEST);
        }
        return ResponseEntity.ok(tickerRateService.getIntervalData(request));
    }
}
