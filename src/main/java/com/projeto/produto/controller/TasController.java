package com.projeto.produto.controller;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.dto.TasDTO;
import com.projeto.produto.service.impl.AuditoriaServiceImpl;
import com.projeto.produto.service.impl.TasServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Random;

@RestController
@RequestMapping("/tas")
public class TasController {
    @Autowired
    public TasServiceImpl service;

    @GetMapping("/buscar-todas")
    public Page<TasDTO> listarTodosTas(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<TasDTO> listaTas = service.listarTodosTas(pageRequest);
        return listaTas;
    }


    @GetMapping("/buscar-filtros")
    public Page<TasDTO> buscarTasFiltros( @RequestParam(required = false) String operacao,
                                          @RequestParam(required = false) String qtdDias,
                                          @RequestParam(required = false) String inicioValidadeTas,
                                          @RequestParam(required = false) String fimValidadeTas,
                                          @RequestParam(required = true) Integer pageIndex,
                                          @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<TasDTO> listaTas = service.listarTodosTasFiltros(operacao, qtdDias, inicioValidadeTas, fimValidadeTas, pageRequest);
        return listaTas;
    }

    @GetMapping("/imprimir")
    public ResponseEntity<byte[]> imprimirRelatorio( @RequestParam(required = false) String operacao,
                                               @RequestParam(required = false) String qtdDias,
                                               @RequestParam(required = false) String inicioValidadeTas,
                                               @RequestParam(required = false) String fimValidadeTas,
                                               @RequestParam(required = true) Integer pageIndex,
                                               @RequestParam(required = true) Integer pageSize)
    {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        byte[] fileBytes = service.imprimirRelatorio(operacao, qtdDias, inicioValidadeTas, fimValidadeTas, pageRequest);

        Random gerador = new Random();
        Integer numAleatorio = gerador.nextInt(100);
        String fileName = "tas-" + LocalDate.now() + ":" + numAleatorio + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(fileBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

}
