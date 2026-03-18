package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.auditoria")
@Table(name="auditoria", schema="proj")
@Data
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDITORIA", nullable = false)
    private Integer idAuditoria;
    @Column(name = "NOME_MODULO")
    private String nomeModulo;
    @Column(name = "USUARIO_OPERACAO")
    private String usuarioOperacao;
    @Column(name = "OPERACAO")
    private String operacao;
    @Column(name = "DATA_OPERACAO")
    private LocalDate dataOperacao;
}
