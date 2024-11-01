package com.api.cadastroScore.auth.jwt.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.api.cadastroScore.auth.jwt.model.UsuarioModel;

public class DetalhesUsuario implements UserDetails {

    private final Optional<UsuarioModel> usuario;
    
    public DetalhesUsuario(Optional<UsuarioModel> usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        List<String> listaRoles = usuario.orElse(new UsuarioModel()).getRoles();
        for(String role : listaRoles) {
          list.add(new SimpleGrantedAuthority(role));
        }
        return list;
    }

    @Override
    public String getPassword() {
        return usuario.orElse(new UsuarioModel()).getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.orElse(new UsuarioModel()).getNomeUsuario();
    }
    

}
