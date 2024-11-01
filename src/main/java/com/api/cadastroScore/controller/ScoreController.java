package com.api.cadastroScore.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.cadastroScore.auth.jwt.model.ErrorResponse;
import com.api.cadastroScore.auth.jwt.model.SuccessResponse;
import com.api.cadastroScore.model.ConsultaCepResponse;
import com.api.cadastroScore.model.Pessoa;
import com.api.cadastroScore.repository.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/score")
@AllArgsConstructor
public class ScoreController{
    
  private final PessoaRepository pessoaRepository;
  private static final Integer REG_POR_PAGINA = 5;
  private static final String  URI_CONSULTA_CEP = "https://viacep.com.br/ws/";

  @PostMapping
  @RequestMapping(value = "/cadastra-pessoa", method = RequestMethod.POST)
  public ResponseEntity<Object> cadastraPessoa(@RequestBody Pessoa pessoa) {
    try{
      List<String> camposInvalidos = validaCampos(pessoa);
      if(!camposInvalidos.isEmpty()) {
        ErrorResponse mensagemErro = new ErrorResponse(400,"Campos inválidos: " + camposInvalidos.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);                                      
      }
      Pessoa pessoaDadosCep = buscaDadosCep(pessoa);
      pessoaRepository.save(pessoaDadosCep);
      return ResponseEntity.status(HttpStatus.CREATED).body(pessoaDadosCep);                                      
    }catch(Exception e) {
      ErrorResponse mensagemErro = new ErrorResponse(500,"Houve um erro ao realizar o cadastro: " + e.getLocalizedMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensagemErro);                                      
    }
  }
  
  @GetMapping
  @RequestMapping(value = "/busca", method = RequestMethod.GET)
  public ResponseEntity<Object> buscaPessoa(@RequestParam(value="nome", required=false) String nome, 
        @RequestParam(value="cep", required=false) String cep, 
        @RequestParam(value="idade", required=false, defaultValue="0") Integer idade, 
        @RequestParam(value="pagina", required=false, defaultValue = "1") Integer pagina) {
    pagina = (pagina != null && 0 < pagina.compareTo(0)) ? pagina - 1 : 0;
    Pageable paginaConsulta = PageRequest.of(pagina, REG_POR_PAGINA);
    List<Pessoa> listaPessoas = pessoaRepository.findAllByFields(nome, idade, cep, paginaConsulta);
    return  listaPessoas.isEmpty() ? ResponseEntity.ok().build() : ResponseEntity.ok(listaPessoas);
  }
    
  @PostMapping
  @RequestMapping(value = "/atualiza-cadastro", method = RequestMethod.POST)
  public ResponseEntity<Object> atualizaCadastro(@RequestBody Pessoa pessoa) {
    if (pessoa.getId() != null && pessoaRepository.findById(pessoa.getId().toString()).isPresent()) {  
      List<String> camposInvalidos = validaCampos(pessoa);
      if(!camposInvalidos.isEmpty()) {
        ErrorResponse mensagemErro = new ErrorResponse(400,"Campos inválidos: " + camposInvalidos.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);                                      
      }
      try{
        Pessoa pessoaDadosCep = buscaDadosCep(pessoa);
        pessoaRepository.save(pessoaDadosCep);
      }catch (Exception e) {
        ErrorResponse mensagemErro = new ErrorResponse(500,"Houve um erro ao atualizar o cadastro: " + e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensagemErro);     
      }
      return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);   
    }else {
      ErrorResponse mensagemErro = new ErrorResponse(400,"Informe um Id de cadastro válido para atualizar.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);            
    }
  }
  
  @DeleteMapping
  @RequestMapping(value = "/exclui-cadastro", method = RequestMethod.DELETE)
  public ResponseEntity<Object> excluiCadastro(@RequestBody Pessoa pessoa) {
    if(pessoa.getId() != null && pessoaRepository.findById(pessoa.getId().toString()).isPresent()) {  
      pessoaRepository.delete(pessoa);
      SuccessResponse mensagemSucesso = new SuccessResponse(200, "Registro excluído com sucesso!");
      return ResponseEntity.status(HttpStatus.OK).body(mensagemSucesso);   
    }else {
      ErrorResponse mensagemErro = new ErrorResponse(400,"Informe um Id de cadastro válido para excluir.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);            
    }
  }
  
  private Pessoa buscaDadosCep(Pessoa pessoa) throws Exception {
    if(pessoa.getCep() != null ) {
      HttpRequest requestHttp;
      HttpClient htppClient = HttpClient.newBuilder().sslContext(SSLContext.getDefault()).build();
      String uri = URI_CONSULTA_CEP + pessoa.getCep() + "/json/";
      requestHttp = HttpRequest.newBuilder().uri(new URI(uri)).GET().header("Content-type", "application/json").build();
      HttpResponse<String> resposta =  htppClient.send(requestHttp, BodyHandlers.ofString());
      ConsultaCepResponse respostaDadosCep = new ObjectMapper().readValue(resposta.body(), ConsultaCepResponse.class);
      pessoa.setEstado(respostaDadosCep.getEstado());
      pessoa.setCidade(respostaDadosCep.getLocalidade());
      pessoa.setBairro(respostaDadosCep.getBairro());
      pessoa.setLogradouro(respostaDadosCep.getLogradouro());
      pessoa.setScoreDescricao(processaScoreDescricao(pessoa.getScore()));
    }
    return pessoa;
  }
  
  private String processaScoreDescricao(Integer score) {
    String descricao;
    if(score < 0) {
      descricao = "NULL";
    } else if(score <= 200) {
      descricao = "Insuficiente";
    } else if(score <= 500) {
      descricao = "Inaceitável";
    } else if(score <= 700) {
      descricao = "Aceitável";
    } else if(score <= 1000) {
      descricao = "Recomendável";
    } else {
      descricao = "Score inválido.";
    }
    return descricao;
  }
  
  private List<String> validaCampos(Pessoa pessoa) {
    List<String> camposInvalidos = new ArrayList<>();
    if(pessoa.getNome() == null || pessoa.getNome().isBlank()) {
      camposInvalidos.add("Nome");
    }
    if(pessoa.getTelefone() == null || pessoa.getTelefone().isBlank()) {
      camposInvalidos.add("Telefone");
    }
    if(pessoa.getIdade() == null || pessoa.getIdade() <= 0) {
      camposInvalidos.add("Idade");
    }
    if(pessoa.getCep() == null || pessoa.getCep().isBlank()) {
      camposInvalidos.add("CEP");
    }
    if(pessoa.getScore() == null || pessoa.getScore() < 0 || pessoa.getScore() > 1000) {
      camposInvalidos.add("Score");
    }
    return camposInvalidos;
  }
  
}