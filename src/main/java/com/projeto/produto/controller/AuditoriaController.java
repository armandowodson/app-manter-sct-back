package com.projeto.produto.controller;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.service.impl.AuditoriaServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<List<AuditoriaDTO>> imprimirAuditoria( @RequestParam(required = false) String nomeModulo,
                                                                 @RequestParam(required = false) String usuarioOperacao,
                                                                 @RequestParam(required = false) String operacao,
                                                                 @RequestParam(required = false) String dataInicioOperacao,
                                                                 @RequestParam(required = false) String dataFimOperacao,
                                                                 @RequestParam(required = true) Integer pageIndex,
                                                                 @RequestParam(required = true) Integer pageSize) throws JRException, SQLException, IOException {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        return ResponseEntity.ok(service.imprimirAuditoria(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao, pageRequest));
    }

    @GetMapping("/gerar-relatorio")
    public void gerarRelatorio() throws JRException, SQLException, IOException {
        service.gerarRelatorio(null);
    }

}
