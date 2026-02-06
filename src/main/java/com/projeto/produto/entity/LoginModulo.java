package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.login_modulo")
@Table(name="login_modulo", schema="proj")
@SequenceGenerator(name = "seq_login_modulo", schema = "proj", sequenceName = "seq_login_modulo", initialValue = 2, allocationSize = 1)
@Data
public class LoginModulo {
    @Id
    @Column(name = "ID_LOGIN_MODULO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_login")
    private Long idLoginModulo;
    @Column(name = "ID_LOGIN")
    private Long idLogin;
    @Column(name = "NUMERO_MODULO")
    private Integer numeroModulo;
    @Column(name = "DATA_CRIACAO")
    private LocalDate dataCriacao;
}
