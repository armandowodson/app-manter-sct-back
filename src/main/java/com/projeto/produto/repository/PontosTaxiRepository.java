package com.projeto.produto.repository;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.entity.PontoTaxi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PontosTaxiRepository extends JpaRepository<PontoTaxi,Integer> {
    PontoTaxi findByIdPontosTaxi(Long idPontosTaxi);

    void deletePontoTaxiByIdPontosTaxi(Long idPontosTaxi);

    @Query(
            value = "SELECT * " +
                    "FROM proj.pontos_taxi " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPonto IS NULL OR NUMERO_PONTO = :numeroPonto) " +
                    "AND (:descricaoPonto IS NULL OR UPPER(DESCRICAO_PONTO) LIKE %:descricaoPonto%) " +
                    "AND (:fatorRotatividade IS NULL OR FATOR_ROTATIVIDADE = :fatorRotatividade) " +
                    "AND (:referenciaPonto IS NULL OR UPPER(REFERENCIA_PONTO) LIKE %:referenciaPonto%) ",
            nativeQuery = true
    )
    List<PontoTaxi> listarTodosPontosTaxiFiltros(String numeroPonto, String descricaoPonto,
                                                 String fatorRotatividade, String referenciaPonto);
}

