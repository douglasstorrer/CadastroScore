package com.api.cadastroScore.auth.jwt.model;

import lombok.Data;

@Data
public class TokenReturn {
    private String usuario;
    private String token;

}
