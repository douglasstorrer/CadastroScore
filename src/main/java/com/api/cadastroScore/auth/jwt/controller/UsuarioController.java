package com.api.cadastroScore.auth.jwt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.cadastroScore.auth.jwt.model.UsuarioModel;
import com.api.cadastroScore.auth.jwt.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
  
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder encriptadorSenha;
  
  public UsuarioController (UsuarioRepository usuarioRepository, PasswordEncoder encriptadorSenha) {
    this .usuarioRepository = usuarioRepository;
    this.encriptadorSenha = encriptadorSenha;
  }

  @GetMapping()
  @RequestMapping(value = "/listar", method = RequestMethod.GET)
  public ResponseEntity<List<UsuarioModel>> listarTodos() {
    return ResponseEntity.ok(usuarioRepository.findAll());
  }

  @PostMapping()
  @RequestMapping(value = "/salvar", method = RequestMethod.POST)
  public ResponseEntity<UsuarioModel> salvar(@RequestBody UsuarioModel usuario) {
    usuario.setSenha(encriptadorSenha.encode(usuario.getSenha()));
    return ResponseEntity.ok(usuarioRepository.save(usuario));
  }

}
