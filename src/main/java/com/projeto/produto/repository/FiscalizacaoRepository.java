package com.projeto.produto.repository;

import com.projeto.produto.entity.Fiscalizacao;
import com.projeto.produto.entity.Permissao;
import com.projeto.produto.entity.Veiculo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FiscalizacaoRepository extends JpaRepository<Fiscalizacao,Integer> {
    Fiscalizacao findFiscalizacaoByIdFiscalizacao(Long idFiscalizacao);

    @Query(
            value = "SELECT * " +
                    "FROM proj.fiscalizacao " +
                    "WHERE 1 = 1 " +
                    "ORDER BY DATA_FISCALIZACAO DESC" ,
            nativeQuery = true
    )
    List<Fiscalizacao> buscarTodas(Pageable pageable);

    @Query(
            value = "select f.* " +
                    "from proj.fiscalizacao f, proj.veiculo v, " +
                    "     proj.permissionario p " +
                    "where f.id_veiculo = v.id_veiculo " +
                    "and p.id_permissionario = v.id_permissionario " +
                    "and (:placa is null or v.placa = :placa) " +
                    "and (:nomePermissionario is null or UPPER(p.nome_permissionario) like %:nomePermissionario%) " +
                    "and (:motivoInfracao is null or f.motivo_infracao = :motivoInfracao) " +
                    "and (:penalidade is null or f.penalidade = :penalidade) " +
                    "and f.data_fiscalizacao >= :dataFiscalizacao ",
            nativeQuery = true
    )
    List<Fiscalizacao> listarTodasFiscalizacoesFiltros(String placa, String nomePermissionario,
                                                       String motivoInfracao, String penalidade,
                                                       LocalDate dataFiscalizacao, Pageable pageable);
    /*List<Fiscalizacao> listarTodasFiscalizacoesFiltros(String placa, String nomePermissionario,
                                                       LocalDate dataFiscalizacao, String motivoInfracao,
                                                       String penalidade, Pageable pageable);*/
}

