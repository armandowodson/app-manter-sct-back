package com.projeto.produto.controller;

import com.projeto.produto.dto.PermissaoDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.service.impl.PermissaoServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/permissao")
public class PermissaoController {
    @Autowired
    public PermissaoServiceImpl service;

    @GetMapping("/buscar-todos")
    public Page<PermissaoDTO> listarTodosPermissao(@RequestParam(required = true) Integer pageIndex, @RequestParam(required = true) Integer pageSize) {
        try{
            PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
            Page<PermissaoDTO> permissoes = service.listarTodosPermissao(pageRequest);
            return permissoes;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar todas as Permissões cadastradas!");
        }
    }

    @GetMapping("/buscar/{idPermissao}")
    public ResponseEntity<PermissaoDTO> buscarPermissaoId(@PathVariable Long idPermissao) {
        return ResponseEntity.ok(service.buscarPermissaoId(idPermissao));
    }

    @GetMapping("/buscar-filtros")
    public Page<PermissaoDTO> buscarPermissaoFiltros( @RequestParam(required = false) String numeroPermissao,
                                                      @RequestParam(required = false) String numeroAlvara,
                                                      @RequestParam(required = false) String anoPermissao,
                                                      @RequestParam(required = false) String statusPermissao,
                                                      @RequestParam(required = false) String periodoInicialStatus,
                                                      @RequestParam(required = false) String periodoFinalStatus,
                                                      @RequestParam(required = true) Integer pageIndex,
                                                      @RequestParam(required = true) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        try {
            Page<PermissaoDTO> permissoes = service.listarTodasPermissoesFiltros(
                    numeroPermissao, numeroAlvara, anoPermissao,
                    statusPermissao, periodoInicialStatus, periodoFinalStatus, pageRequest
            );

            return permissoes;
        } catch (Exception e){
            throw new RuntimeException("Não foi possível consultar as Permissões com os filtros informados!");
        }
    }

    @GetMapping("/buscar-disponiveis")
    public ResponseEntity<List<PermissaoDTO>> buscarPermissoesDisponiveis() {
        return ResponseEntity.ok(service.listarPermissoesDisponiveis(null));
    }

    @GetMapping("/buscar-disponiveis-defensor")
    public ResponseEntity<List<PermissaoDTO>> buscarPermissoesDisponiveisDefensor() {
        return ResponseEntity.ok(service.listarPermissoesDisponiveisDefensor(null));
    }

    @GetMapping("/buscar-disponiveis/{numeroPermissao}")
    public ResponseEntity<List<PermissaoDTO>> buscarPermissoesDisponiveisAlteracao(@PathVariable String numeroPermissao) {
        return ResponseEntity.ok(service.listarPermissoesDisponiveis(numeroPermissao));
    }

    @GetMapping("/buscar-disponiveis-defensor/{numeroPermissao}")
    public ResponseEntity<List<PermissaoDTO>> buscarPermissoesDisponiveisAlteracaoDefensor(@PathVariable String numeroPermissao) {
        return ResponseEntity.ok(service.listarPermissoesDisponiveisDefensor(numeroPermissao));
    }

    @PostMapping("/inserir")
    public ResponseEntity<PermissaoDTO> inserirPermissao(@RequestBody PermissaoDTO permissaoDTO) {
        return ResponseEntity.ok(service.inserirPermissao(permissaoDTO));
    }

    @PostMapping("/alterar")
    public ResponseEntity<PermissaoDTO> atualizarPermissao(@RequestBody PermissaoDTO permissaoDTO) {
        return ResponseEntity.ok(service.atualizarPermissao(permissaoDTO));
    }

    @DeleteMapping("/excluir/{idPermissao}/usuario/{usuario}")
    public ResponseEntity<Void> excluirPermissao(@PathVariable Long idPermissao, @PathVariable String usuario) {
        return service.excluirPermissao(idPermissao, usuario);
    }

    @GetMapping("/gerar-relatorio")
    public ResponseEntity<byte[]> gerarRelatorio( @RequestParam(required = false) String anoPermissao,
                                                  @RequestParam(required = false) String statusPermissao,
                                                  @RequestParam(required = false) String dataInicioGeracao,
                                                  @RequestParam(required = false) String dataFimGeracao) {
            byte[] fileBytes = service.gerarRelatorio(anoPermissao, statusPermissao, dataInicioGeracao, dataFimGeracao);

            Random gerador = new Random();
            Integer numAleatorio = gerador.nextInt(100);
            String fileName = "permissao-" + LocalDate.now() + ":" + numAleatorio + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(fileBytes.length);

            return ResponseEntity.ok().headers(headers).body(fileBytes);
    }

}
