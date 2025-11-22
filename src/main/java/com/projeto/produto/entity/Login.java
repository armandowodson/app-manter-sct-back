package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.login")
@Table(name="login", schema="proj")
@SequenceGenerator(name = "seq_login", schema = "proj", sequenceName = "seq_login", initialValue = 10, allocationSize = 1)
@Data
public class Login {
    @Id
    @Column(name = "ID_LOGIN")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_login")
    private Long idLogin;
    @Column(name = "NOME_COMPLETO")
    private String nomeCompleto;
    @Column(name = "LOGIN_USUARIO")
    private String loginUsuario;
    @Column(name = "SENHA_USUARIO")
    private String senhaUsuario;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
}
