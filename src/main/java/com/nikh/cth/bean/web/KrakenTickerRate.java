package com.nikh.cth.bean.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KrakenTickerRate {
    List<String> a;
    List<String> b;
    List<String> c;
    List<String> v;
    List<String> p;
    List<Integer> t;
    List<String> l;
    List<String> h;
    String o;


    public Float getValue() {
        return Float.parseFloat(a.get(0));
    }
}
