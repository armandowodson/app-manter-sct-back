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
    private String sexo;
    private String estadoCivil;
    private String dataNascimento;
    private String ufDefensor;
    private String cidadeDefensor;
    private String bairroDefensor;
    private String enderecoDefensor;
    private String celularDefensor;
    private String emailDefensor;
    private String cnhDefensor;
    private String categoriaCnhDefensor;
    private String numeroQuitacaoMilitar;
    private String numeroQuitacaoEleitoral;
    private String numeroInscricaoInss;
    private String numeroCertificadoCondutor;
    private byte[] certificadoCondutor;
    private byte[] certidaoNegativaCriminal;
    private byte[] certidaoNegativaMunicipal;
    private byte[] foto;
    private String dataCriacao;
    private String status;

}
