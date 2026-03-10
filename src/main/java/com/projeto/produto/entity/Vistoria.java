package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.vistoria")
@Table(name="vistoria", schema="proj")
@SequenceGenerator(name = "seq_vistoria", schema = "proj", sequenceName = "seq_vistoria", initialValue = 10, allocationSize = 1)
@Data
public class Vistoria {
    @Id
    @Column(name = "ID_VISTORIA")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_vistoria")
    private Long idVistoria;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VEICULO")
    private Veiculo veiculo;
    @Column(name = "CHASSIFUNILARIAPINTURA")
    private String chassiFunilariaPintura;
    @Column(name = "INSTALACAOELETRICA")
    private String instalacaoEletrica;
    @Column(name = "FAROLATLABAIXA")
    private String farolAtlaBaixa;
    @Column(name = "BUZINA")
    private String buzina;
    @Column(name = "LANTERNATRASEIRA")
    private String lanternaTraseira;
    @Column(name = "FREIODIANTEIRO")
    private String freioDianteiro;
    @Column(name = "LUZPLACA")
    private String luzPlaca;
    @Column(name = "FREIOTRASEIRO")
    private String freioTraseiro;
    @Column(name = "LUZESDIRECAO")
    private String luzesDirecao;
    @Column(name = "PNEUSDESGATECALIBRAGEM")
    private String pneusDesgateCalibragem;
    @Column(name = "LUZFREIO")
    private String luzFreio;
    @Column(name = "CORRENTECORREIA")
    private String correnteCorreia;
    @Column(name = "PLACASDIANTEIRATRASEIRA")
    private String placasDianteiraTraseira;
    @Column(name = "VAZAMENTOOLEOCOMBUSTIVEL")
    private String vazamentoOleoCombustivel;
    @Column(name = "LIMPEZAGERALINTERNA")
    private String limpezaGeralInterna;
    @Column(name = "ESCAPAMENTO")
    private String escapamento;
    @Column(name = "ASSENTOFIXACAO")
    private String assentoFixacao;
    @Column(name = "EQUIPAMENTOSOBRIGATORIOS")
    private String equipamentosObrigatorios;
    @Column(name = "ESPELHOSRETROVISORES")
    private String espelhosRetrovisores;
    @Column(name = "SELOSVISTORIA")
    private String selosVistoria;
    @Column(name = "GUIDAOMANOPLAS")
    private String guidaoManoplas;
    @Column(name = "OUTROS")
    private String outros;
    @Column(name = "DATA_VISTORIA")
    private LocalDate dataVistoria;
    @Column(name = "DATA_RETORNO")
    private LocalDate dataRetorno;
    @Column(name = "STATUS_VISTORIA")
    private String statusVistoria;
    @Column(name = "RESSALVAS")
    private String ressalvas;
    @Lob
    @Column(name = "COMPROVANTE_VISTORIA")
    private byte[] comprovanteVistoria;
    @Column(name = "OBSERVACAO")
    private String observacao;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;

}
