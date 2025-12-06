package com.projeto.produto.repository;

import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo,Integer> {
    Veiculo findVeiculoByIdVeiculo(Long idVeiculo);

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
                                                 String numeroTaximetro, String anoFabricacao);

    void deleteVeiculoByIdVeiculo(Long idVeiculo);
}

