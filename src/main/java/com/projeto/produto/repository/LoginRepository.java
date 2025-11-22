package com.projeto.produto.repository;

import com.projeto.produto.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Integer> {
    Login findLoginByLoginUsuarioAndSenhaUsuario(String login, String senha);
}

