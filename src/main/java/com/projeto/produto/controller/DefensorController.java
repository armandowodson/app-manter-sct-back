package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.DefensorRequestDTO;
import com.projeto.produto.dto.DefensorResponseDTO;
import com.projeto.produto.service.impl.DefensorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/defensor")
public class DefensorController {
    @Autowired
    public DefensorServiceImpl service;

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<DefensorResponseDTO>> listarTodosDefensors() {
        return ResponseEntity.ok(service.listarTodosDefensors());
    }

    @GetMapping("/buscar/{idDefensor}")
    public ResponseEntity<DefensorResponseDTO> buscarDefensorId(@PathVariable Long idDefensor) {
        return ResponseEntity.ok(service.buscarDefensorId(idDefensor));
    }

    @GetMapping("/buscar-filtros")
    public ResponseEntity<List<DefensorResponseDTO>> buscarDefensorsFiltros(@RequestParam(required = false) String numeroPermissao,
                                                                                       @RequestParam(required = false) String nomeDefensor,
                                                                                       @RequestParam(required = false) String cpfDefensor,
                                                                                       @RequestParam(required = false) String cnpjEmpresa,
                                                                                       @RequestParam(required = false) String cnhDefensor) {
        return ResponseEntity.ok(service.listarTodosDefensorFiltros(
                numeroPermissao, nomeDefensor, cpfDefensor, cnpjEmpresa, cnhDefensor
        ));
    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<DefensorResponseDTO>> buscarDefensorsDisponiveis() {
        return ResponseEntity.ok(service.listarDefensorsDisponiveis(null));
    }

    @GetMapping("/buscar-disponiveis/{idDefensor}")
    public ResponseEntity<List<DefensorResponseDTO>> buscarDefensorsDisponiveisAlteracao(@PathVariable Long idDefensor) {
        return ResponseEntity.ok(service.listarDefensorsDisponiveis(idDefensor));
    }

    @PostMapping("/inserir")
    public ResponseEntity<DefensorResponseDTO> inserirDefensor(
            @RequestParam("defensor") String defensor,
            @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
            @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
            @RequestParam("foto") MultipartFile foto
    ) throws IOException {
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.inserirDefensor(defensorDTO, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<DefensorResponseDTO> atualizarDefensor(@RequestParam("defensor") String defensor,
                                                                             @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile certidaoNegativaCriminal,
                                                                             @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile certidaoNegativaMunicipal,
                                                                             @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile foto) throws IOException {
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.atualizarDefensor(defensorDTO, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @DeleteMapping("/excluir/{idDefensor}/usuario/{usuario}")
    public ResponseEntity<Void> excluirDefensor(@PathVariable Long idDefensor, @PathVariable String usuario) {
        return service.excluirDefensor(idDefensor, usuario);
    }

}
