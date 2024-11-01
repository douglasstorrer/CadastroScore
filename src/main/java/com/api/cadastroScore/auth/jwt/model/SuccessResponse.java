package com.api.cadastroScore.auth.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponse {
    private Integer statusCode;
    private String message;
}
