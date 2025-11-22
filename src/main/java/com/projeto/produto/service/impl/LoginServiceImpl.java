package com.projeto.produto.service.impl;

import com.projeto.produto.dto.LoginDTO;
import com.projeto.produto.dto.RegistroDTO;
import com.projeto.produto.entity.Login;
import com.projeto.produto.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;


@Service
public class LoginServiceImpl {
    @Autowired
    private LoginRepository loginRepository;

    public LoginDTO efetuarLogin(String login, String senha) {
        Login loginEntity = loginRepository.findLoginByLoginUsuarioAndSenhaUsuario(login, senha);
        if (loginEntity != null){
            return converterLoginToLoginDTO(loginEntity);
        }else{
            return null;
        }
    }

    public LoginDTO gravarUsuario(RegistroDTO registro){
        if (Objects.isNull(registro.getUsuario()) || Objects.isNull(registro.getSenha()) ||
                Objects.isNull(registro.getNome())) {
            throw new RuntimeException("Dados inválidos para o Usuário!");
        }
        Login login = new Login();
        login.setNomeCompleto(registro.getNome());
        login.setLoginUsuario(registro.getUsuario());
        login.setSenhaUsuario(registro.getSenha());
        login.setDataCriacao(LocalDate.now());
        login = loginRepository.save(login);
        return converterLoginToLoginDTO(login);
    }

    public LoginDTO converterLoginToLoginDTO(Login login){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario(login.getLoginUsuario());
        loginDTO.setSenha("");

        return  loginDTO;
    }

}
