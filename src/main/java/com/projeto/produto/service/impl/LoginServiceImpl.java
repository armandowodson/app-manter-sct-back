package com.projeto.produto.service.impl;

import com.projeto.produto.dto.LoginDTO;
import com.projeto.produto.dto.RegistroDTO;
import com.projeto.produto.entity.Login;
import com.projeto.produto.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Objects;


@Service
public class LoginServiceImpl {
    @Autowired
    private LoginRepository loginRepository;

    public LoginDTO efetuarLogin(String login, String senha) throws NoSuchAlgorithmException {
        if (Objects.isNull(login) || Objects.isNull(senha) || login.isEmpty() || senha.isEmpty()) {
            throw new RuntimeException("Dados inválidos para o Login!");
        }

        Login loginEntity = loginRepository.findLoginByLoginUsuarioAndSenhaUsuario(login, hash(senha));
        if (loginEntity != null){
            return converterLoginToLoginDTO(loginEntity);
        }else{
            return null;
        }
    }

    public LoginDTO gravarUsuario(RegistroDTO registro) throws NoSuchAlgorithmException {
        if (Objects.isNull(registro.getUsuario()) || Objects.isNull(registro.getSenha()) ||
                Objects.isNull(registro.getNome()) || registro.getUsuario().isEmpty() ||
                registro.getSenha().isEmpty() || registro.getNome().isEmpty()) {
            throw new RuntimeException("Dados inválidos para o Usuário!");
        }

        Login login = new Login();
        login.setNomeCompleto(registro.getNome().toUpperCase());
        login.setLoginUsuario(registro.getUsuario());
        login.setSenhaUsuario(hash(registro.getSenha()));
        login.setDataCriacao(LocalDate.now());
        login = loginRepository.save(login);
        return converterLoginToLoginDTO(login);
    }

    public LoginDTO converterLoginToLoginDTO(Login login){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario(login.getLoginUsuario());
        loginDTO.setSenha("");
        loginDTO.setNome(login.getNomeCompleto());

        return  loginDTO;
    }

    public static String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b)); // Format as hex string
        }
        return sb.toString();
    }

}
