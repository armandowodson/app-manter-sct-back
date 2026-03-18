package com.projeto.produto.repository;

import com.projeto.produto.entity.Permissionario;
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

    Veiculo findVeiculoByIdVeiculo(Integer idVeiculo);


    Veiculo findVeiculoByPermissionarioAndStatus(Permissionario permissionario, String status);

    @Query(
            value = "SELECT * " +
                    "FROM proj.veiculo " +
                    "WHERE 1 = 1 " +
                    "AND (:placa IS NULL OR PLACA = :placa) " +
                    "AND (:renavam IS NULL OR RENAVAM = :renavam) " +
                    "AND (:cilindrada IS NULL OR CILINDRADA = :cilindrada) " +
                    "AND (:anoFabricacao IS NULL OR ANO_FABRICACAO = :anoFabricacao) ",
            nativeQuery = true
    )
    List<Veiculo> listarTodosVeiculosFiltros(String placa, String renavam, String cilindrada,
                                             String anoFabricacao, Pageable pageable);

    @Query(
            value = "SELECT * " +
                    "FROM proj.veiculo " +
                    "WHERE PLACA = :placa " +
                    "AND STATUS = 'ATIVO' ",
            nativeQuery = true
    )
    List<Veiculo> buscarVeiculoPlaca(String placa);
}

