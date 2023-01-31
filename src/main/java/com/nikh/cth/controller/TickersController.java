package com.nikh.cth.controller;

import com.nikh.cth.bean.request.TickerRateRequest;
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
    ResponseEntity<?> getTickerRateHistory(@RequestParam("brkId") Integer brkId){
        return ResponseEntity.ok(tickerRateService.getLastTickerRates(brkId));
    }

    @GetMapping("/interval")
    ResponseEntity<?> getTickerRateIntervalData(@RequestBody TickerRateRequest request){
        return ResponseEntity.ok(tickerRateService.getIntervalData(request));
    }
}
