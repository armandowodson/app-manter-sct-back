package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiscalizacaoDTO {
    private Long idFiscalizacao;
    private String dataFiscalizacao;
    private Long idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private String cor;
    private String idPermissionario;
    private String numeroPermissao;
    private String nomePermissionario;
    private String cnhPermissionario;
    private String motivoInfracao;
    private String tipoInfracao;
    private String grupoMultas;
    private String prazoRegularizacao;
    private String naturezaInfracao;
    private String modalidade;
    private String penalidade;
    private String observacao;
    private String dataCriacao;
    private String usuario;
}
