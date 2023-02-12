package com.nikh.cth.bean.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    int code;
    String url;
    String message;
    String details;
}
