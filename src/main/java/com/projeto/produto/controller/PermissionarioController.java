package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.service.impl.PermissionarioServiceImpl;
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
import java.util.List;

@RestController
@RequestMapping("/permissionario")
public class PermissionarioController {
    @Autowired
    public PermissionarioServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<PermissionarioResponseDTO> listarTodosPermissionarios(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try {
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<PermissionarioResponseDTO> permissionarios = service.listarTodosPermissionarios(pageRequest);
            return permissionarios;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todos os Permissionários cadastrados!");
        }
    }

    @GetMapping("/buscar/{idPermissionario}")
    public ResponseEntity<PermissionarioResponseDTO> buscarPermissionarioId(@PathVariable Long idPermissionario) {
        try{
            return ResponseEntity.ok(service.buscarPermissionarioId(idPermissionario));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Permissionário pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<PermissionarioResponseDTO> buscarPermissionariosFiltros(@RequestParam(required = false) String numeroPermissao,
                                                                        @RequestParam(required = false) String nomePermissionario,
                                                                        @RequestParam(required = false) String cpfPermissionario,
                                                                        @RequestParam(required = false) String cnhPermissionario,
                                                                        @RequestParam(required = true) Integer pageIndex,
                                                                        @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<PermissionarioResponseDTO> permissionarios = service.listarTodosPermissionarioFiltros(
                    numeroPermissao, nomePermissionario, cpfPermissionario, cnhPermissionario, pageRequest
            );

            return permissionarios;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar os Permissionários com os filtros informados!");
        }
    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosDisponiveis() {
        return ResponseEntity.ok(service.listarPermissionariosDisponiveis(null));
    }

    @GetMapping("/buscar-disponiveis/{idPermissionario}")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosDisponiveisAlteracao(@PathVariable Long idPermissionario) {
        return ResponseEntity.ok(service.listarPermissionariosDisponiveis(idPermissionario));
    }

    @PostMapping("/inserir")
    public ResponseEntity<PermissionarioResponseDTO> inserirPermissionario(
            @RequestParam("permissionario") String permissionario,
            @RequestParam("certificadoCondutor") MultipartFile certificadoCondutor,
            @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
            @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
            @RequestParam("foto") MultipartFile foto
    ) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.inserirPermissionario(permissionarioDTO, certidaoNegativaCriminal, certificadoCondutor,
                certidaoNegativaMunicipal, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PermissionarioResponseDTO> atualizarPermissionario(@RequestParam("permissionario") String permissionario,
                                                                             @RequestParam(value = "certificadoCondutor", required = false) MultipartFile certificadoCondutor,
                                                                             @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile certidaoNegativaCriminal,
                                                                             @RequestParam(value = "certidaoNegativaMunicipal", required = false) MultipartFile certidaoNegativaMunicipal,
                                                                             @RequestParam(value = "foto", required = false) MultipartFile foto) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.atualizarPermissionario(permissionarioDTO, certificadoCondutor, certidaoNegativaCriminal,
                certidaoNegativaMunicipal, foto));
    }

    @DeleteMapping("/excluir/{idPermissionario}/usuario/{usuario}")
    public ResponseEntity<Void> excluirPermissionario(@PathVariable Long idPermissionario, @PathVariable String usuario) {
        return service.excluirPermissionario(idPermissionario, usuario);
    }

    @GetMapping("/gerar-registro-condutor")
    public ResponseEntity<byte[]> gerarRegistroCondutor( @RequestParam(required = true) String numeroPermissao) {
        try{
            byte[] fileBytes = service.gerarRegistroCondutor(numeroPermissao);

            String fileName = "registroCondutor-" + LocalDate.now() + "Nº" + numeroPermissao + ".pdf";

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

        return null;
    }

}
