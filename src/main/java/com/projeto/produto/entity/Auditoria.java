package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.auditoria")
@Table(name="auditoria", schema="proj")
@SequenceGenerator(name = "seq_auditoria", schema = "proj", sequenceName = "seq_auditoria", initialValue = 10, allocationSize = 1)
@Data
public class Auditoria {
    @Id
    @Column(name = "ID_AUDITORIA")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_auditoria")
    private Long idAuditoria;
    @Column(name = "NOME_MODULO")
    private String nomeModulo;
    @Column(name = "USUARIO_OPERACAO")
    private String usuarioOperacao;
    @Column(name = "OPERACAO")
    private String operacao;
    @Column(name = "DATA_OPERACAO")
    private LocalDate dataOperacao;
}
