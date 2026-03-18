package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionarioResponseDTO {
    private Integer idPermissionario;
    private String numeroPermissao;
    private String nomePermissionario;
    private String cpfPermissionario;
    private String cnpjEmpresa;
    private String rgPermissionario;
    private String orgaoEmissor;
    private String filiacaoMae;
    private String filiacaoPai;
    private String sexo;
    private String estadoCivil;
    private String dataNascimento;
    private String ufPermissionario;
    private String cidadePermissionario;
    private String bairroPermissionario;
    private String enderecoPermissionario;
    private String cep;
    private String celularPermissionario;
    private String emailPermissionario;
    private String cnhPermissionario;
    private String categoriaCnhPermissionario;
    private String dataValidadeCnh;
    private String numeroQuitacaoMilitar;
    private String numeroQuitacaoEleitoral;
    private String numeroInscricaoInss;
    private String numeroCertificadoCondutor;
    private String dataValidadeCertificadoCondutor;
    private byte[] anexoRg;
    private byte[] anexoCpf;
    private byte[] anexoCnh;
    private byte[] comprovanteResidencia;
    private byte[] certidaoNegativaMunicipal;
    private byte[] certidaoNegativaCriminal;
    private byte[] certificadoPropriedade;
    private byte[] certificadoCondutor;
    private byte[] apoliceSeguroVida;
    private byte[] apoliceSeguroMotocicleta;
    private byte[] foto;
    private String dataCriacao;
    private String status;
    private String aplicativoAlternativo;
    private String observacao;

}
