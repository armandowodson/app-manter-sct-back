package com.projeto.produto.repository;

import com.projeto.produto.entity.Auditoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.auditoria " +
                    "WHERE 1 = 1 " +
                    "ORDER BY NOME_MODULO ASC" ,
            nativeQuery = true
    )
    List<Auditoria> buscarTodos(Pageable pageable);

    Auditoria findByIdAuditoria(Long idAuditoria);

    @Query(
            value = "SELECT * " +
                    "FROM proj.auditoria " +
                    "WHERE 1 = 1 " +
                    "AND (:nomeModulo IS NULL OR UPPER(NOME_MODULO) LIKE %:nomeModulo%) " +
                    "AND (:usuarioOperacao IS NULL OR UPPER(USUARIO_OPERACAO) = UPPER(:usuarioOperacao)) " +
                    "AND (:operacao IS NULL OR OPERACAO = :operacao) " +
                    "AND (:dataInicioOperacao IS NULL OR DATA_OPERACAO >= :dataInicioOperacao) " +
                    "AND (:dataFimOperacao IS NULL OR DATA_OPERACAO <= :dataFimOperacao) " ,
            nativeQuery = true
    )
    List<Auditoria> listarTodasAuditoriasFiltros(String nomeModulo, String usuarioOperacao,
                                                 String operacao, LocalDate dataInicioOperacao,
                                                 LocalDate dataFimOperacao, Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.auditoria " +
                    "WHERE 1 = 1 " +
                    "AND (:nomeModulo IS NULL OR UPPER(NOME_MODULO) LIKE %:nomeModulo%) " +
                    "AND (:usuarioOperacao IS NULL OR UPPER(USUARIO_OPERACAO) = UPPER(:usuarioOperacao)) " +
                    "AND (:operacao IS NULL OR OPERACAO = :operacao) " ,
            nativeQuery = true
    )
    List<Auditoria> listarTodasAuditoriasFiltrosSemDatas(String nomeModulo, String usuarioOperacao, String operacao, Pageable pageable);

}

