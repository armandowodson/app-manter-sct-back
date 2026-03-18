package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.pontos_taxi")
@Table(name="pontos_taxi", schema="proj")
@Data
public class PontoTaxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PONTOS_TAXI", nullable = false)
    private Integer idPontoTaxi;
    @Column(name = "NUMERO_PONTO")
    private String numeroPonto;
    @Column(name = "DESCRICAO_PONTO")
    private String descricaoPonto;
    @Column(name = "FATOR_ROTATIVIDADE")
    private String fatorRotatividade;
    @Column(name = "REFERENCIA_PONTO")
    private String referenciaPonto;
    @Column(name = "NUMERO_VAGAS")
    private String numeroVagas;
    @Column(name = "MODALIDADE")
    private String modalidade;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
    @Column(name = "STATUS")
    private String status;
}
