package com.projeto.produto.controller;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.service.impl.PontosTaxiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ponto-taxi")
public class PontoTaxiController {
    @Autowired
    public PontosTaxiServiceImpl service;

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<PontoTaxiDTO>> listarTodosPontosTaxi() {
        return ResponseEntity.ok(service.listarTodosPontosTaxi());
    }

    @GetMapping("/buscar/{idPontoTaxi}")
    public ResponseEntity<PontoTaxiDTO> buscarPontoTaxiId(@PathVariable Long idPontoTaxi) {
        return ResponseEntity.ok(service.buscarPontoTaxiId(idPontoTaxi));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<PontoTaxiDTO>> buscarPontosTaxiFiltros(@RequestParam(required = false) String numeroPonto,
                                                                      @RequestParam(required = false) String descricaoPonto,
                                                                      @RequestParam(required = false) String fatorRotatividade,
                                                                      @RequestParam(required = false) String numeroVagas,
                                                                      @RequestParam(required = false) String referenciaPonto) {
        return ResponseEntity.ok(service.listarTodosPontosTaxiFiltros(numeroPonto, descricaoPonto, fatorRotatividade, numeroVagas, referenciaPonto));
    }

    @PostMapping("/inserir")
    public ResponseEntity<PontoTaxiDTO> inserirPontoTaxi(@RequestBody PontoTaxiDTO pontoTaxiDTO) {
        return ResponseEntity.ok(service.inserirPontoTaxi(pontoTaxiDTO));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PontoTaxiDTO> atualizarPontoTaxi(@RequestBody PontoTaxiDTO pontoTaxiDTO) {
        return ResponseEntity.ok(service.atualizarPontoTaxi(pontoTaxiDTO));
    }

    @DeleteMapping("/excluir/{idPontoTaxi}")
    public ResponseEntity<Void> excluirPontoTaxi(@PathVariable Long idPontoTaxi) {
        return service.excluirPontoTaxi(idPontoTaxi);
    }

}
