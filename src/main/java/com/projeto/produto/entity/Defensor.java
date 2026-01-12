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
    @Column(name = "CNPJ_EMPRESA")
    private String cnpjEmpresa;
    @Column(name = "RG_DEFENSOR")
    private String rgDefensor;
    @Column(name = "ORGAO_EMISSOR")
    private String orgaoEmissor;
    @Column(name = "NATUREZA_PESSOA")
    private String naturezaPessoa;
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
