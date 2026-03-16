package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
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
            throw new RuntimeException("Não foi possível consultar todos os Autorizatários cadastrados!");
        }
    }

    @GetMapping("/buscar/{idPermissionario}")
    public ResponseEntity<PermissionarioResponseDTO> buscarPermissionarioId(@PathVariable Long idPermissionario) {
        try{
            return ResponseEntity.ok(service.buscarPermissionarioId(idPermissionario));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Autorizatário pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<PermissionarioResponseDTO> buscarPermissionariosFiltros(@RequestParam(required = false) String nomePermissionario,
                                                                        @RequestParam(required = false) String cpfPermissionario,
                                                                        @RequestParam(required = false) String cnhPermissionario,
                                                                        @RequestParam(required = true) Integer pageIndex,
                                                                        @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<PermissionarioResponseDTO> permissionarios = service.listarTodosPermissionarioFiltros(
                   nomePermissionario, cpfPermissionario, cnhPermissionario, pageRequest
            );

            return permissionarios;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar os Autorizatários com os filtros informados!");
        }
    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosDisponiveis() {
        return ResponseEntity.ok(service.listarPermissionariosDisponiveis(null));
    }

    @GetMapping("/buscar-disponiveis-defensor")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosDisponiveisDefensor() {
        return ResponseEntity.ok(service.listarPermissionariosDisponiveisDefensor(null));
    }

    @GetMapping("/buscar-disponiveis/{idPermissionario}")
    public ResponseEntity<List<PermissionarioResponseDTO>> buscarPermissionariosDisponiveisAlteracao(@PathVariable Long idPermissionario) {
        return ResponseEntity.ok(service.listarPermissionariosDisponiveis(idPermissionario));
    }

    @PostMapping("/inserir")
    public ResponseEntity<PermissionarioResponseDTO> inserirPermissionario(
            @RequestParam("permissionario") String permissionario,
            @RequestParam("anexoRg") MultipartFile anexoRg,
            @RequestParam("anexoCpf") MultipartFile anexoCpf,
            @RequestParam("anexoCnh") MultipartFile anexoCnh,
            @RequestParam("comprovanteResidencia") MultipartFile comprovanteResidencia,
            @RequestParam("certidaoNegativaMunicipal") MultipartFile certidaoNegativaMunicipal,
            @RequestParam("certidaoNegativaCriminal") MultipartFile certidaoNegativaCriminal,
            @RequestParam("certificadoPropriedade") MultipartFile certificadoPropriedade,
            @RequestParam("certificadoCondutor") MultipartFile certificadoCondutor,
            @RequestParam("apoliceSeguroVida") MultipartFile apoliceSeguroVida,
            @RequestParam("apoliceSeguroMotocicleta") MultipartFile apoliceSeguroMotocicleta,
            @RequestParam("foto") MultipartFile foto
    ) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.inserirPermissionario(permissionarioDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia,
                certidaoNegativaMunicipal, certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor,
                apoliceSeguroVida, apoliceSeguroMotocicleta, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PermissionarioResponseDTO> atualizarPermissionario(@RequestParam("permissionario") String permissionario,
                                                                             @RequestParam(value = "anexoRg", required = false) MultipartFile anexoRg,
                                                                             @RequestParam(value = "anexoCpf", required = false) MultipartFile anexoCpf,
                                                                             @RequestParam(value = "anexoCnh", required = false) MultipartFile anexoCnh,
                                                                             @RequestParam(value = "comprovanteResidencia", required = false) MultipartFile comprovanteResidencia,
                                                                             @RequestParam(value = "certidaoNegativaMunicipal", required = false) MultipartFile certidaoNegativaMunicipal,
                                                                             @RequestParam(value = "certidaoNegativaCriminal", required = false) MultipartFile certidaoNegativaCriminal,
                                                                             @RequestParam(value = "certificadoPropriedade", required = false) MultipartFile certificadoPropriedade,
                                                                             @RequestParam(value = "certificadoCondutor", required = false) MultipartFile certificadoCondutor,
                                                                             @RequestParam(value = "apoliceSeguroVida", required = false) MultipartFile apoliceSeguroVida,
                                                                             @RequestParam(value = "apoliceSeguroMotocicleta", required = false) MultipartFile apoliceSeguroMotocicleta,
                                                                             @RequestParam(value = "foto", required = false) MultipartFile foto) throws IOException {
        PermissionarioRequestDTO permissionarioDTO = new ObjectMapper().readValue(permissionario, PermissionarioRequestDTO.class);
        return ResponseEntity.ok(service.atualizarPermissionario(permissionarioDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia,
                certidaoNegativaMunicipal, certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor,
                apoliceSeguroVida, apoliceSeguroMotocicleta, foto));
    }

    @DeleteMapping("/excluir/{idPermissionario}/usuario/{usuario}")
    public ResponseEntity<Void> excluirPermissionario(@PathVariable Long idPermissionario, @PathVariable String usuario) {
        return service.excluirPermissionario(idPermissionario, usuario);
    }

    @GetMapping("/gerar-registro-condutor")
    public ResponseEntity<byte[]> gerarRegistroCondutor( @RequestParam(required = true) String cpfPermissionario,
                                                         @RequestParam(required = true) String modulo) {
        try{
            byte[] fileBytes = service.gerarRegistroCondutor(cpfPermissionario, modulo);

            String fileName = "registroCondutor-" + LocalDate.now() + "Nº" + cpfPermissionario + ".pdf";

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
            if(e.getMessage().equals("500"))
                return ResponseEntity.status(500).body(null);
        }

        return null;
    }

    @GetMapping("/gerar-termo-autorizacao-servico")
    public ResponseEntity<byte[]> gerarTermoAutorizacaoServico( @RequestParam(required = true) String cpfPermissionario,
                                                         @RequestParam(required = true) String modulo) {
        try{
            byte[] fileBytes = service.gerarTermoAutorizacaoServico(cpfPermissionario, modulo);

            String fileName = "termoAutorizacaoservico-" + LocalDate.now() + "Nº" + cpfPermissionario + ".pdf";

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
            if(e.getMessage().equals("500"))
                return ResponseEntity.status(500).body(null);
        }

        return null;
    }

    @GetMapping("/imprimir-anexo")
    public ResponseEntity<byte[]> imprimirAnexo( @RequestParam(required = true) String idAplicacao,
                                                     @RequestParam(required = true) String aplicacao,
                                                     @RequestParam(required = true) String anexo,
                                                     @RequestParam(required = true) String modulo) {
        try{
            byte[] fileBytes = service.imprimirAnexo(idAplicacao, aplicacao, anexo, modulo);

            String fileName = "anexo-" + anexo + "-" + LocalDate.now() + "Nº" + idAplicacao + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(fileBytes.length);

            return ResponseEntity.ok().headers(headers).body(fileBytes);
        } catch (Exception e){
            if(e.getMessage().equals("400"))
                return ResponseEntity.status(400).body(null);
            if(e.getMessage().equals("500"))
                return ResponseEntity.status(500).body(null);
        }
        return  null;
    }

}
