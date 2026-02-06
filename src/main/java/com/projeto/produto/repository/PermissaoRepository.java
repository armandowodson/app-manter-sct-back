package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            value = "SELECT MAX(NUMERO_PERMISSAO) " +
                    "FROM proj.permissao " +
                    "WHERE 1 = 1 " +
                    "ORDER BY NUMERO_PERMISSAO ASC" ,
            nativeQuery = true
    )
    Long buscarNumeroMaximo();

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPermissao IS NULL OR NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:numeroAlvara IS NULL OR NUMERO_ALVARA = :numeroAlvara) " +
                    "AND (:anoPermissao IS NULL OR ANO_PERMISSAO = :anoPermissao) " +
                    "AND (:statusPermissao IS NULL OR STATUS_PERMISSAO = :statusPermissao) " +
                    "AND PERIODO_INICIAL_STATUS >= :periodoInicial AND PERIODO_FINAL_STATUS <= :periodoFinal " ,
            nativeQuery = true
    )
    List<Permissao> listarTodasPermissoesFiltros(@Param("numeroPermissao") String numeroPermissao, @Param("numeroAlvara") String numeroAlvara,
                                                 @Param("anoPermissao") String anoPermissao, @Param("statusPermissao") String statusPermissao,
                                                 @Param("periodoInicial") LocalDate periodoInicial, @Param("periodoFinal") LocalDate periodoFinal,
                                                 Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE NUMERO_PERMISSAO NOT IN (SELECT NUMERO_PERMISSAO FROM PROJ.PERMISSIONARIO) " +
                    "AND STATUS = 'ATIVO' ",
            nativeQuery = true
    )
    List<Permissao> listarPermissaoDisponiveis();

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissao " +
                    "WHERE NUMERO_PERMISSAO IN (SELECT NUMERO_PERMISSAO FROM PROJ.PERMISSIONARIO) " +
                    "AND NUMERO_PERMISSAO NOT IN (SELECT NUMERO_PERMISSAO FROM PROJ.DEFENSOR) " +
                    "AND STATUS = 'ATIVO' ",
            nativeQuery = true
    )
    List<Permissao> listarPermissaoDisponiveisDefensor();

    @Query(
            value = "SELECT count(0) " +
                    "FROM proj.permissionario " +
                    "WHERE NUMERO_PERMISSAO = :numeroPermissao " ,
            nativeQuery = true
    )
    Integer verificarPermissoaExistente(String numeroPermissao);
}

