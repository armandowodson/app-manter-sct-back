package com.projeto.produto.repository;

import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.entity.Vistoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VistoriaRepository extends JpaRepository<Vistoria,Integer> {
    @Query(
            value = "SELECT * " +
                    "FROM proj.vistoria " +
                    "WHERE 1 = 1 " +
                    "ORDER BY DATA_CRIACAO DESC" ,
            nativeQuery = true
    )
    List<Vistoria> buscarTodos(Pageable pageable);

    Vistoria findVistoriaByIdVistoria(Long idVistoria);

    Vistoria findVistoriaByVeiculo(Veiculo veiculo);


    @Query(
            value = "SELECT VI.* " +
                    "FROM PROJ.VISTORIA VI, PROJ.VEICULO VE " +
                    "WHERE VI.ID_VEICULO = VE.ID_VEICULO " +
                    "AND (:numeroPermissao IS NULL OR VE.NUMERO_PERMISSAO = :numeroPermissao) " +
                    "AND (:placa IS NULL OR VE.PLACA = :placa) " +
                    "AND (:statusVistoria IS NULL OR VI.STATUS = :statusVistoria) ",
            nativeQuery = true
    )
    List<Vistoria> listarTodasVistoriasFiltros(String numeroPermissao, String placa, String statusVistoria,
                                              Pageable pageable);

}

