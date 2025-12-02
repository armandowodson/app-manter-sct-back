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
    @Column(name = "CNPJ_EMPRESA")
    private String cnpjEmpresa;
    @Column(name = "RG_PERMISSIONARIO")
    private String rgPermissionario;
    @Column(name = "NATUREZA_PESSOA")
    private String naturezaPessoa;
    @Column(name = "CNH_PERMISSIONARIO")
    private String cnhPermissionario;
    @Column(name = "UF_PERMISSIONARIO")
    private String ufPermissionario;
    @Column(name = "BAIRRO_PERMISSIONARIO")
    private String bairroPermissionario;
    @Column(name = "ENDERECO_PERMISSIONARIO")
    private String enderecoPermissionario;
    @Column(name = "CELULAR_PERMISSIONARIO")
    private String celularPermissionario;
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

}
