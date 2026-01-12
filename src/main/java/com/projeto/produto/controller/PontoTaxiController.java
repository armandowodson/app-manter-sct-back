package com.projeto.produto.controller;

import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.service.impl.PontosTaxiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ponto-taxi")
public class PontoTaxiController {
    @Autowired
    public PontosTaxiServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<PontoTaxiDTO> listarTodosPontosTaxi(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<PontoTaxiDTO> pontos = service.listarTodosPontosTaxi(pageRequest);
            return pontos;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todos os Pontos de Estacionamentos de Táxi cadastrados!");
        }
    }

    @GetMapping("/buscar/{idPontoTaxi}")
    public ResponseEntity<PontoTaxiDTO> buscarPontoTaxiId(@PathVariable Long idPontoTaxi) {
        try{
            return ResponseEntity.ok(service.buscarPontoTaxiId(idPontoTaxi));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Ponto de Estacionamento de Táxi pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<PontoTaxiDTO> buscarPontosTaxiFiltros(@RequestParam(required = false) String numeroPonto,
                                                      @RequestParam(required = false) String descricaoPonto,
                                                      @RequestParam(required = false) String fatorRotatividade,
                                                      @RequestParam(required = false) String numeroVagas,
                                                      @RequestParam(required = false) String referenciaPonto,
                                                      @RequestParam(required = false) String modalidade,
                                                      @RequestParam(required = true) Integer pageIndex,
                                                      @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<PontoTaxiDTO> pontos = service.listarTodosPontosTaxiFiltros(
                    numeroPonto, descricaoPonto, fatorRotatividade,
                    numeroVagas, referenciaPonto, modalidade, pageRequest
            );

            return pontos;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar os Pontos de Estacionamentos de Táxi com os filtros informados!");
        }

    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<PontoTaxiDTO>> buscarPontosTaxiDisponiveis() {
        return ResponseEntity.ok(service.listarPontosTaxiDisponiveis());
    }

    @PostMapping("/inserir")
    public ResponseEntity<PontoTaxiDTO> inserirPontoTaxi(@RequestBody PontoTaxiDTO pontoTaxiDTO) {
        return ResponseEntity.ok(service.inserirPontoTaxi(pontoTaxiDTO));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PontoTaxiDTO> atualizarPontoTaxi(@RequestBody PontoTaxiDTO pontoTaxiDTO) {
        return ResponseEntity.ok(service.atualizarPontoTaxi(pontoTaxiDTO));
    }

    @DeleteMapping("/excluir/{idPontoTaxi}/usuario/{usuario}")
    public ResponseEntity<Void> excluirPontoTaxi(@PathVariable Long idPontoTaxi, @PathVariable String usuario) {
        return service.excluirPontoTaxi(idPontoTaxi, usuario);
    }

}
