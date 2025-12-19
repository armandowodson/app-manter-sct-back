package com.projeto.produto.controller;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.service.impl.AuditoriaServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
    @Autowired
    public AuditoriaServiceImpl service;

    @GetMapping("/buscar-todas")
    public ResponseEntity<List<AuditoriaDTO>> listarTodosAuditoria() {
        return ResponseEntity.ok(service.listarTodosAuditoria());
    }

    @GetMapping("/buscar/{idAuditoria}")
    public ResponseEntity<AuditoriaDTO> buscarAuditoriaId(@PathVariable Long idAuditoria) {
        return ResponseEntity.ok(service.buscarAuditoriaId(idAuditoria));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<AuditoriaDTO>> buscarAuditoriaFiltros( @RequestParam(required = false) String nomeModulo,
                                                                      @RequestParam(required = false) String usuarioOperacao,
                                                                      @RequestParam(required = false) String operacao,
                                                                      @RequestParam(required = false) String dataInicioOperacao,
                                                                      @RequestParam(required = false) String dataFimOperacao) {
        return ResponseEntity.ok(service.listarTodosAuditoriaFiltros(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao));
    }

    @GetMapping("/imprimir")
    public ResponseEntity<List<AuditoriaDTO>> imprimirAuditoria( @RequestParam(required = false) String nomeModulo,
                                                                 @RequestParam(required = false) String usuarioOperacao,
                                                                 @RequestParam(required = false) String operacao,
                                                                 @RequestParam(required = false) String dataInicioOperacao,
                                                                 @RequestParam(required = false) String dataFimOperacao) throws JRException, SQLException, IOException {
        return ResponseEntity.ok(service.imprimirAuditoria(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao));
    }

    @GetMapping("/gerar-relatorio")
    public void gerarRelatorio() throws JRException, SQLException, IOException {
        service.gerarRelatorio(null);
    }

}
