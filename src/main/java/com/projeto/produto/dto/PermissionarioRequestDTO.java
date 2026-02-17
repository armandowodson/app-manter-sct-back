package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionarioRequestDTO {
    private Long idPermissionario;
    private String numeroPermissao;
    private String nomePermissionario;
    private String cpfPermissionario;
    private String rgPermissionario;
    private String orgaoEmissor;
    private String sexo;
    private String estadoCivil;
    private String dataNascimento;
    private String ufPermissionario;
    private String cidadePermissionario;
    private String bairroPermissionario;
    private String enderecoPermissionario;
    private String celularPermissionario;
    private String emailPermissionario;
    private String cnhPermissionario;
    private String categoriaCnhPermissionario;
    private String numeroQuitacaoMilitar;
    private String numeroQuitacaoEleitoral;
    private String numeroInscricaoInss;
    private String numeroCertificadoCondutor;
    private String dataCriacao;
    private String usuario;
    private String aplicativoAlternativo;
    private String observacao;

}
