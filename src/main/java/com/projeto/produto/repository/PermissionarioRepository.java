package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissionario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    Permissionario findPermissionarioByIdPermissionario(Long idPermissionario);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE 1 = 1 " +
                    "AND (:nomePermissionario IS NULL OR UPPER(NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:cpfPermissionario IS NULL OR CPF_PERMISSIONARIO = :cpfPermissionario) " +
                    "AND (:cnhPermissionario IS NULL OR CNH_PERMISSIONARIO = :cnhPermissionario) ",
            nativeQuery = true
    )
    List<Permissionario> listarTodosPermissionariosFiltros(
            String nomePermissionario, String cpfPermissionario, String cnhPermissionario, Pageable pageable
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

