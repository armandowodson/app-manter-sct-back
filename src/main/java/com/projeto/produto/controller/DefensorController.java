package com.projeto.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.produto.dto.DefensorRequestDTO;
import com.projeto.produto.dto.DefensorResponseDTO;
import com.projeto.produto.service.impl.DefensorServiceImpl;
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

    @GetMapping("/buscar-permissionario/{idPermissionario}")
    public ResponseEntity<DefensorResponseDTO> buscarDefensorIdPermissionario(@PathVariable String idPermissionario) {
        try{
            return ResponseEntity.ok(service.buscarDefensorIdPermissionario(idPermissionario));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar o Defensor pelo ID!");
        }
    }

    @GetMapping("/buscar-filtros")
    public Page<DefensorResponseDTO> buscarDefensorsFiltros(   @RequestParam(required = false) String nomeDefensor,
                                                               @RequestParam(required = false) String cpfDefensor,
                                                               @RequestParam(required = false) String cnhDefensor,
                                                               @RequestParam(required = false) String nomePermissionario,
                                                               @RequestParam(required = false) String cpfPermissionario,
                                                               @RequestParam(required = true) Integer pageIndex,
                                                               @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try{
            Page<DefensorResponseDTO> defensores = service.listarTodosDefensorFiltros(
                    nomeDefensor, cpfDefensor, cnhDefensor, nomePermissionario, cpfPermissionario, pageRequest
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
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.inserirDefensor(defensorDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia,
                certidaoNegativaMunicipal, certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor,
                apoliceSeguroVida, apoliceSeguroMotocicleta, foto));
    }

    @PostMapping("/alterar")
    public ResponseEntity<DefensorResponseDTO> atualizarDefensor(@RequestParam("defensor") String defensor,
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
        DefensorRequestDTO defensorDTO = new ObjectMapper().readValue(defensor, DefensorRequestDTO.class);
        return ResponseEntity.ok(service.atualizarDefensor(defensorDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia,
                certidaoNegativaMunicipal, certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor,
                apoliceSeguroVida, apoliceSeguroMotocicleta, foto));
    }

    @DeleteMapping("/excluir/{idDefensor}/usuario/{usuario}")
    public ResponseEntity<Void> excluirDefensor(@PathVariable Long idDefensor, @PathVariable String usuario) {
        return service.excluirDefensor(idDefensor, usuario);
    }

    @GetMapping("/gerar-registro-condutor")
    public ResponseEntity<byte[]> gerarRegistroCondutor( @RequestParam(required = true) String idPermissionario,
                                                         @RequestParam(required = true) String modulo) {
        try{
            byte[] fileBytes = service.gerarRegistroCondutor(idPermissionario, modulo);

            String fileName = "registroCondutor-" + LocalDate.now() + "Nº" + idPermissionario + ".pdf";

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
