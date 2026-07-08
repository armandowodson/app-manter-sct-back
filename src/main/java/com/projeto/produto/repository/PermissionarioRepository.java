package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissionario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PermissionarioRepository extends JpaRepository<Permissionario, Integer> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE 1 = 1 " +
                    "ORDER BY NOME_PERMISSIONARIO ASC" ,
            nativeQuery = true
    )
    List<Permissionario> buscarTodos(Pageable pageable);

    @Query(
            value = "SELECT p.* " +
                    "FROM proj.permissionario p " +
                    "WHERE 1 = 1 " +
                    "AND EXISTS ( " +
                    "   SELECT 0 " +
                    "   FROM proj.veiculo v " +
                    "    WHERE v.ID_PERMISSIONARIO = p.ID_PERMISSIONARIO " +
                    ") " +
                    "ORDER BY p.NOME_PERMISSIONARIO ASC" ,
            nativeQuery = true
    )
    List<Permissionario> buscarTodosRelatorio(Pageable pageable);

    Permissionario findPermissionarioByIdPermissionario(Integer idPermissionario);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE 1 = 1 " +
                    "AND (:idPermissionario IS NULL OR ID_PERMISSIONARIO = :idPermissionario) " +
                    "AND (:nomePermissionario IS NULL OR UPPER(NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:cpfPermissionario IS NULL OR CPF_PERMISSIONARIO = :cpfPermissionario) " +
                    "AND (:cnhPermissionario IS NULL OR CNH_PERMISSIONARIO = :cnhPermissionario) ",
            nativeQuery = true
    )
    List<Permissionario> listarTodosPermissionariosFiltros(
            String idPermissionario, String nomePermissionario, String cpfPermissionario, String cnhPermissionario, Pageable pageable
    );

    @Query(
            value = "SELECT p.* " +
                    "FROM proj.permissionario p " +
                    "WHERE 1 = 1 " +
                    "AND (:idPermissionario IS NULL OR p.ID_PERMISSIONARIO = :idPermissionario) " +
                    "AND (:nomePermissionario IS NULL OR UPPER(p.NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:dataInicioValidadeCnh IS NULL OR p.DATA_VALIDADE_CNH >= :dataInicioValidadeCnh) " +
                    "AND (:dataFimValidadeCnh IS NULL OR p.DATA_VALIDADE_CNH <= :dataFimValidadeCnh) " +
                    "AND (:dataInicioValidadeRc IS NULL OR p.DATA_CRIACAO >= :dataInicioValidadeRc) " +
                    "AND (:dataFimValidadeRc IS NULL OR p.DATA_CRIACAO <= :dataFimValidadeRc) " +
                    "AND EXISTS ( " +
                    "   SELECT 0 " +
                    "   FROM proj.veiculo v " +
                    "    WHERE v.ID_PERMISSIONARIO = p.ID_PERMISSIONARIO " +
                    ") ",
            nativeQuery = true
    )
    List<Permissionario> listarTodosPermissionariosFiltrosRelatorio(
            String idPermissionario, String nomePermissionario, LocalDate dataInicioValidadeCnh, LocalDate dataFimValidadeCnh,
            LocalDate dataInicioValidadeRc, LocalDate dataFimValidadeRc, Pageable pageable
    );

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE ID_PERMISSIONARIO NOT IN (SELECT ID_PERMISSIONARIO FROM proj.veiculo) " +
                    "AND STATUS = 'ATIVO' ",
            nativeQuery = true
    )
    List<Permissionario> listarPermissionariosDisponiveis();

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE ID_PERMISSIONARIO NOT IN (SELECT ID_PERMISSIONARIO FROM proj.defensor) " +
                    "AND STATUS = 'ATIVO' ",
            nativeQuery = true
    )
    List<Permissionario> listarPermissionariosDisponiveisDefensor();

    Permissionario findPermissionarioByCpfPermissionario(String cpfPermissionario);
}

