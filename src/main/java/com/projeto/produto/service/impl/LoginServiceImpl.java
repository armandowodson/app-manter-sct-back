package com.projeto.produto.service.impl;

import com.projeto.produto.dto.LoginDTO;
import com.projeto.produto.dto.RegistroDTO;
import com.projeto.produto.entity.Login;
import com.projeto.produto.repository.LoginRepository;
import com.projeto.produto.utils.ValidaCPF;
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

    public LoginDTO efetuarLogin(String login, String senha) {
        if(Objects.nonNull(login) && !login.isEmpty() &&  !ValidaCPF.isCPF(login))
            throw new RuntimeException("O CPF " + login + " é inválido!");

        Login loginEntity = loginRepository.findLoginByLoginUsuarioAndSenhaUsuario(login, senha);
        if (loginEntity != null){
            return converterLoginToLoginDTO(loginEntity);
        }else{
            throw new RuntimeException("Usuário não identificado!");
        }
    }

    public LoginDTO gravarUsuario(RegistroDTO registro) {
        if(Objects.nonNull(registro.getUsuario()) && !registro.getUsuario().isEmpty() &&  !ValidaCPF.isCPF(registro.getUsuario()))
            throw new RuntimeException("O CPF " + registro.getUsuario() + " é inválido!");

        Login existLogin = this.loginRepository.findLoginByLoginUsuario(registro.getUsuario());
        if(Objects.nonNull(existLogin))
            throw new RuntimeException("Já existe usuário para o CPF " + registro.getUsuario() + "!");

        Login login = new Login();
        try{
            login.setNomeCompleto(registro.getNome().toUpperCase());
            login.setLoginUsuario(registro.getUsuario());
            login.setSenhaUsuario(registro.getSenha());
            login.setDataCriacao(LocalDate.now());
            login = loginRepository.save(login);

        } catch (Exception e){
            throw new RuntimeException("Não foi possível gravar o Usuário!");
        }
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
