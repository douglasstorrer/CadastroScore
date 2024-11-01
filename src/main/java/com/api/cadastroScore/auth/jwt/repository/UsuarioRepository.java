package com.api.cadastroScore.auth.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.cadastroScore.auth.jwt.model.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository <UsuarioModel, Integer> {
    public Optional<UsuarioModel> findByNomeUsuario (String nomeUsuario);

}
