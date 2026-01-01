package com.projeto.produto.repository;

import com.projeto.produto.entity.Defensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DefensorRepository extends JpaRepository<Defensor, Integer> {
    Defensor findDefensorByIdDefensor(Long idDefensor);

    @Query(
            value = "SELECT * " +
                    "FROM proj.defensor " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPermissao IS NULL OR NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:nomeDefensor IS NULL OR UPPER(NOME_DEFENSOR) LIKE %:nomeDefensor%) " +
                    "AND (:cpfDefensor IS NULL OR CPF_DEFENSOR = :cpfDefensor) " +
                    "AND (:cnpjEmpresa IS NULL OR CNPJ_EMPRESA = :cnpjEmpresa) " +
                    "AND (:cnhDefensor IS NULL OR CNH_DEFENSOR = :cnhDefensor) ",
            nativeQuery = true
    )
    List<Defensor> listarTodosDefensorsFiltros(
            String numeroPermissao, String nomeDefensor, String cpfDefensor,
            String cnpjEmpresa, String cnhDefensor
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

