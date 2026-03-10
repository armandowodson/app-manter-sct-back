package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.VistoriaRequestDTO;
import com.projeto.produto.dto.VistoriaResponseDTO;
import com.projeto.produto.service.impl.VistoriaServiceImpl;
import com.projeto.produto.service.impl.VistoriaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/vistoria")
public class VistoriaController {
    @Autowired
    public VistoriaServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<VistoriaResponseDTO> listarTodasVistorias(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<VistoriaResponseDTO> vistoriaResponseDTOPage = service.listarTodasVistorias(pageRequest);
            return vistoriaResponseDTOPage;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todos as Vistorias cadastradas!");
        }
    }

    @GetMapping("/buscar/{idVistoria}")
    public ResponseEntity<VistoriaResponseDTO> buscarVistoriaId(@PathVariable Long idVistoria) {
        try {
            return ResponseEntity.ok(service.buscarVistoriaId(idVistoria));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar a Vistoria pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<VistoriaResponseDTO> buscarVistoriasFiltros(@RequestParam(required = false) String numeroPermissao,
                                                          @RequestParam(required = false) String placa,
                                                          @RequestParam(required = false) String statusVistoria,
                                                          @RequestParam(required = true) Integer pageIndex,
                                                          @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<VistoriaResponseDTO> vistorias = service.listarTodasVistoriasFiltros(
                    numeroPermissao, placa, statusVistoria, pageRequest
            );

            return vistorias;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar as Vistorias com os filtros informados!");
        }
    }

    @PostMapping("/inserir")
    public ResponseEntity<VistoriaResponseDTO> inserirVistoria(@RequestParam("vistoria") String vistoria,
                                                             @RequestParam("comprovanteVistoria") MultipartFile comprovanteVistoria
    ) throws IOException {
        VistoriaRequestDTO vistoriaRequestDTO = new ObjectMapper().readValue(vistoria, VistoriaRequestDTO.class);
        return ResponseEntity.ok(service.inserirVistoria(vistoriaRequestDTO, comprovanteVistoria));
    }

    @PostMapping("/alterar")
    public ResponseEntity<VistoriaResponseDTO> atualizarVistoria(@RequestParam("vistoria") String vistoria,
                                                               @RequestParam(value = "comprovanteVistoria", required = false) MultipartFile comprovanteVistoria
    ) throws IOException {
        VistoriaRequestDTO vistoriaRequestDTO = new ObjectMapper().readValue(vistoria, VistoriaRequestDTO.class);
        return ResponseEntity.ok(service.atualizarVistoria(vistoriaRequestDTO, comprovanteVistoria));
    }

    @DeleteMapping("/excluir/{idVistoria}/usuario/{usuario}")
    public ResponseEntity<Void> excluirVistoria(@PathVariable Long idVistoria, @PathVariable String usuario) {
        return service.excluirVistoria(idVistoria, usuario);
    }

    @GetMapping("/gerar-laudo-vistoria")
    public ResponseEntity<byte[]> gerarLaudoVistoria( @RequestParam(required = true) String idVistoria,
                                                      @RequestParam(required = true) String idVeiculo,
                                                      @RequestParam(required = true) String modulo) {
        try{
            byte[] fileBytes = service.gerarLaudoVistoria(idVistoria, idVeiculo, modulo);

            String fileName = "laudoVistoria-" + LocalDate.now() + "Nº" + idVistoria + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(fileBytes.length);

            return ResponseEntity.ok().headers(headers).body(fileBytes);
        } catch (Exception e){
            if(e.getMessage().equals("400"))
                return ResponseEntity.status(400).body(null);
            if(e.getMessage().equals("401"))
                return ResponseEntity.status(401).body(null);
            if(e.getMessage().equals("402"))
                return ResponseEntity.status(402).body(null);
            if(e.getMessage().equals("403"))
                return ResponseEntity.status(403).body(null);
            if(e.getMessage().equals("500"))
                return ResponseEntity.status(500).body(null);
        }
        return  null;
    }

}
