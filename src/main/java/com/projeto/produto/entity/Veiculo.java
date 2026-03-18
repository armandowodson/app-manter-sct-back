package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.veiculo")
@Table(name="veiculo", schema="proj")
@Data
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VEICULO", nullable = false)
    private Integer idVeiculo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERMISSIONARIO")
    private Permissionario permissionario;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PONTOS_TAXI")
    private PontoTaxi pontoTaxi;
    @Column(name = "PLACA")
    private String placa;
    @Column(name = "RENAVAM")
    private String renavam;
    @Column(name = "CHASSI")
    private String chassi;
    @Column(name = "ANO_FABRICACAO")
    private String anoFabricacao;
    @Column(name = "MARCA")
    private String marca;
    @Column(name = "MODELO")
    private String modelo;
    @Column(name = "ANO_MODELO")
    private String anoModelo;
    @Column(name = "COR")
    private String cor;
    @Column(name = "COMBUSTIVEL")
    private String combustivel;
    @Column(name = "CAPACIDADE")
    private String capacidade;
    @Column(name = "QUILOMETRAGEM")
    private String quilometragem;
    @Column(name = "CILINDRADA")
    private String cilindrada;
    @Lob
    @Column(name = "CRLV")
    private byte[] crlv;
    @Column(name = "NUMERO_TAXIMETRO")
    private String numeroTaximetro;
    @Column(name = "ANO_RENOVACAO")
    private String anoRenovacao;
    @Column(name = "DATA_VISTORIA")
    private LocalDate dataVistoria;
    @Column(name = "DATA_RETORNO")
    private LocalDate dataRetorno;
    @Column(name = "STATUS_VISTORIA")
    private String statusVistoria;
    @Column(name = "TIPO_VISTORIA")
    private String tipoVistoria;
    @Column(name = "RESSALVAS")
    private String ressalvas;
    @Column(name = "MATRICULA_VISTORIADOR")
    private String matriculaVistoriador;
    @Column(name = "NUMERO_CAV_EMITIDO")
    private String numeroCavEmitido;
    @Column(name = "SITUACAO_VEICULO")
    private String situacaoVeiculo;
    @Column(name = "NUMERO_CRLV")
    private String numeroCrlv;
    @Column(name = "ANO_CRLV")
    private String anoCrlv;
    @Column(name = "CERTIFICADO_AFERICAO")
    private String certificadoAfericao;
    @Column(name = "TIPO_VEICULO")
    private String tipoVeiculo;
    @Column(name = "OBSERVACAO")
    private String observacao;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;

}
