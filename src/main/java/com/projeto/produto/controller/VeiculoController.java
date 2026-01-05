package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.service.impl.VeiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/veiculo")
public class VeiculoController {
    @Autowired
    public VeiculoServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<VeiculoResponseDTO> listarTodosVeiculos(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<VeiculoResponseDTO> veiculoResponseDTOPage = service.listarTodosVeiculos(pageRequest);
            return veiculoResponseDTOPage;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todos os Veículos cadastrados!");
        }

    }

    @GetMapping("/buscar/{idVeiculo}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoId(@PathVariable Long idVeiculo) {
        try {
            return ResponseEntity.ok(service.buscarVeiculoId(idVeiculo));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Veículo pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<VeiculoResponseDTO> buscarVeiculosFiltros(@RequestParam(required = false) String numeroPermissao,
                                                          @RequestParam(required = false) String placa,
                                                          @RequestParam(required = false) String renavam,
                                                          @RequestParam(required = false) String numeroTaximetro,
                                                          @RequestParam(required = false) String anoFabricacao,
                                                          @RequestParam(required = true) Integer pageIndex,
                                                          @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<VeiculoResponseDTO> veiculos = service.listarTodosVeiculosFiltros(
                    numeroPermissao, placa, renavam, numeroTaximetro, anoFabricacao, pageRequest
            );

            return veiculos;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar os Veículos com os filtros informados!");
        }
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
