package com.nikh.cth.error;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = ExceptionCode.class)
public @interface ExceptionCode {

     int INVALID_REQUEST = 1;

}