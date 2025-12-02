package com.projeto.produto.controller;

import com.projeto.produto.dto.VeiculoDTO;
import com.projeto.produto.service.impl.VeiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculo")
public class VeiculoController {
    @Autowired
    public VeiculoServiceImpl service;

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<VeiculoDTO>> listarTodosVeiculos() {
        return ResponseEntity.ok(service.listarTodosVeiculos());
    }

    @GetMapping("/buscar/{idVeiculo}")
    public ResponseEntity<VeiculoDTO> buscarVeiculoId(@PathVariable Long idVeiculo) {
        return ResponseEntity.ok(service.buscarVeiculoId(idVeiculo));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<VeiculoDTO>> buscarVeiculosFiltros(@RequestParam(required = false) String numeroPermissao,
                                                                      @RequestParam(required = false) String placa,
                                                                      @RequestParam(required = false) String renavam,
                                                                      @RequestParam(required = false) String numeroTaximetro,
                                                                      @RequestParam(required = false) String anoFabricacao) {
        return ResponseEntity.ok(service.listarTodosVeiculosFiltros(numeroPermissao, placa, renavam, numeroTaximetro, anoFabricacao));
    }

    @PostMapping("/inserir")
    public ResponseEntity<VeiculoDTO> inserirVeiculo(@RequestBody VeiculoDTO veiculoDTO) {
        return ResponseEntity.ok(service.inserirVeiculo(veiculoDTO));
    }

    @PostMapping("/alterar")
    public ResponseEntity<VeiculoDTO> atualizarVeiculo(@RequestBody VeiculoDTO veiculoDTO) {
        return ResponseEntity.ok(service.atualizarVeiculo(veiculoDTO));
    }

    @DeleteMapping("/excluir/{idVeiculo}")
    public ResponseEntity<Void> excluirVeiculo(@PathVariable Long idVeiculo) {
        return service.excluirVeiculo(idVeiculo);
    }

}
