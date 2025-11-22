package com.projeto.produto.repository;

import com.projeto.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Produto,Integer> {
    List<Produto> findByNomeStartsWithIgnoreCaseAndSituacao(String nome, Integer situacao);
    List<Produto> findByNomeStartsWithIgnoreCase(String nome);
    List<Produto> findBySituacao(Integer situacao);
    List<Produto> findByNomeStartsWithIgnoreCaseAndSituacaoAndPrecoGreaterThanEqual(
            String nome, Integer situacao, Double preco
    );
    List<Produto> findByNomeStartsWithIgnoreCaseAndSituacaoAndPrecoLessThanEqual(
            String nome, Integer situacao, Double preco
    );
    List<Produto> findByPrecoGreaterThanEqual(Double preco);
    List<Produto> findByPrecoLessThanEqual(Double preco);
    List<Produto> findByPrecoBetween(Double precoIni, Double precoFin);
}

