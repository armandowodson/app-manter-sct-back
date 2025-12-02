package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionarioRepository extends JpaRepository<Permissionario, Integer> {
    Permissionario findPermissionarioByIdPermissionario(Long idPermissionario);

    @Query(
            value = "SELECT * " +
                    "FROM proj.permissionario " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPermissao IS NULL OR NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:nomePermissionario IS NULL OR UPPER(NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:cpfPermissionario IS NULL OR CPF_PERMISSIONARIO = :cpfPermissionario) " +
                    "AND (:cnpjEmpresa IS NULL OR NPJ_EMPRESA = :cnpjEmpresa) " +
                    "AND (:cnhPermissionario IS NULL OR CNH_PERMISSIONARIO = :cnhPermissionario) ",
            nativeQuery = true
    )
    List<Permissionario> listarTodosPermissionariosFiltros(
            String numeroPermissao, String nomePermissionario, String cpfPermissionario,
            String cnpjEmpresa, String cnhPermissionario
    );

    void deletePermissionarioByIdPermissionario(Long idPermissionario);
}

