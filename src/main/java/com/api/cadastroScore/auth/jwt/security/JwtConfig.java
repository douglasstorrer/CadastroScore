package com.api.cadastroScore.auth.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.api.cadastroScore.auth.jwt.service.DetalhesUsuarioServiceImpl;

@Configuration
@EnableWebSecurity
public class JwtConfig {

    private final DetalhesUsuarioServiceImpl usuarioService;

    private AuthenticationConfiguration configuration;
    
    
    public JwtConfig(DetalhesUsuarioServiceImpl usuarioService, AuthenticationConfiguration configuration) {
      this.usuarioService = usuarioService;
      this.configuration = configuration;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
      
      http.csrf(csrf -> csrf.disable()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests((auth) -> auth
      .requestMatchers(HttpMethod.POST, "/login").permitAll()
      .requestMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagger-ui.html").permitAll()
      .requestMatchers(HttpMethod.POST, "/api/score/cadastra-pessoa").hasRole("ADMIN")
      .requestMatchers(HttpMethod.POST, "/api/score/atualiza-cadastro").hasRole("ADMIN")
      .requestMatchers(HttpMethod.POST, "/api/score/exclui-cadastro").hasRole("ADMIN")
      .requestMatchers("/api/usuario/**").hasRole("ADMIN")
      .requestMatchers(HttpMethod.GET, "/api/score/busca").authenticated()
      .anyRequest().permitAll());
      http.addFilterBefore(new AutenticarJwtFiltro(configuration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);
      http.addFilterBefore(new ValidarJwtFiltro(configuration.getAuthenticationManager(), usuarioService), BasicAuthenticationFilter.class);
      return http.build();
    }
    
}

