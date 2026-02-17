package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Veiculo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo,Integer> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.veiculo " +
                    "WHERE 1 = 1 " +
                    "ORDER BY PLACA ASC" ,
            nativeQuery = true
    )
    List<Veiculo> buscarTodos(Pageable pageable);

    Veiculo findVeiculoByIdVeiculo(Long idVeiculo);

    Veiculo findVeiculoByPermissionario(Permissionario permissionario);

    Veiculo findVeiculoByNumeroPermissao(String numeroPermissao);

    @Query(
            value = "SELECT * " +
                    "FROM proj.veiculo " +
                    "WHERE 1 = 1 " +
                    "AND (:numeroPermissao IS NULL OR NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:placa IS NULL OR PLACA = :placa) " +
                    "AND (:renavam IS NULL OR RENAVAM = :renavam) " +
                    "AND (:numeroTaximetro IS NULL OR NUMERO_TAXIMETRO = :numeroTaximetro) " +
                    "AND (:anoFabricacao IS NULL OR ANO_FABRICACAO = :anoFabricacao) ",
            nativeQuery = true
    )
    List<Veiculo> listarTodosVeiculosFiltros(String numeroPermissao, String placa, String renavam,
                                                 String numeroTaximetro, String anoFabricacao,
                                                 Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.veiculo " +
                    "WHERE PLACA = :placa ",
            nativeQuery = true
    )
    List<Veiculo> buscarVeiculoPlaca(String placa);
}

