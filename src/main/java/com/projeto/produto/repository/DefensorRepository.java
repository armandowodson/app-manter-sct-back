package com.projeto.produto.repository;

import com.projeto.produto.entity.Defensor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DefensorRepository extends JpaRepository<Defensor, Integer> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.defensor " +
                    "WHERE 1 = 1 " +
                    "ORDER BY NOME_DEFENSOR ASC" ,
            nativeQuery = true
    )
    List<Defensor> buscarTodos(Pageable pageable);

    Defensor findDefensorByIdDefensor(Long idDefensor);

    Defensor findDefensorByNumeroPermissao(String numeroPermissao);

    @Query(
            value = "SELECT d.* " +
                    "FROM proj.defensor d, proj.permissionario p " +
                    "WHERE d.NUMERO_PERMISSAO = p.NUMERO_PERMISSAO " +
                    "AND (:numeroPermissao IS NULL OR d.NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:nomeDefensor IS NULL OR UPPER(d.NOME_DEFENSOR) LIKE %:nomeDefensor%) " +
                    "AND (:cpfDefensor IS NULL OR d.CPF_DEFENSOR = :cpfDefensor) " +
                    "AND (:cnpjEmpresa IS NULL OR d.CNPJ_EMPRESA = :cnpjEmpresa) " +
                    "AND (:cnhDefensor IS NULL OR d.CNH_DEFENSOR = :cnhDefensor) " +
                    "AND (:nomePermissionario IS NULL OR UPPER(p.NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:cpfPermissionario IS NULL OR p.CPF_PERMISSIONARIO = :cpfPermissionario) ",
            nativeQuery = true
    )
    List<Defensor> listarTodosDefensorsFiltros(
            String numeroPermissao, String nomeDefensor, String cpfDefensor, String cnpjEmpresa,
            String cnhDefensor, String nomePermissionario, String cpfPermissionario, Pageable pageable
    );

    @Query(
            value = "SELECT * " +
                    "FROM proj.defensor " +
                    "WHERE ID_DEFENSOR NOT IN (SELECT ID_DEFENSOR FROM proj.veiculo) ",
            nativeQuery = true
    )
    List<Defensor> listarDefensorsDisponiveis();

    void deleteDefensorByIdDefensor(Long idDefensor);
}

