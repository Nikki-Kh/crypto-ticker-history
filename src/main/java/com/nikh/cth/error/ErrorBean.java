package com.nikh.cth.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorBean {

    int code;

    String url;
    String message;
    String details;
}
