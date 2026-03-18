package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.login")
@Table(name="login", schema="proj")
@Data
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOGIN", nullable = false)
    private Integer idLogin;
    @Column(name = "NOME_COMPLETO")
    private String nomeCompleto;
    @Column(name = "LOGIN_USUARIO")
    private String loginUsuario;
    @Column(name = "SENHA_USUARIO")
    private String senhaUsuario;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
}
