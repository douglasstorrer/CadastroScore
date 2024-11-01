package com.api.cadastroScore.auth.jwt.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.api.cadastroScore.auth.jwt.service.DetalhesUsuarioServiceImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ValidarJwtFiltro extends BasicAuthenticationFilter{
    
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_BEARER = "Bearer ";
    private DetalhesUsuarioServiceImpl detalhesUsuarioService;

    public ValidarJwtFiltro(AuthenticationManager authenticationManager, DetalhesUsuarioServiceImpl detalhesUsuarioService) {
        super(authenticationManager);
        this.detalhesUsuarioService = detalhesUsuarioService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String atributo = request.getHeader(HEADER_AUTH);
        if (atributo == null || !atributo.startsWith(HEADER_BEARER)) {
            chain.doFilter(request, response);
            return;
        }
        String token = atributo.replace(HEADER_BEARER, "");
        
        UsernamePasswordAuthenticationToken authToken = getAuthToken(token);
      
        SecurityContextHolder.getContext().setAuthentication(authToken);
        chain.doFilter(request, response);
        
    }
    
    private UsernamePasswordAuthenticationToken getAuthToken(String token) {
        String usuario = JWT.require(Algorithm.HMAC512(AutenticarJwtFiltro.SENHA_TOKEN)).build().verify(token).getSubject();
        
        if(usuario == null) {
            return null;
        }
        List<GrantedAuthority> listaAuth = new ArrayList<>();
        UserDetails userDetails = detalhesUsuarioService.loadUserByUsername(usuario);
        listaAuth.addAll(userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(usuario, null, listaAuth);
    }

}
