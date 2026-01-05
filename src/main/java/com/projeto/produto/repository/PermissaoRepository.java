package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
    Permissao findPermissaoByIdPermissao(Long idPermissao);
    Permissao findPermissaoByNumeroPermissao(String numeroPermissao);

    Permissao findByNumeroPermissao(String numeroPermissao);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE 1 = 1 " +
                    "ORDER BY NUMERO_PERMISSAO ASC" ,
            nativeQuery = true
    )
    List<Permissao> buscarTodas(Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPermissao IS NULL OR NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:numeroAlvara IS NULL OR NUMERO_ALVARA = :numeroAlvara) " +
                    "AND (:anoAlvara IS NULL OR ANO_ALVARA = :anoAlvara) " +
                    "AND (:statusPermissao IS NULL OR STATUS_PERMISSAO = :statusPermissao) " +
                    "AND (:periodoInicial IS NULL OR PERIODO_INICIAL_STATUS >= :periodoInicial) " +
                    "AND (:periodoFinal IS NULL OR PERIODO_FINAL_STATUS <= :periodoFinal) " ,
            nativeQuery = true
    )
    List<Permissao> listarTodasPermissoesFiltros(String numeroPermissao, String numeroAlvara,
                                                 String anoAlvara, String statusPermissao,
                                                 LocalDate periodoInicial, LocalDate periodoFinal,
                                                 Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE NUMERO_PERMISSAO NOT IN (SELECT NUMERO_PERMISSAO FROM PROJ.PERMISSIONARIO) ",
            nativeQuery = true
    )
    List<Permissao> listarPermissaoDisponiveis();

    @Query(
            value = "SELECT count(0) " +
                    "FROM proj.permissionario " +
                    "WHERE NUMERO_PERMISSAO = :numeroPermissao " ,
            nativeQuery = true
    )
    Integer verificarPermissoaExistente(String numeroPermissao);
}

