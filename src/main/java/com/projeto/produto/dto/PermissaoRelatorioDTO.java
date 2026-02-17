package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoRelatorioDTO {
    private String numeroPermissao;
    private String anoPermissao;
    private String categoriaPermissao;
    private String statusPermissao;
    private String periodoStatus;
    private String dataValidadePermissao;
    private String modalidade;
    private String numeroAutorizacaoTrafego;
    private String dataCriacao;
}
