package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.permissao")
@Table(name="permissao", schema="proj")
@SequenceGenerator(name = "seq_permissao", schema = "proj", sequenceName = "seq_permissao", initialValue = 10, allocationSize = 1)
@Data
public class Permissao {
    /*


     */
    @Id
    @Column(name = "ID_PERMISSAO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_permissao")
    private Long idPermissao;
    @Column(name = "NUMERO_PERMISSAO")
    private String numeroPermissao;
    @Column(name = "NUMERO_ALVARA")
    private String numeroAlvara;
    @Column(name = "ANO_ALVARA")
    private String anoAlvara;
    @Column(name = "CATEGORIA_PERMISSAO")
    private String categoriaPermissao;
    @Column(name = "STATUS_PERMISSAO")
    private String statusPermissao;
    @Column(name = "PERIODO_INICIAL_STATUS")
    private LocalDate periodoInicialStatus;
    @Column(name = "PERIODO_FINAL_STATUS")
    private LocalDate periodoFinalStatus;
    @Column(name = "DATA_VALIDADE_PERMISSAO")
    private LocalDate dataValidadePermissao;
    @Column(name = "PENALIDADE")
    private String penalidade;
    @Column(name = "DATA_VALIDADE_PENALIDADE")
    private LocalDate dataValidadePenalidade;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
}
