package com.projeto.produto.controller;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.service.impl.AuditoriaServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
    @Autowired
    public AuditoriaServiceImpl service;

    @GetMapping("/buscar-todas")
    public Page<AuditoriaDTO> listarTodosAuditoria(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<AuditoriaDTO> auditorias = service.listarTodosAuditoria(pageRequest);
        return auditorias;
    }

    @GetMapping("/buscar/{idAuditoria}")
    public ResponseEntity<AuditoriaDTO> buscarAuditoriaId(@PathVariable Long idAuditoria) {
        return ResponseEntity.ok(service.buscarAuditoriaId(idAuditoria));
    }

    @GetMapping("/buscar-filtros")
    public Page<AuditoriaDTO> buscarAuditoriaFiltros( @RequestParam(required = false) String nomeModulo,
                                                                      @RequestParam(required = false) String usuarioOperacao,
                                                                      @RequestParam(required = false) String operacao,
                                                                      @RequestParam(required = false) String dataInicioOperacao,
                                                                      @RequestParam(required = false) String dataFimOperacao,
                                                                      @RequestParam(required = true) Integer pageIndex,
                                                                      @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<AuditoriaDTO> auditorias = service.listarTodosAuditoriaFiltros(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao, pageRequest);
        return auditorias;
    }

    @GetMapping("/imprimir")
    public ResponseEntity<byte[]> imprimirAuditoria( @RequestParam(required = false) String nomeModulo,
                                                     @RequestParam(required = false) String usuarioOperacao,
                                                     @RequestParam(required = false) String operacao,
                                                     @RequestParam(required = false) String dataInicioOperacao,
                                                     @RequestParam(required = false) String dataFimOperacao)
            throws JRException, SQLException, IOException {
        PageRequest pageRequest = PageRequest.of(0, 10);
        byte[] fileBytes = service.imprimirAuditoria(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao, pageRequest);

        Random gerador = new Random();
        Integer numAleatorio = gerador.nextInt(100);
        String fileName = "auditoria-" + LocalDate.now() + ":" + numAleatorio + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(fileBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

}
