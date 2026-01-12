package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.DefensorRequestDTO;
import com.projeto.produto.dto.DefensorResponseDTO;
import com.projeto.produto.service.impl.DefensorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Page<DefensorResponseDTO> listarTodosDefensors(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<DefensorResponseDTO> defensorResponseDTOPage = service.listarTodosDefensors(pageRequest);
            return defensorResponseDTOPage;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todos os Defensores cadastrados!");
        }
    }

    @GetMapping("/buscar/{idDefensor}")
    public ResponseEntity<DefensorResponseDTO> buscarDefensorId(@PathVariable Long idDefensor) {
        try{
            return ResponseEntity.ok(service.buscarDefensorId(idDefensor));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Defensor pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<DefensorResponseDTO> buscarDefensorsFiltros(   @RequestParam(required = false) String numeroPermissao,
                                                               @RequestParam(required = false) String nomeDefensor,
                                                               @RequestParam(required = false) String cpfDefensor,
                                                               @RequestParam(required = false) String cnpjEmpresa,
                                                               @RequestParam(required = false) String cnhDefensor,
                                                               @RequestParam(required = true) Integer pageIndex,
                                                               @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<DefensorResponseDTO> defensores = service.listarTodosDefensorFiltros(
                    numeroPermissao, nomeDefensor, cpfDefensor, cnpjEmpresa, cnhDefensor, pageRequest
            );

            return defensores;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar os Defensores com os filtros informados!");
        }
    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<DefensorResponseDTO>> buscarDefensoresDisponiveis() {
        return ResponseEntity.ok(service.listarDefensoresDisponiveis(null));
    }

    @GetMapping("/buscar-disponiveis/{idDefensor}")
    public ResponseEntity<List<DefensorResponseDTO>> buscarDefensoresDisponiveisAlteracao(@PathVariable Long idDefensor) {
        return ResponseEntity.ok(service.listarDefensoresDisponiveis(idDefensor));
    }

    @PostMapping("/inserir")
    public ResponseEntity<DefensorResponseDTO> inserirDefensor(
            @RequestParam("defensor") String defensor,
            @RequestParam("certificadoCondutor") MultipartFile certificadoCondutor,
            @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
            @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
            @RequestParam("foto") MultipartFile foto
    ) throws IOException {
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.inserirDefensor(defensorDTO, certificadoCondutor, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<DefensorResponseDTO> atualizarDefensor(@RequestParam("defensor") String defensor,
                                                                 @RequestParam(value = "certificadoCondutor", required = false) MultipartFile certificadoCondutor,
                                                                 @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile certidaoNegativaCriminal,
                                                                 @RequestParam(value = "certidaoNegativaMunicipal", required = false) MultipartFile certidaoNegativaMunicipal,
                                                                 @RequestParam(value = "foto", required = false) MultipartFile foto) throws IOException {
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.atualizarDefensor(defensorDTO, certidaoNegativaCriminal, certificadoCondutor,
                certidaoNegativaMunicipal, foto));
    }

    @DeleteMapping("/excluir/{idDefensor}/usuario/{usuario}")
    public ResponseEntity<Void> excluirDefensor(@PathVariable Long idDefensor, @PathVariable String usuario) {
        return service.excluirDefensor(idDefensor, usuario);
    }

}
