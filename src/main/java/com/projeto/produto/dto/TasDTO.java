package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TasDTO {
    private String numeroTas;
    private String nomeAutorizatario;
    private String cpfAutorizatario;
    private String placa;
    private String renavam;
    private String inicioValidadeTas;
    private String fimValidadeTas;
    private String qtdDiasVencer;
    private String vencido;
}
