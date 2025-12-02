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
                    "AND (:numeroPermissao IS NULL OR NUMERO_PONTO = :numeroPermissao) " +
                    "AND (:placa IS NULL OR FATOR_ROTATIVIDADE = :placa) " +
                    "AND (:renavam IS NULL OR NUMERO_VAGAS = :renavam) " +
                    "AND (:numeroTaximetro IS NULL OR NUMERO_VAGAS = :numeroTaximetro) " +
                    "AND (:anoFabricacao IS NULL OR NUMERO_VAGAS = :anoFabricacao) ",
            nativeQuery = true
    )
    List<Veiculo> listarTodosVeiculosFiltros(String numeroPermissao, String placa, String renavam,
                                                 String numeroTaximetro, String anoFabricacao);

    void deleteVeiculoByIdVeiculo(Long idVeiculo);
}

