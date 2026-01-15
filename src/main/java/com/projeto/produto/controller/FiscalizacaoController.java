package com.projeto.produto.controller;

import com.projeto.produto.dto.FiscalizacaoDTO;
import com.projeto.produto.service.impl.FiscalizacaoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/fiscalizacao")
public class FiscalizacaoController {
    @Autowired
    public FiscalizacaoServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<FiscalizacaoDTO> listarTodasFiscalizacoes(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<FiscalizacaoDTO> fiscalizacaoResponseDTOPage = service.listarTodasFiscalizacoes(pageRequest);
            return fiscalizacaoResponseDTOPage;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todas as Fiscalizações cadastradas!");
        }
    }

    @GetMapping("/buscar/{idFiscalizacao}")
    public ResponseEntity<FiscalizacaoDTO> buscarFiscalizacaoId(@PathVariable Long idFiscalizacao) {
        try{
            return ResponseEntity.ok(service.buscarFiscalizacaoId(idFiscalizacao));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar a Fiscalização pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<FiscalizacaoDTO> buscarFiscalizacoesFiltros(   @RequestParam(required = false) String placa,
                                                               @RequestParam(required = false) String nomePermissionario,
                                                               @RequestParam(required = false) String dataFiscalizacao,
                                                               @RequestParam(required = false) String motivoInfracao,
                                                               @RequestParam(required = false) String penalidade,
                                                               @RequestParam(required = true) Integer pageIndex,
                                                               @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<FiscalizacaoDTO> fiscalizacaoes = service.listarTodosFiscalizacaoFiltros(
                    placa, nomePermissionario, dataFiscalizacao, motivoInfracao, penalidade, pageRequest
            );

            return fiscalizacaoes;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar as Fiscalizações com os filtros informados!");
        }
    }

    @PostMapping("/inserir")
    public ResponseEntity<FiscalizacaoDTO> inserirFiscalizacao(@RequestBody FiscalizacaoDTO fiscalizacaoDTO) throws IOException {
        return ResponseEntity.ok(service.inserirFiscalizacao(fiscalizacaoDTO));
    }

    @PostMapping("/alterar")
    public ResponseEntity<FiscalizacaoDTO> atualizarFiscalizacao(@RequestBody FiscalizacaoDTO fiscalizacaoDTO) throws IOException {
        return ResponseEntity.ok(service.atualizarFiscalizacao(fiscalizacaoDTO));
    }

    @DeleteMapping("/excluir/{idFiscalizacao}/usuario/{usuario}")
    public ResponseEntity<Void> excluirFiscalizacao(@PathVariable Long idFiscalizacao, @PathVariable String usuario) {
        return service.excluirFiscalizacao(idFiscalizacao, usuario);
    }

}
