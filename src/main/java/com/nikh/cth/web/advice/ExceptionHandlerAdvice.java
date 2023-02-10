package com.nikh.cth.web.advice;

import com.nikh.cth.error.ApiException;
import com.nikh.cth.error.ErrorBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ApiException.class })
    protected ResponseEntity<?> apiException(ApiException e, WebRequest request) {
        log.info("Error request:{}, message: {}",request,e.getMessage());
        HttpStatus status = switch (e.getExceptionCode()) {
            case 1 -> HttpStatus.BAD_REQUEST;
            case 3,4,5 -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status)
                .body(ErrorBean.builder()
                        .url(((ServletWebRequest)request).getRequest().getRequestURI())
                        .code(e.getExceptionCode())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<?> defaultException(Exception e, WebRequest request) {
        log.error("Exception request:{}",request,e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorBean.builder()
                        .url(((ServletWebRequest)request).getRequest().getRequestURI())
                        .code(0)
                        .message(e.getMessage())
                        .build());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers, HttpStatus status,
                                                                   WebRequest request) {
        return resolveException(ex,request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,HttpStatus status,
                                                             WebRequest request) {
        return resolveException(ex,request);
    }

    private ResponseEntity<Object> resolveException(Exception e, WebRequest request){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorBean.builder()
                        .url(((ServletWebRequest)request).getRequest().getRequestURI())
                        .code(0)
                        .message(e.getMessage())
                        .build());

    }


}
