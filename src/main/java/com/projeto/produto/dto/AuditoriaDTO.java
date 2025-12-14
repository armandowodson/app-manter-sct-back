package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditoriaDTO {
    private Long idAuditoria;
    private String nomeModulo;
    private String usuarioOperacao;
    private String operacao;
    private String dataOperacao;
}
