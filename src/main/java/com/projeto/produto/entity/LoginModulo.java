package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.login_modulo")
@Table(name="login_modulo", schema="proj")
@Data
public class LoginModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOGIN_MODULO", nullable = false)
    private Integer idLoginModulo;
    @Column(name = "ID_LOGIN")
    private Integer idLogin;
    @Column(name = "NUMERO_MODULO")
    private Integer numeroModulo;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
}
