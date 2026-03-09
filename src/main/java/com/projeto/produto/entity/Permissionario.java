package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.permissionario")
@Table(name="permissionario", schema="proj")
@SequenceGenerator(name = "seq_permissionario", schema = "proj", sequenceName = "seq_permissionario", initialValue = 10, allocationSize = 1)
@Data
public class Permissionario {
    @Id
    @Column(name = "ID_PERMISSIONARIO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_permissionario")
    private Long idPermissionario;
    @Column(name = "NUMERO_PERMISSAO")
    private String numeroPermissao;
    @Column(name = "NOME_PERMISSIONARIO")
    private String nomePermissionario;
    @Column(name = "CPF_PERMISSIONARIO")
    private String cpfPermissionario;
    @Column(name = "RG_PERMISSIONARIO")
    private String rgPermissionario;
    @Column(name = "ORGAO_EMISSOR")
    private String orgaoEmissor;
    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;
    @Column(name = "FILIACAO_MAE")
    private String filiacaoMae;
    @Column(name = "FILIACAO_PAI")
    private String filiacaoPai;
    @Column(name = "SEXO")
    private String sexo;
    @Column(name = "ESTADO_CIVIL")
    private String estadoCivil;
    @Column(name = "UF_PERMISSIONARIO")
    private String ufPermissionario;
    @Column(name = "CIDADE_PERMISSIONARIO")
    private String cidadePermissionario;
    @Column(name = "BAIRRO_PERMISSIONARIO")
    private String bairroPermissionario;
    @Column(name = "ENDERECO_PERMISSIONARIO")
    private String enderecoPermissionario;
    @Column(name = "CEP")
    private String cep;
    @Column(name = "CELULAR_PERMISSIONARIO")
    private String celularPermissionario;
    @Column(name = "EMAIL_PERMISSIONARIO")
    private String emailPermissionario;
    @Column(name = "CNH_PERMISSIONARIO")
    private String cnhPermissionario;
    @Column(name = "CATEGORIA_CNH_PERMISSIONARIO")
    private String categoriaCnhPermissionario;
    @Column(name = "DATA_VALIDADE_CNH")
    private LocalDate dataValidadeCnh;
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
    @Column(name = "ANEXO_RG")
    private byte[] anexoRg;
    @Lob
    @Column(name = "ANEXO_CPF")
    private byte[] anexoCpf;
    @Lob
    @Column(name = "ANEXO_CNH")
    private byte[] anexoCnh;
    @Lob
    @Column(name = "COMPROVANTE_RESIDENCIA")
    private byte[] comprovanteResidencia;
    @Lob
    @Column(name = "CERTIDAO_NEGATIVA_MUNICIPAL")
    private byte[] certidaoNegativaMunicipal;
    @Lob
    @Column(name = "CERTIDAO_NEGATIVA_CRIMINAL")
    private byte[] certidaoNegativaCriminal;
    @Lob
    @Column(name = "CERTIFICADO_PROPRIEDADE")
    private byte[] certificadoPropriedade;
    @Lob
    @Column(name = "CERTIFICADO_CONDUTOR")
    private byte[] certificadoCondutor;
    @Lob
    @Column(name = "APOLICE_SEGURO_VIDA")
    private byte[] apoliceSeguroVida;
    @Lob
    @Column(name = "APOLICE_SEGURO_MOTOCICLETA")
    private byte[] apoliceSeguroMotocicleta;
    @Lob
    @Column(name = "FOTO")
    private byte[] foto;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "APLICATIVO_ALTERNATIVO")
    private String aplicativoAlternativo;
    @Column(name = "OBSERVACAO")
    private String observacao;

}
