package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefensorResponseDTO {
    private Long idDefensor;
    private String numeroPermissao;
    private String nomeDefensor;
    private String cpfDefensor;
    private String rgDefensor;
    private String orgaoEmissor;
    private String filiacaoMae;
    private String filiacaoPai;
    private String sexo;
    private String estadoCivil;
    private String dataNascimento;
    private String ufDefensor;
    private String cidadeDefensor;
    private String bairroDefensor;
    private String enderecoDefensor;
    private String cep;
    private String celularDefensor;
    private String emailDefensor;
    private String cnhDefensor;
    private String categoriaCnhDefensor;
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

}
