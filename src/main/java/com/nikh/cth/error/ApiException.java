package com.nikh.cth.error;


import com.nikh.cth.utils.ExceptionCode;
import lombok.Getter;

@Getter
public class ApiException extends Exception{

    int exceptionCode;

    public ApiException(String message, @ExceptionCode int exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public ApiException(String message, Throwable cause, @ExceptionCode int exceptionCode) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }
}
