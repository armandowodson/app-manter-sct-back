package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoDTO {
    private Long idPermissao;
    private String numeroPermissao;
    private String numeroAlvara;
    private String anoAlvara;
    private String categoriaPermissao;
    private String statusPermissao;
    private String periodoInicialStatus;
    private String periodoFinalStatus;
    private String dataValidadePermissao;
    private String dataValidadePermissaoOriginal;
    private String penalidade;
    private String dataValidadePenalidade;
    private String modalidade;
    private String autorizacaoTrafego;
    private String usuario;
    private String dataCriacao;
    private String status;
}
