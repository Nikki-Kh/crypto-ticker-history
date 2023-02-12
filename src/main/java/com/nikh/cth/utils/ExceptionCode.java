package com.nikh.cth.utils;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = ExceptionCode.class)
public @interface ExceptionCode {

     int INVALID_REQUEST = 1;
     int HTTP_CALL_FAILED = 2;
     int TOKEN_EXPIRED = 3;
     int WRONG_TOKEN_FORMAT = 4;
     int UNAUTHORIZED = 5;
     int UNKNOWN = 0;


}
