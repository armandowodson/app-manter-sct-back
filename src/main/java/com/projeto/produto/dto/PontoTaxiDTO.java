package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PontoTaxiDTO {
    private Long idPontosTaxi;
    private String numeroPonto;
    private String descricaoPonto;
    private String fatorRotatividade;
    private String referenciaPonto;
    private String dataCriacao;
}
