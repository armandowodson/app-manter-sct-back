package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VeiculoResponseDTO {
    private Long idVeiculo;
    private Long idPermissionario;
    private String numeroPermissao;
    private String placa;
    private String renavam;
    private String chassi;
    private String anoFabricacao;
    private String marca;
    private String modelo;
    private String anoModelo;
    private String cor;
    private String combustivel;
    private byte[] crlv;
    private String numeroTaximetro;
    private String anoRenovacao;
    private String dataVistoria;
    private String dataRetorno;
    private byte[] comprovanteVistoria;
    private String situacaoVeiculo;
    private String numeroCrlv;
    private String anoCrlv;
    private String certificadoAfericao;
    private String observacao;
    private String dataCriacao;


}
