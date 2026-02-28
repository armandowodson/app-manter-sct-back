package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.defensor")
@Table(name="defensor", schema="proj")
@SequenceGenerator(name = "seq_defensor", schema = "proj", sequenceName = "seq_defensor", initialValue = 10, allocationSize = 1)
@Data
public class Defensor {
    @Id
    @Column(name = "ID_DEFENSOR")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_defensor")
    private Long idDefensor;
    @Column(name = "NUMERO_PERMISSAO")
    private String numeroPermissao;
    @Column(name = "NOME_DEFENSOR")
    private String nomeDefensor;
    @Column(name = "CPF_DEFENSOR")
    private String cpfDefensor;
    @Column(name = "RG_DEFENSOR")
    private String rgDefensor;
    @Column(name = "ORGAO_EMISSOR")
    private String orgaoEmissor;
    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;
    @Column(name = "SEXO")
    private String sexo;
    @Column(name = "ESTADO_CIVIL")
    private String estadoCivil;
    @Column(name = "UF_DEFENSOR")
    private String ufDefensor;
    @Column(name = "CIDADE_DEFENSOR")
    private String cidadeDefensor;
    @Column(name = "BAIRRO_DEFENSOR")
    private String bairroDefensor;
    @Column(name = "ENDERECO_DEFENSOR")
    private String enderecoDefensor;
    @Column(name = "CELULAR_DEFENSOR")
    private String celularDefensor;
    @Column(name = "EMAIL_DEFENSOR")
    private String emailDefensor;
    @Column(name = "CNH_DEFENSOR")
    private String cnhDefensor;
    @Column(name = "CATEGORIA_CNH_DEFENSOR")
    private String categoriaCnhDefensor;
    @Column(name = "NUMERO_QUITACAO_MILITAR")
    private String numeroQuitacaoMilitar;
    @Column(name = "NUMERO_QUITACAO_ELEITORAL")
    private String numeroQuitacaoEleitoral;
    @Column(name = "NUMERO_INSCRICAO_INSS")
    private String numeroInscricaoInss;
    @Column(name = "NUMERO_CERTIFICADO_CONDUTOR")
    private String numeroCertificadoCondutor;
    @Column(name = "DATA_VALIDADE_CER_CON")
    private LocalDate dataValidadeCertificadoCondutor;
    @Lob
    @Column(name = "CERTIFICADO_CONDUTOR")
    private byte[] certificadoCondutor;
    @Lob
    @Column(name = "CERTIDAO_NEGATIVA_CRIMINAL")
    private byte[] certidaoNegativaCriminal;
    @Lob
    @Column(name = "CERTIDAO_NEGATIVA_MUNICIPAL")
    private byte[] certidaoNegativaMunicipal;
    @Lob
    @Column(name = "FOTO")
    private byte[] foto;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;

}
