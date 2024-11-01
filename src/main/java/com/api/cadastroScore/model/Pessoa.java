package com.api.cadastroScore.model;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Pessoa { 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(nullable = false)
  private String nome;
  @Column(nullable = false)
  private String telefone;
  @Column(nullable = false)
  private Integer idade;
  @Column(length = 50, nullable = false)
  private String cep;
  @Column(length = 50, nullable = false)
  private String estado;
  @Column(length = 50, nullable = false)
  private String cidade;
  @Column(length = 50, nullable = false)
  private String bairro;
  @Column(length = 100, nullable = false)
  private String logradouro;
  @Column(nullable = false)
  private Integer score;
  @Column(nullable = false)
  private String scoreDescricao;
}