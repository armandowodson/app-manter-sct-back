package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.pontos_taxi")
@Table(name="pontos_taxi", schema="proj")
@SequenceGenerator(name = "seq_pontos_taxi", schema = "proj", sequenceName = "seq_pontos_taxi", initialValue = 10, allocationSize = 1)
@Data
public class PontoTaxi {
    @Id
    @Column(name = "ID_PONTOS_TAXI")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pontos_taxi")
    private Long idPontoTaxi;
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
}
