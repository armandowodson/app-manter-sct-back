package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PermissaoDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Defensor;
import com.projeto.produto.entity.Permissao;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class PermissaoServiceImpl {
    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Transactional
    public PermissaoDTO inserirPermissao(PermissaoDTO permissaoDTO) {
        if (Objects.isNull(permissaoDTO.getNumeroPermissao()) || Objects.isNull(permissaoDTO.getNumeroAlvara()) ||
                Objects.isNull(permissaoDTO.getAnoAlvara()) || Objects.isNull(permissaoDTO.getCategoriaPermissao()) ||
                Objects.isNull(permissaoDTO.getStatusPermissao()) || Objects.isNull(permissaoDTO.getDataValidadePermissao())) {
            throw new RuntimeException("Dados inválidos para a Permissão!");
        }

        if(Objects.isNull(permissaoDTO.getUsuario()) || permissaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Permissao permissao = converterPermissaoDTOToPermissao(permissaoDTO, 1);
        permissao = permissaoRepository.save(permissao);

        //Auditoria
        salvarAuditoria("PERMISSÃO", "INCLUSÃO", permissaoDTO.getUsuario());
        return converterPermissaoToPermissaoDTO(permissao);
    }

    @Transactional
    public PermissaoDTO atualizarPermissao(PermissaoDTO permissaoDTO) {
        if (Objects.isNull(permissaoDTO.getNumeroPermissao()) || Objects.isNull(permissaoDTO.getNumeroAlvara()) ||
                Objects.isNull(permissaoDTO.getAnoAlvara()) || Objects.isNull(permissaoDTO.getCategoriaPermissao()) ||
                Objects.isNull(permissaoDTO.getStatusPermissao()) || Objects.isNull(permissaoDTO.getDataValidadePermissao())) {
            throw new RuntimeException("Dados inválidos para a Permissão!");
        }

        if(Objects.isNull(permissaoDTO.getUsuario()) || permissaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Permissao permissaoExiste = permissaoRepository.findByNumeroPermissao(permissaoDTO.getNumeroPermissao());
        if(Objects.nonNull(permissaoExiste) && Objects.nonNull(permissaoExiste.getIdPermissao()))
            throw new RuntimeException("Já existe o Nº de Permissão: " + permissaoExiste.getIdPermissao() + " informado!" );

        Permissao permissao = converterPermissaoDTOToPermissao(permissaoDTO, 2);
        permissao = permissaoRepository.save(permissao);
        //Auditoria
        salvarAuditoria("PERMISSÃO", "ALTERAÇÃO", permissaoDTO.getUsuario());
        return converterPermissaoToPermissaoDTO(permissao);
    }

    public Page<PermissaoDTO> listarTodosPermissao(PageRequest pageRequest) {
        List<Permissao> listaPermissao = permissaoRepository.buscarTodas(pageRequest);
        Integer countLista = permissaoRepository.buscarTodas(null).size();
        List<PermissaoDTO> permissaoDTOList = converterEntityToDTO(listaPermissao);
        return new PageImpl<>(permissaoDTOList, pageRequest, countLista);
    }

    public PermissaoDTO buscarPermissaoId(Long idPermissao) {
        Permissao permissao = permissaoRepository.findById(idPermissao).get();
        PermissaoDTO permissaoDTO = new PermissaoDTO();
        if (permissao != null){
            permissaoDTO = converterPermissaoToPermissaoDTO(permissao);
        }
        return permissaoDTO;
    }

    public Page<PermissaoDTO> listarTodasPermissoesFiltros( String numeroPermissao, String numeroAlvara,
                                                           String anoAlvara, String statusPermissao,
                                                           String periodoInicial, String periodoFinal,
                                                           PageRequest pageRequest) {

        List<Permissao> listaPermissao = permissaoRepository.listarTodasPermissoesFiltros(
                numeroPermissao, numeroAlvara,  anoAlvara,  statusPermissao,
                periodoInicial != null ? LocalDate.parse(periodoInicial) : null,
                periodoFinal != null ? LocalDate.parse(periodoFinal) : null,  pageRequest
        );

        Integer countRegistros = permissaoRepository.listarTodasPermissoesFiltros(
                numeroPermissao, numeroAlvara,  anoAlvara,  statusPermissao,
                periodoInicial != null ? LocalDate.parse(periodoInicial) : null,
                periodoFinal != null ? LocalDate.parse(periodoFinal) : null,  pageRequest
        ).size();

        List<PermissaoDTO> listaPermissaoDTO = new ArrayList<>();
        if (!listaPermissao.isEmpty()){
            for (Permissao permissao : listaPermissao) {
                PermissaoDTO permissaoDTORetornado = converterPermissaoToPermissaoDTO(permissao);
                listaPermissaoDTO.add(permissaoDTORetornado);
            }
        }

        return new PageImpl<>(listaPermissaoDTO, pageRequest, countRegistros);
    }

    public List<PermissaoDTO> listarPermissoesDisponiveis(String numeroPermissao) {
        List<PermissaoDTO> listaPermissaoDTO = new ArrayList<>();
        List<Permissao> listaPermissao = permissaoRepository.listarPermissaoDisponiveis();
        if(Objects.nonNull(numeroPermissao)){
            Permissao permissao = permissaoRepository.findPermissaoByNumeroPermissao(numeroPermissao);
            PermissaoDTO permissaoDTORetornado = converterPermissaoToPermissaoDTO(permissao);
            listaPermissaoDTO.add(permissaoDTORetornado);
        }

        if (!listaPermissao.isEmpty()){
            for (Permissao permissaoDisponivel : listaPermissao) {
                PermissaoDTO permissaoDTORetornado = converterPermissaoToPermissaoDTO(permissaoDisponivel);
                listaPermissaoDTO.add(permissaoDTORetornado);
            }
        }

        return listaPermissaoDTO;
    }

    @Transactional
    public ResponseEntity<Void> excluirPermissao(Long idPermissao, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            Permissao permissao = permissaoRepository.findPermissaoByIdPermissao(idPermissao);
            if(permissaoRepository.verificarPermissoaExistente(permissao.getNumeroPermissao()) > 0)
                throw new RuntimeException("Não é possível realizar a exclusão. A Permissão de Nº " + permissao.getNumeroPermissao() +
                        " está sendo utilizada por um Permissionário!");

            permissao.setStatus("INATIVO");
            permissaoRepository.save(permissao);

            //Auditoria
            salvarAuditoria("PERMISSÃO", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir a Permissão!!!");
        }
    }

    public List<PermissaoDTO> converterEntityToDTO(List<Permissao> listaPermissao){
        List<PermissaoDTO> listaPermissaoDTO = new ArrayList<>();
        for(Permissao permissao : listaPermissao){
            PermissaoDTO permissaoDTO = converterPermissaoToPermissaoDTO(permissao);
            listaPermissaoDTO.add(permissaoDTO);
        }

        return  listaPermissaoDTO;
    }

    public PermissaoDTO converterPermissaoToPermissaoDTO(Permissao permissao){
        PermissaoDTO permissaoDTO = new PermissaoDTO();
        if (permissao.getIdPermissao() != null){
            permissaoDTO.setIdPermissao(permissao.getIdPermissao());
        }
        permissaoDTO.setNumeroPermissao(permissao.getNumeroPermissao());
        permissaoDTO.setNumeroAlvara(permissao.getNumeroAlvara());
        permissaoDTO.setAnoAlvara(permissao.getAnoAlvara());
        permissaoDTO.setCategoriaPermissao(permissao.getCategoriaPermissao());
        permissaoDTO.setStatusPermissao(permissao.getStatusPermissao());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        if(Objects.nonNull(permissao.getPeriodoInicialStatus())){
            String formattedDate = permissao.getPeriodoInicialStatus().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
            permissaoDTO.setPeriodoInicialStatus(formattedDate);
        }
        if(Objects.nonNull(permissao.getPeriodoFinalStatus())){
            String formattedDate = permissao.getPeriodoFinalStatus().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
            permissaoDTO.setPeriodoFinalStatus(formattedDate);
        }
        if(Objects.nonNull(permissao.getDataValidadePermissao())){
            String formattedDate = permissao.getDataValidadePermissao().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
            permissaoDTO.setDataValidadePermissao(formattedDate);
            formattedDate = permissao.getDataValidadePermissao().atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
            permissaoDTO.setDataValidadePermissaoOriginal(formattedDate);
        }
        permissaoDTO.setPenalidade(permissao.getPenalidade());
        if(Objects.nonNull(permissao.getDataValidadePenalidade())){
            String formattedDate = permissao.getDataValidadePenalidade().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
            permissaoDTO.setDataValidadePenalidade(formattedDate);
        }
        permissaoDTO.setModalidade(permissao.getModalidade());
        permissaoDTO.setAutorizacaoTrafego(permissao.getAutorizacaoTrafego());

        permissaoDTO.setDataCriacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataCriacao()));

        return  permissaoDTO;
    }

    public Permissao converterPermissaoDTOToPermissao(PermissaoDTO permissaoDTO, Integer tipo){
        Permissao permissao = new Permissao();
        if (permissaoDTO.getIdPermissao() != null && permissaoDTO.getIdPermissao() != 0){
            permissao = permissaoRepository.findById(permissaoDTO.getIdPermissao()).get();
        }
        permissao.setNumeroPermissao(permissaoDTO.getNumeroPermissao());
        permissao.setNumeroAlvara(permissaoDTO.getNumeroAlvara());
        permissao.setAnoAlvara(permissaoDTO.getAnoAlvara());

        if(tipo == 1){
            permissao.setCategoriaPermissao(permissaoDTO.getCategoriaPermissao());
        }else{
            permissao.setCategoriaPermissao(converterNomeCategoriaPermissao(permissaoDTO.getCategoriaPermissao()));
        }

        if(tipo == 1){
            permissao.setStatusPermissao(permissaoDTO.getStatusPermissao());
        }else{
            permissao.setStatusPermissao(converterNomeStatusPermissao(permissaoDTO.getStatusPermissao()));
        }

        if(Objects.nonNull(permissaoDTO.getPeriodoInicialStatus()) && !permissaoDTO.getPeriodoInicialStatus().isEmpty()) {
            String data = permissaoDTO.getPeriodoInicialStatus();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    permissao.setPeriodoInicialStatus(LocalDate.parse(data));
                }else{
                    permissao.setPeriodoInicialStatus(LocalDate.parse(data).minusDays(1));
                }
            }
        }

        if(Objects.nonNull(permissaoDTO.getPeriodoFinalStatus()) && !permissaoDTO.getPeriodoFinalStatus().isEmpty()){
            String data = permissaoDTO.getPeriodoFinalStatus();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    permissao.setPeriodoFinalStatus(LocalDate.parse(data));
                }else{
                    permissao.setPeriodoFinalStatus(LocalDate.parse(data).minusDays(1));
                }
            }
        }

        if(Objects.nonNull(permissaoDTO.getDataValidadePermissao())){
            String data = permissaoDTO.getDataValidadePermissao();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    permissao.setDataValidadePermissao(LocalDate.parse(data));
                }else{
                    permissao.setDataValidadePermissao(LocalDate.parse(data).minusDays(1));
                }
                if(permissao.getDataValidadePermissao().isAfter(LocalDate.now().plusYears(5)))
                    throw new RuntimeException("Data de Validade da Permissão acima de 5 anos!");

            }
        }

        if(tipo == 1){
            permissao.setPenalidade(permissaoDTO.getPenalidade());
        }else{
            permissao.setPenalidade(converterNomePenalidade(permissaoDTO.getPenalidade()));
        }

        if(Objects.nonNull(permissaoDTO.getDataValidadePenalidade()) && !permissaoDTO.getDataValidadePenalidade().isEmpty()){
            String data = permissaoDTO.getDataValidadePenalidade();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0)
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    permissao.setDataValidadePenalidade(LocalDate.parse(data));
                }else{
                    permissao.setDataValidadePenalidade(LocalDate.parse(data).minusDays(1));
                }
        }

        if(tipo == 1){
            permissao.setModalidade(permissaoDTO.getModalidade());
        }else{
            permissao.setModalidade(converterNomeModalidadePermissao(permissaoDTO.getModalidade()));
        }

        if(Objects.nonNull(permissaoDTO.getDataCriacao()) && !permissaoDTO.getDataCriacao().isEmpty())
            permissao.setDataCriacao(LocalDate.parse(permissaoDTO.getDataCriacao()));
        else
            permissao.setDataCriacao(LocalDate.now());

        permissao.setAutorizacaoTrafego(permissaoDTO.getAutorizacaoTrafego());
        permissao.setStatus("ATIVO");

        return  permissao;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public String converterNomeCategoriaPermissao(String categoria){
        switch (categoria){
            case "DELEGAÇÃO":
                return "1";
            case "TÍTULO PRECÁRIO":
                return "2";
            case "LICITAÇÃO":
                return "3";
            case "PRESTAÇÃO DO SERVIÇO PÚBLICO DE TRANSPORTE DE PASSAGEIROS":
                return "4";
        }

        return "";
    }

    public String converterNomeStatusPermissao(String status){
        switch (status){
            case "EM USO":
                return "1";
            case "SUSPENSA":
                return "2";
            case "RENUNCIADA":
                return "3";
            case "RESERVADA":
                return "4";
            case "SUBSTITUÍDA":
                return "5";
            case "REVOGADA":
                return "6";
            case "EXPIRADA":
                return "7";
            case "ABANDONADA":
                return "8";
        }

        return "";
    }

    public String converterNomePenalidade(String permissao){
        switch (permissao){
            case "MULTA":
                return "1";
            case "SUSPENSÃO":
                return "2";
            case "CASSAÇÃO DO REGISTRO DE CONDUTOR":
                return "3";
        }

        return "";
    }

    public String converterNomeModalidadePermissao(String modalidade){
        switch (modalidade){
            case "FIXO":
                return "1";
            case "ROTATIVO":
                return "2";
            case "FIXO-ROTATIVO":
                return "3";
        }

        return "";
    }
}
