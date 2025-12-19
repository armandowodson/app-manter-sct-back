package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PontoTaxiDTO {
    private Long idPontoTaxi;
    private String numeroPonto;
    private String descricaoPonto;
    private String fatorRotatividade;
    private String referenciaPonto;
    private String numeroVagas;
    private String modalidade;
    private String dataCriacao;
    private String usuario;
}
