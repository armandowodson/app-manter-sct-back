package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.service.impl.VeiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/veiculo")
public class VeiculoController {
    @Autowired
    public VeiculoServiceImpl service;

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<VeiculoResponseDTO>> listarTodosVeiculos() {
        return ResponseEntity.ok(service.listarTodosVeiculos());
    }

    @GetMapping("/buscar/{idVeiculo}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoId(@PathVariable Long idVeiculo) {
        return ResponseEntity.ok(service.buscarVeiculoId(idVeiculo));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<VeiculoResponseDTO>> buscarVeiculosFiltros(@RequestParam(required = false) String numeroPermissao,
                                                                          @RequestParam(required = false) String placa,
                                                                          @RequestParam(required = false) String renavam,
                                                                          @RequestParam(required = false) String numeroTaximetro,
                                                                          @RequestParam(required = false) String anoFabricacao) {
        return ResponseEntity.ok(service.listarTodosVeiculosFiltros(numeroPermissao, placa, renavam, numeroTaximetro, anoFabricacao));
    }

    @PostMapping("/inserir")
    public ResponseEntity<VeiculoResponseDTO> inserirVeiculo(@RequestParam("veiculo") String veiculo,
                                                             @RequestParam("crlv") MultipartFile crlv,
                                                             @RequestParam("comprovanteVistoria") MultipartFile comprovanteVistoria
    ) throws IOException {
        VeiculoRequestDTO veiculoRequestDTO = new ObjectMapper().readValue(veiculo, VeiculoRequestDTO.class);
        return ResponseEntity.ok(service.inserirVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria));
    }

    @PostMapping("/alterar")
    public ResponseEntity<VeiculoResponseDTO> atualizarVeiculo(@RequestParam("veiculo") String veiculo,
                                                               @RequestParam(value = "crlv", required = false) MultipartFile crlv,
                                                               @RequestParam(value = "comprovanteVistoria", required = false) MultipartFile comprovanteVistoria
    ) throws IOException {
        VeiculoRequestDTO veiculoRequestDTO = new ObjectMapper().readValue(veiculo, VeiculoRequestDTO.class);
        return ResponseEntity.ok(service.atualizarVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria));
    }

    @DeleteMapping("/excluir/{idVeiculo}/usuario/{usuario}")
    public ResponseEntity<Void> excluirVeiculo(@PathVariable Long idVeiculo, @PathVariable String usuario) {
        return service.excluirVeiculo(idVeiculo, usuario);
    }

}
