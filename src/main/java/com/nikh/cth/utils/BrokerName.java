package com.nikh.cth.utils;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = BrokerName.class)
public @interface BrokerName {
    String KRAKEN = "Kraken";
    String KUCOIN = "Kucoin";
}
