package com.api.cadastroScore.auth.jwt.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.api.cadastroScore.auth.jwt.model.UsuarioModel;
import com.api.cadastroScore.auth.jwt.repository.UsuarioRepository;
import com.api.cadastroScore.auth.jwt.user.DetalhesUsuario;

@Component
public class DetalhesUsuarioServiceImpl implements UserDetailsService {
    
  private final UsuarioRepository usuarioRepository;
  
  public DetalhesUsuarioServiceImpl (UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UsuarioModel> usuario = Optional.of(usuarioRepository.findByNomeUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException(" Usuário " + username + " não encontrado.")));
    return new DetalhesUsuario(usuario);
  }

}
