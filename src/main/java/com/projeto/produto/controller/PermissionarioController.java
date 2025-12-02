package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.service.impl.PermissionarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/permissionario")
public class PermissionarioController {
    @Autowired
    public PermissionarioServiceImpl service;

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<PermissionarioResponseDTO>> listarTodosPermissionarios() {
        return ResponseEntity.ok(service.listarTodosPermissionarios());
    }

    @GetMapping("/buscar/{idPermissionario}")
    public ResponseEntity<PermissionarioResponseDTO> buscarPermissionarioId(@PathVariable Long idPermissionario) {
        return ResponseEntity.ok(service.buscarPermissionarioId(idPermissionario));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosFiltros(@RequestParam(required = false) String numeroPermissao,
                                                                                       @RequestParam(required = false) String placa,
                                                                                       @RequestParam(required = false) String renavam,
                                                                                       @RequestParam(required = false) String numeroTaximetro,
                                                                                       @RequestParam(required = false) String anoRenovacao) {
        return ResponseEntity.ok(service.listarTodosPermissionarioFiltros(numeroPermissao, placa, renavam, numeroTaximetro, anoRenovacao));
    }
//
    @PostMapping("/inserir")
    public ResponseEntity<PermissionarioResponseDTO> inserirPermissionario(
            @RequestParam("permissionario") String permissionario,
            @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
            @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
            @RequestParam("foto") MultipartFile foto
    ) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.inserirPermissionario(permissionarioDTO, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PermissionarioResponseDTO> atualizarPermissionario(@RequestParam("permissionario") String permissionario,
                                                                             @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
                                                                             @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
                                                                             @RequestParam("foto") MultipartFile foto) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.atualizarPermissionario(permissionarioDTO, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @DeleteMapping("/excluir/{idPermissionario}")
    public ResponseEntity<Void> excluirPermissionario(@PathVariable Long idPermissionario) {
        return service.excluirPermissionario(idPermissionario);
    }

}
