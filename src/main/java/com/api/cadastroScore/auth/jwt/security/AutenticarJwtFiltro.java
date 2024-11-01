package com.api.cadastroScore.auth.jwt.security;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.cadastroScore.auth.jwt.model.TokenReturn;
import com.api.cadastroScore.auth.jwt.model.UsuarioModel;
import com.api.cadastroScore.auth.jwt.user.DetalhesUsuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AutenticarJwtFiltro extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    public static final int TEMPO_EXP_TOKEN = 600000;
    public static final String SENHA_TOKEN = "aa43de38-fa39-4df3-b4b0-babb34989c33";
    
    public AutenticarJwtFiltro (AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsuarioModel usuario = new ObjectMapper().readValue(request.getInputStream(), UsuarioModel.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getNomeUsuario(), usuario.getSenha(), usuario.getRolesGrantedAuthority()));
        } catch (Exception e) {
           throw new RuntimeException("Erro ao realizar a autenticação do usuário", e);
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        DetalhesUsuario detalhesUsuario = (DetalhesUsuario) authResult.getPrincipal();
        
        String token = JWT.create().withSubject(detalhesUsuario.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TEMPO_EXP_TOKEN ))
                .sign(Algorithm.HMAC512(SENHA_TOKEN));
        TokenReturn tokenReturn = new TokenReturn();
        tokenReturn.setToken(token);
        tokenReturn.setUsuario(detalhesUsuario.getUsername());
        response.addHeader("Content-type", "application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(tokenReturn));
        response.getWriter().flush();

    }
        
    
}
