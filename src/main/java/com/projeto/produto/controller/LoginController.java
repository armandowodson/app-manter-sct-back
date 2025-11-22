package com.projeto.produto.controller;

import com.projeto.produto.dto.LoginDTO;
import com.projeto.produto.dto.RegistroDTO;
import com.projeto.produto.service.impl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    public LoginServiceImpl service;

    @GetMapping("/conectar")
    public ResponseEntity<LoginDTO> efetuarLogin(@RequestParam String usuario, @RequestParam String senha) {
        return ResponseEntity.ok(service.efetuarLogin(usuario, senha));
    }

    @PostMapping("/gravar")
    public ResponseEntity<LoginDTO> gravarUsuario(@RequestBody RegistroDTO registro) {
        return ResponseEntity.ok(service.gravarUsuario(registro));
    }

}
