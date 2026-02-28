package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
public class DefensorRequestDTO {
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
    private String dataValidadeCertificadoCondutor;
    private String dataCriacao;
    private String usuario;

}
