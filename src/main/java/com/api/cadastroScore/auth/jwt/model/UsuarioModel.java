package com.api.cadastroScore.auth.jwt.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private String nomeUsuario;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;
    @Column
    private List<String> roles = new ArrayList<String>();
    
    public List<GrantedAuthority> getRolesGrantedAuthority(){
        List<GrantedAuthority> listaRetorno = new ArrayList<>();
        for (String role : this.roles) {
            listaRetorno.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return listaRetorno;
    }

}
