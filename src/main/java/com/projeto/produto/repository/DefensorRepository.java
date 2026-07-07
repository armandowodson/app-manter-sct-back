package com.projeto.produto.repository;

import com.projeto.produto.entity.Defensor;
import com.projeto.produto.entity.Permissionario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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

    Defensor findDefensorByIdDefensor(Integer idDefensor);

    Defensor findDefensorByPermissionario(Permissionario permissionario);

    @Query(
            value = "SELECT d.* " +
                    "FROM proj.defensor d, proj.permissionario p " +
                    "WHERE d.ID_PERMISSIONARIO = p.ID_PERMISSIONARIO " +
                    "AND (:nomeDefensor IS NULL OR UPPER(d.NOME_DEFENSOR) LIKE %:nomeDefensor%) " +
                    "AND (:cpfDefensor IS NULL OR d.CPF_DEFENSOR = :cpfDefensor) " +
                    "AND (:cnhDefensor IS NULL OR d.CNH_DEFENSOR = :cnhDefensor) " +
                    "AND (:nomePermissionario IS NULL OR UPPER(p.NOME_PERMISSIONARIO) LIKE %:nomePermissionario%) " +
                    "AND (:cpfPermissionario IS NULL OR p.CPF_PERMISSIONARIO = :cpfPermissionario) ",
            nativeQuery = true
    )
    List<Defensor> listarTodosDefensorsFiltros(
            String nomeDefensor, String cpfDefensor,
            String cnhDefensor, String nomePermissionario, String cpfPermissionario, Pageable pageable
    );

    @Query(
            value = "SELECT * " +
                    "FROM proj.defensor " +
                    "WHERE 1 = 1 " +
                    "AND (:idDefensor IS NULL OR ID_DEFENSOR = :idDefensor) " +
                    "AND (:nomeDefensor IS NULL OR UPPER(NOME_DEFENSOR) LIKE %:nomeDefensor%) " +
                    "AND (:dataInicioValidadeCnh IS NULL OR DATA_VALIDADE_CNH >= :dataInicioValidadeCnh) " +
                    "AND (:dataFimValidadeCnh IS NULL OR DATA_VALIDADE_CNH <= :dataFimValidadeCnh) " +
                    "AND (:dataInicioValidadeRc IS NULL OR DATA_CRIACAO >= :dataInicioValidadeRc) " +
                    "AND (:dataFimValidadeRc IS NULL OR DATA_CRIACAO <= :dataFimValidadeRc) ",
            nativeQuery = true
    )
    List<Defensor> listarTodosDefensoresFiltrosRelatorio(
            String idDefensor, String nomeDefensor, LocalDate dataInicioValidadeCnh, LocalDate dataFimValidadeCnh,
            LocalDate dataInicioValidadeRc, LocalDate dataFimValidadeRc, Pageable pageable
    );

    @Query(
            value = "SELECT * " +
                    "FROM proj.defensor " +
                    "WHERE ID_DEFENSOR NOT IN (SELECT ID_DEFENSOR FROM proj.veiculo) ",
            nativeQuery = true
    )
    List<Defensor> listarDefensorsDisponiveis();

    void deleteDefensorByIdDefensor(Integer idDefensor);
}

