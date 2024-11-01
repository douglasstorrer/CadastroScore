package com.api.cadastroScore.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.api.cadastroScore.model.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, String>{
    @Query("SELECT tabela_pessoa " +
            " FROM Pessoa tabela_pessoa" +
            " WHERE (:nome is null or tabela_pessoa.nome like %:nome%) " +
            " and (:idade = 0 or tabela_pessoa.idade = :idade) " +
            " and (:cep is null or tabela_pessoa.cep = :cep)")
    List<Pessoa> findAllByFields(String nome, int idade , String cep, Pageable paginacao);
    
    List<Pessoa> findByTelefone(String telefone);

}