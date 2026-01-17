package com.projeto.produto.repository;

import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.entity.PontoTaxi;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PontosTaxiRepository extends JpaRepository<PontoTaxi, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.pontos_taxi " +
                    "WHERE 1 = 1 " +
                    "ORDER BY DESCRICAO_PONTO ASC" ,
            nativeQuery = true
    )
    List<PontoTaxi> buscarTodos(Pageable pageable);

    PontoTaxi findByIdPontoTaxi(Long idPontoTaxi);

    PontoTaxi findPontoTaxiByNumeroPonto(String numeroPonto);

    void deletePontoTaxiByIdPontoTaxi(Long idPontoTaxi);

    @Query(
            value = "SELECT * " +
                    "FROM proj.pontos_taxi " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPonto IS NULL OR NUMERO_PONTO = :numeroPonto) " +
                    "AND (:descricaoPonto IS NULL OR UPPER(DESCRICAO_PONTO) LIKE %:descricaoPonto%) " +
                    "AND (:fatorRotatividade IS NULL OR FATOR_ROTATIVIDADE = :fatorRotatividade) " +
                    "AND (:numeroVagas IS NULL OR NUMERO_VAGAS = :numeroVagas) " +
                    "AND (:referenciaPonto IS NULL OR UPPER(REFERENCIA_PONTO) LIKE %:referenciaPonto%) " +
                    "AND (:modalidade IS NULL OR MODALIDADE = :modalidade) " ,
            nativeQuery = true
    )
    List<PontoTaxi> listarTodosPontosTaxiFiltros(String numeroPonto, String descricaoPonto,
                                                 String fatorRotatividade, String referenciaPonto,
                                                 String numeroVagas, String modalidade,
                                                 Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.pontos_taxi " +
                    "order by descricao_ponto ",
            nativeQuery = true
    )
    List<PontoTaxi> listarPontosTaxiDisponiveis();
}

