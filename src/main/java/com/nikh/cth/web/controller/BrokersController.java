package com.nikh.cth.web.controller;

import com.nikh.cth.service.BrokerService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brokers")
@RequiredArgsConstructor
public class BrokersController {

    private final BrokerService brokerService;

    @GetMapping
    public ResponseEntity<?> getBrokers() {
        return ResponseEntity.ok(brokerService.getBrokers());
    }

    @GetMapping("/{brokerId}")
    public ResponseEntity<?> getBrokerTickers(@PathVariable("brokerId") @NotNull Integer brkId) {
        return ResponseEntity.ok(brokerService.getBrokerTickers(brkId));
    }


    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }
}
