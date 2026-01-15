package com.projeto.produto.service.impl;

import com.projeto.produto.dto.FiscalizacaoDTO;
import com.projeto.produto.dto.FiscalizacaoDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Fiscalizacao;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.FiscalizacaoRepository;
import com.projeto.produto.repository.FiscalizacaoRepository;
import com.projeto.produto.repository.VeiculoRepository;
import com.projeto.produto.utils.ValidaCNPJ;
import com.projeto.produto.utils.ValidaCPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class FiscalizacaoServiceImpl {
    @Autowired
    private FiscalizacaoRepository fiscalizacaoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Transactional
    public FiscalizacaoDTO inserirFiscalizacao(FiscalizacaoDTO fiscalizacaoDTO) {
        
        if (Objects.isNull(fiscalizacaoDTO.getIdVeiculo()) || Objects.isNull(fiscalizacaoDTO.getIdPermissionario()) ||
                Objects.isNull(fiscalizacaoDTO.getDataFiscalizacao()) || Objects.isNull(fiscalizacaoDTO.getMotivoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getPrazoRegularizacao()) || Objects.isNull(fiscalizacaoDTO.getTipoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getNumeroPermissao()) || Objects.isNull(fiscalizacaoDTO.getGrupoMultas()) ||
                Objects.isNull(fiscalizacaoDTO.getNaturezaInfracao()) || Objects.isNull(fiscalizacaoDTO.getModalidade()) ||
                Objects.isNull(fiscalizacaoDTO.getPenalidade())) {
            throw new RuntimeException("Dados inválidos para a Fiscalizacao!");
        }

        if(Objects.isNull(fiscalizacaoDTO.getUsuario()) || fiscalizacaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Fiscalizacao fiscalizacao = new Fiscalizacao();
        try {
            fiscalizacao = converterFiscalizacaoDTOToFiscalizacao(fiscalizacaoDTO, 1);
            fiscalizacao = fiscalizacaoRepository.save(fiscalizacao);

            //Auditoria
            salvarAuditoria("FISCALIZAÇÃO TÁXI", "INCLUSÃO", fiscalizacaoDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Fiscalização!");
        }

        return converterFiscalizacaoToFiscalizacaoDTO(fiscalizacao);
    }

    @Transactional
    public FiscalizacaoDTO atualizarFiscalizacao(FiscalizacaoDTO fiscalizacaoDTO) {
        if (Objects.isNull(fiscalizacaoDTO.getIdVeiculo()) || Objects.isNull(fiscalizacaoDTO.getIdPermissionario()) ||
                Objects.isNull(fiscalizacaoDTO.getDataFiscalizacao()) || Objects.isNull(fiscalizacaoDTO.getMotivoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getPrazoRegularizacao()) || Objects.isNull(fiscalizacaoDTO.getTipoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getNumeroPermissao()) || Objects.isNull(fiscalizacaoDTO.getGrupoMultas()) ||
                Objects.isNull(fiscalizacaoDTO.getNaturezaInfracao()) || Objects.isNull(fiscalizacaoDTO.getModalidade()) ||
                Objects.isNull(fiscalizacaoDTO.getPenalidade())) {
            throw new RuntimeException("Dados inválidos para a Fiscalização!");
        }

        if(Objects.isNull(fiscalizacaoDTO.getUsuario()) || fiscalizacaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Fiscalizacao fiscalizacao = new Fiscalizacao();
        try{
            fiscalizacao = converterFiscalizacaoDTOToFiscalizacao(fiscalizacaoDTO, 2);

            fiscalizacao = fiscalizacaoRepository.save(fiscalizacao);

            //Auditoria
            salvarAuditoria("FISCALIZAÇÃO", "ALTERAÇÃO", fiscalizacaoDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível alterar os dados da Fiscalização!");
        }

        return converterFiscalizacaoToFiscalizacaoDTO(fiscalizacaoRepository.save(fiscalizacao));
    }

    public Page<FiscalizacaoDTO> listarTodasFiscalizacoes(PageRequest pageRequest) {
        List<Fiscalizacao> fiscalizacaoList = fiscalizacaoRepository.buscarTodas(pageRequest);
        Integer countLista = fiscalizacaoRepository.buscarTodas(null).size();
        List<FiscalizacaoDTO> fiscalizacaoResponseDTOList = converterEntityToDTO(fiscalizacaoList);
        return new PageImpl<>(fiscalizacaoResponseDTOList, pageRequest, countLista);
    }

    public FiscalizacaoDTO buscarFiscalizacaoId(Long idFiscalizacao) {
        Fiscalizacao fiscalizacao = fiscalizacaoRepository.findFiscalizacaoByIdFiscalizacao(idFiscalizacao);
        FiscalizacaoDTO fiscalizacaoResponseDTO = new FiscalizacaoDTO();
        if (fiscalizacao != null){
            fiscalizacaoResponseDTO = converterFiscalizacaoToFiscalizacaoDTO(fiscalizacao);
        }
        return fiscalizacaoResponseDTO;
    }

    public Page<FiscalizacaoDTO> listarTodosFiscalizacaoFiltros(   String placa, String nomePermissionario,
                                                                   String dataFiscalizacao, String motivoInfracao,
                                                                   String penalidade, PageRequest pageRequest) {
        LocalDate localDate = LocalDate.now();
        if(Objects.nonNull(dataFiscalizacao) && !dataFiscalizacao.isEmpty()){
            String data = dataFiscalizacao;
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                localDate = zonedDateTime.toLocalDate();
            }
        }else{
            localDate = LocalDate.parse("2026-01-01");
        }

        List<Fiscalizacao> listaFiscalizacao = fiscalizacaoRepository.listarTodasFiscalizacoesFiltros(
                placa, nomePermissionario, motivoInfracao, penalidade, localDate, pageRequest
        );

        Integer countRegistros = fiscalizacaoRepository.listarTodasFiscalizacoesFiltros(
                placa, nomePermissionario, motivoInfracao, penalidade, localDate,null
        ).size();

        List<FiscalizacaoDTO> listaFiscalizacaoDTO = new ArrayList<>();
        if (!listaFiscalizacao.isEmpty()){
            for (Fiscalizacao fiscalizacao : listaFiscalizacao) {
                FiscalizacaoDTO fiscalizacaoResponseDTORetornado = converterFiscalizacaoToFiscalizacaoDTO(fiscalizacao);
                listaFiscalizacaoDTO.add(fiscalizacaoResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaFiscalizacaoDTO, pageRequest, countRegistros);
    }

    @Transactional
    public ResponseEntity<Void> excluirFiscalizacao(Long idFiscalizacao, String usuario) {
        String msgErro = "Erro ao Excluir a Fiscalização!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            Fiscalizacao fiscalizacao = fiscalizacaoRepository.findFiscalizacaoByIdFiscalizacao(idFiscalizacao);
            fiscalizacao.setStatus("INATIVO");
            fiscalizacaoRepository.save(fiscalizacao);

            //Auditoria
            salvarAuditoria("FISCALIZAÇÃO", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException(msgErro);
        }
    }

    public List<FiscalizacaoDTO> converterEntityToDTO(List<Fiscalizacao> listaFiscalizacao){
        List<FiscalizacaoDTO> listaFiscalizacaoDTO = new ArrayList<>();
        for(Fiscalizacao fiscalizacao : listaFiscalizacao){
            FiscalizacaoDTO fiscalizacaoResponseDTO = converterFiscalizacaoToFiscalizacaoDTO(fiscalizacao);
            listaFiscalizacaoDTO.add(fiscalizacaoResponseDTO);
        }

        return listaFiscalizacaoDTO;
    }

    public FiscalizacaoDTO converterFiscalizacaoToFiscalizacaoDTO(Fiscalizacao fiscalizacao){
        FiscalizacaoDTO fiscalizacaoResponseDTO = new FiscalizacaoDTO();
        if (fiscalizacao.getIdFiscalizacao() != null){
            fiscalizacaoResponseDTO.setIdFiscalizacao(fiscalizacao.getIdFiscalizacao());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        String formattedDate = fiscalizacao.getDataFiscalizacao().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
        fiscalizacaoResponseDTO.setDataFiscalizacao(formattedDate);
        formattedDate = fiscalizacao.getDataFiscalizacao().atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
        fiscalizacaoResponseDTO.setDataFiscalizacaoOriginal(formattedDate);

        fiscalizacaoResponseDTO.setIdVeiculo(fiscalizacao.getVeiculo().getIdVeiculo());
        fiscalizacaoResponseDTO.setDataCriacao(fiscalizacao.getDataCriacao().toString());
        fiscalizacaoResponseDTO.setPlaca(fiscalizacao.getVeiculo().getPlaca());
        fiscalizacaoResponseDTO.setMarca(fiscalizacao.getVeiculo().getMarca());
        fiscalizacaoResponseDTO.setModelo(fiscalizacao.getVeiculo().getModelo());
        fiscalizacaoResponseDTO.setCor(converterIdCor(fiscalizacao.getVeiculo().getCor()));
        fiscalizacaoResponseDTO.setIdPermissionario(fiscalizacao.getVeiculo().getPermissionario().getIdPermissionario().toString());
        fiscalizacaoResponseDTO.setNumeroPermissao(fiscalizacao.getVeiculo().getNumeroPermissao());
        fiscalizacaoResponseDTO.setNomePermissionario(fiscalizacao.getVeiculo().getPermissionario().getNomePermissionario());
        fiscalizacaoResponseDTO.setCnhPermissionario(fiscalizacao.getVeiculo().getPermissionario().getCnhPermissionario());
        fiscalizacaoResponseDTO.setMotivoInfracao(converterIdMotivoInfracao(fiscalizacao.getMotivoInfracao()));
        fiscalizacaoResponseDTO.setTipoInfracao(converterIdTipoInfracao(fiscalizacao.getTipoInfracao()));
        fiscalizacaoResponseDTO.setGrupoMultas(fiscalizacao.getGrupoMultas());

        formattedDate = fiscalizacao.getPrazoRegularizacao().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
        fiscalizacaoResponseDTO.setPrazoRegularizacao(formattedDate);
        formattedDate = fiscalizacao.getPrazoRegularizacao().atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter);
        fiscalizacaoResponseDTO.setPrazoRegularizacaoOriginal(formattedDate);

        fiscalizacaoResponseDTO.setNaturezaInfracao(converterIdNaturezaInfracao(fiscalizacao.getNaturezaInfracao()));
        fiscalizacaoResponseDTO.setModalidade(converterIdModalidadeInfracao(fiscalizacao.getModalidade()));
        fiscalizacaoResponseDTO.setPenalidade(converterIdPenalidadeInfracao(fiscalizacao.getPenalidade()));
        fiscalizacaoResponseDTO.setObservacao(fiscalizacao.getObservacao());
        fiscalizacaoResponseDTO.setDataCriacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fiscalizacao.getDataCriacao()));
        fiscalizacaoResponseDTO.setStatus(fiscalizacao.getStatus());

        return fiscalizacaoResponseDTO;
    }

    public Fiscalizacao converterFiscalizacaoDTOToFiscalizacao(FiscalizacaoDTO fiscalizacaoDTO, Integer tipo) throws IOException {
        Fiscalizacao fiscalizacao = new Fiscalizacao();
        if (fiscalizacaoDTO.getIdFiscalizacao() != null && fiscalizacaoDTO.getIdFiscalizacao() != 0){
            fiscalizacao = fiscalizacaoRepository.findFiscalizacaoByIdFiscalizacao(fiscalizacaoDTO.getIdFiscalizacao());
        }

        fiscalizacao.setVeiculo(veiculoRepository.findVeiculoByIdVeiculo(fiscalizacaoDTO.getIdVeiculo()));

        if(Objects.nonNull(fiscalizacaoDTO.getDataFiscalizacao()) && !fiscalizacaoDTO.getDataFiscalizacao().isEmpty()){
            String data = fiscalizacaoDTO.getDataFiscalizacao();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    fiscalizacao.setDataFiscalizacao(LocalDate.parse(data));
                }else{
                    fiscalizacao.setDataFiscalizacao(LocalDate.parse(data).minusDays(1));
                }
            }
        }

        if(tipo == 1){
            fiscalizacao.setMotivoInfracao(fiscalizacaoDTO.getMotivoInfracao());
        }else{
            fiscalizacao.setMotivoInfracao(converterNomeMotivoInfracao(fiscalizacaoDTO.getMotivoInfracao()));
        }

        if(tipo == 1){
            fiscalizacao.setTipoInfracao(fiscalizacaoDTO.getTipoInfracao());
        }else{
            fiscalizacao.setTipoInfracao(converterNomeTipoInfracao(fiscalizacaoDTO.getTipoInfracao()));
        }

        fiscalizacao.setGrupoMultas(fiscalizacaoDTO.getGrupoMultas());

        if(Objects.nonNull(fiscalizacaoDTO.getPrazoRegularizacao()) && !fiscalizacaoDTO.getPrazoRegularizacao().isEmpty()){
            String data = fiscalizacaoDTO.getPrazoRegularizacao();
            Integer indexChar = data.indexOf('T');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                if(tipo == 1){
                    fiscalizacao.setPrazoRegularizacao(LocalDate.parse(data));
                }else{
                    fiscalizacao.setPrazoRegularizacao(LocalDate.parse(data).minusDays(1));
                }
            }
        }

        if(tipo == 1){
            fiscalizacao.setNaturezaInfracao(fiscalizacaoDTO.getNaturezaInfracao());
        }else{
            fiscalizacao.setNaturezaInfracao(converterNomeNaturezaInfracao(fiscalizacaoDTO.getNaturezaInfracao()));
        }

        if(tipo == 1){
            fiscalizacao.setModalidade(fiscalizacaoDTO.getModalidade());
        }else{
            fiscalizacao.setModalidade(converterNomeModalidadeInfracao(fiscalizacaoDTO.getModalidade()));
        }

        if(tipo == 1){
            fiscalizacao.setPenalidade(fiscalizacaoDTO.getPenalidade());
        }else{
            fiscalizacao.setPenalidade(converterNomePenalidadeInfracao(fiscalizacaoDTO.getPenalidade()));
        }

        if(Objects.nonNull(fiscalizacaoDTO.getDataCriacao()) && !fiscalizacaoDTO.getDataCriacao().isEmpty())
            fiscalizacao.setDataCriacao(LocalDate.parse(fiscalizacaoDTO.getDataCriacao()));
        else
            fiscalizacao.setDataCriacao(LocalDate.now());

        fiscalizacao.setObservacao(fiscalizacaoDTO.getObservacao());
        fiscalizacao.setStatus("ATIVO");

        return  fiscalizacao;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public String converterIdMotivoInfracao(String motivo){
        switch (motivo){
            case "1":
                return "VEÍCULO IRREGULAR";
            case "2":
                return "VEÍCULO CLANDESTINO";
            case "3":
                return "VEÍCULO SEM TAXÍMETRO";
        }

        return "";
    }

    public String converterNomeMotivoInfracao(String motivo){
        switch (motivo){
            case "VEÍCULO IRREGULAR":
                return "1";
            case "VEÍCULO CLANDESTINO":
                return "2";
            case "VEÍCULO SEM TAXÍMETRO":
                return "3";
        }

        return "";
    }

    public String converterIdTipoInfracao(String tipo){
        switch (tipo){
            case "1":
                return "DEVER";
            case "2":
                return "PROIBIÇÃO";
        }

        return "";
    }

    public String converterNomeTipoInfracao(String tipo){
        switch (tipo){
            case "DEVER":
                return "1";
            case "PROIBIÇÃO":
                return "2";
        }

        return "";
    }

    public String converterIdNaturezaInfracao(String natureza){
        switch (natureza){
            case "1":
                return "LEVE";
            case "2":
                return "MÉDIA";
            case "3":
                return "GRAVE";
        }

        return "";
    }

    public String converterNomeNaturezaInfracao(String natureza){
        switch (natureza){
            case "LEVE":
                return "1";
            case "MÉDIA":
                return "2";
            case "GRAVE":
                return "3";
        }

        return "";
    }

    public String converterIdModalidadeInfracao(String modalidade){
        switch (modalidade){
            case "1":
                return "PRIMÁRIA";
            case "2":
                return "REINCIDENTE";
        }

        return "";
    }

    public String converterNomeModalidadeInfracao(String modalidade){
        switch (modalidade){
            case "PRIMÁRIA":
                return "1";
            case "REINCIDENTE":
                return "2";
        }

        return "";
    }

    public String converterIdPenalidadeInfracao(String modalidade){
        switch (modalidade){
            case "1":
                return "ADVERTÊNCIA";
            case "2":
                return "MULTA";
            case "3":
                return "SUSPENSÃO";
            case "4":
                return "CASSAÇÃO";
        }

        return "";
    }

    public String converterNomePenalidadeInfracao(String modalidade){
        switch (modalidade){
            case "ADVERTÊNCIA":
                return "1";
            case "MULTA":
                return "2";
            case "SUSPENSÃO":
                return "3";
            case "CASSAÇÃO":
                return "4";
        }

        return "";
    }

    public String converterIdCor(String cor){
        switch (cor){
            case "1":
                return "BRANCO";
            case "2":
                return "PRATA";
            case "3":
                return "CINZA";
        }

        return "";
    }

}

