package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PontosTaxiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PontosTaxiServiceImpl {
    @Autowired
    private PontosTaxiRepository pontosTaxiRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Transactional
    public PontoTaxiDTO inserirPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        if (Objects.isNull(pontoTaxiDTO.getDescricaoPonto()) || Objects.isNull(pontoTaxiDTO.getNumeroPonto())) {
            throw new RuntimeException("Dados inválidos para o Ponto de Estacionamento de Táxi!");
        }
        if(Objects.isNull(pontoTaxiDTO.getUsuario()) || pontoTaxiDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        PontoTaxi pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
        pontoTaxi.setDataCriacao(LocalDate.now());
        pontoTaxi = pontosTaxiRepository.save(pontoTaxi);

        //Auditoria
        salvarAuditoria("PONTO DE ESTACIONAMENTO DE TÁXI", "INCLUSÃO", pontoTaxiDTO.getUsuario());
        return converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
    }

    @Transactional
    public PontoTaxiDTO atualizarPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        if (Objects.isNull(pontoTaxiDTO.getDescricaoPonto()) || Objects.isNull(pontoTaxiDTO.getNumeroPonto())) {
            throw new RuntimeException("Dados inválidos para o Ponto de Estacionamento de Táxi!");
        }
        if(Objects.isNull(pontoTaxiDTO.getUsuario()) || pontoTaxiDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        PontoTaxi pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
        pontoTaxi = pontosTaxiRepository.save(pontoTaxi);
        //Auditoria
        salvarAuditoria("PONTO DE ESTACIONAMENTO DE TÁXI", "ALTERAÇÃO", pontoTaxiDTO.getUsuario());
        return converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
    }

    public Page<PontoTaxiDTO> listarTodosPontosTaxi(PageRequest pageRequest) {
        List<PontoTaxi> listaPontosTaxi = pontosTaxiRepository.buscarTodos(pageRequest);
        Integer countLista = pontosTaxiRepository.buscarTodos(null).size();
        List<PontoTaxiDTO> pontosTaxiDTOList = converterEntityToDTO(listaPontosTaxi);
        return new PageImpl<>(pontosTaxiDTOList, pageRequest, countLista);
    }

    public PontoTaxiDTO buscarPontoTaxiId(Long idPontoTaxi) {
        PontoTaxi pontoTaxi = pontosTaxiRepository.findByIdPontoTaxi(idPontoTaxi);
        PontoTaxiDTO pontoTaxiDTO = new PontoTaxiDTO();
        if (pontoTaxi != null){
            pontoTaxiDTO = converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
        }
        return pontoTaxiDTO;
    }

    public Page<PontoTaxiDTO> listarTodosPontosTaxiFiltros(String numeroPonto, String descricaoPonto,
                                                           String fatorRotatividade, String numeroVagas,
                                                           String referenciaPonto, String modalidade,
                                                           PageRequest pageRequest) {

        List<PontoTaxi> listaPontosTaxi = pontosTaxiRepository.listarTodosPontosTaxiFiltros(
                numeroPonto,   descricaoPonto != null ? descricaoPonto.toUpperCase() : descricaoPonto,
                fatorRotatividade,  referenciaPonto != null ? referenciaPonto.toUpperCase() : referenciaPonto,
                numeroVagas, modalidade,  pageRequest
        );

        Integer countRegistros = pontosTaxiRepository.listarTodosPontosTaxiFiltros(
                numeroPonto,   descricaoPonto != null ? descricaoPonto.toUpperCase() : descricaoPonto,
                fatorRotatividade,  referenciaPonto != null ? referenciaPonto.toUpperCase() : referenciaPonto,
                numeroVagas, modalidade,  null
        ).size();

        List<PontoTaxiDTO> listaPontoTaxiDTO = new ArrayList<>();
        if (!listaPontosTaxi.isEmpty()){
            for (PontoTaxi pontoTaxi : listaPontosTaxi) {
                PontoTaxiDTO pontoTaxiDTORetornado = converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
                listaPontoTaxiDTO.add(pontoTaxiDTORetornado);
            }
        }

        return new PageImpl<>(listaPontoTaxiDTO, pageRequest, countRegistros);
    }

    @Transactional
    public ResponseEntity<Void> excluirPontoTaxi(Long idPontoTaxi, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            pontosTaxiRepository.deletePontoTaxiByIdPontoTaxi(idPontoTaxi);

            //Auditoria
            salvarAuditoria("PONTO DE ESTACIONAMENTO DE TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Ponto de Estacionamento de Táxi!!!");
        }
    }

    public List<PontoTaxiDTO> converterEntityToDTO(List<PontoTaxi> listaPontosTaxi){
        List<PontoTaxiDTO> listaPontosTaxiDTO = new ArrayList<>();
        for(PontoTaxi pontoTaxi : listaPontosTaxi){
            PontoTaxiDTO pontoTaxiDTO = converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
            listaPontosTaxiDTO.add(pontoTaxiDTO);
        }

        return  listaPontosTaxiDTO;
    }

    public PontoTaxiDTO converterPontoTaxiToPontoTaxiDTO(PontoTaxi pontoTaxi){
        PontoTaxiDTO pontoTaxiDTO = new PontoTaxiDTO();
        if (pontoTaxi.getIdPontoTaxi() != null){
            pontoTaxiDTO.setIdPontoTaxi(pontoTaxi.getIdPontoTaxi());
        }
        pontoTaxiDTO.setDescricaoPonto(pontoTaxi.getDescricaoPonto());
        pontoTaxiDTO.setNumeroPonto(pontoTaxi.getNumeroPonto());
        pontoTaxiDTO.setReferenciaPonto(pontoTaxi.getReferenciaPonto());
        pontoTaxiDTO.setFatorRotatividade(pontoTaxi.getFatorRotatividade());
        pontoTaxiDTO.setNumeroVagas(pontoTaxi.getNumeroVagas());
        pontoTaxiDTO.setModalidade(pontoTaxi.getModalidade());
        pontoTaxiDTO.setDataCriacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(pontoTaxi.getDataCriacao()));

        return  pontoTaxiDTO;
    }

    public PontoTaxi converterPontoTaxiDTOToPontoTaxi(PontoTaxiDTO pontoTaxiDTO){
        PontoTaxi pontoTaxi = new PontoTaxi();
        if (pontoTaxiDTO.getIdPontoTaxi() != null && pontoTaxiDTO.getIdPontoTaxi() != 0){
            pontoTaxi = pontosTaxiRepository.findByIdPontoTaxi(pontoTaxiDTO.getIdPontoTaxi());
        }
        pontoTaxi.setDescricaoPonto(pontoTaxiDTO.getDescricaoPonto());
        pontoTaxi.setNumeroPonto(pontoTaxiDTO.getNumeroPonto());
        pontoTaxi.setReferenciaPonto(pontoTaxiDTO.getReferenciaPonto());
        pontoTaxi.setFatorRotatividade(pontoTaxiDTO.getFatorRotatividade());
        pontoTaxi.setNumeroVagas(pontoTaxiDTO.getNumeroVagas());
        pontoTaxi.setModalidade(pontoTaxiDTO.getModalidade());

        return  pontoTaxi;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }
}
