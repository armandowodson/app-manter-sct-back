package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.fiscalizacao")
@Table(name="fiscalizacao", schema="proj")
@SequenceGenerator(name = "seq_fiscalizacao", schema = "proj", sequenceName = "seq_fiscalizacao", initialValue = 10, allocationSize = 1)
@Data
public class Fiscalizacao {
    @Id
    @Column(name = "ID_FISCALIZACAO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_fiscalizacao")
    private Long idFiscalizacao;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VEICULO")
    private Veiculo veiculo;
    @Column(name = "DATA_FISCALIZACAO")
    private LocalDate dataFiscalizacao;
    @Column(name = "MOTIVO_INFRACAO")
    private String motivoInfracao;
    @Column(name = "TIPO_INFRACAO")
    private String tipoInfracao;
    @Column(name = "GRUPO_MULTAS")
    private String grupoMultas;
    @Column(name = "PRAZO_REGULARIZACAO")
    private LocalDate prazoRegularizacao;
    @Column(name = "NATUREZA_INFRACAO")
    private String naturezaInfracao;
    @Column(name = "MODALIDADE")
    private String modalidade;
    @Column(name = "PENALIDADE")
    private String penalidade;
    @Column(name = "OBSERVACAO")
    private String observacao;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;
}
