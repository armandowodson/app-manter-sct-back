package com.projeto.produto.service.impl;

import com.projeto.produto.api.model.ProdutoDTO;
import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Produto;
import com.projeto.produto.repository.PontosTaxiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @Transactional
    public PontoTaxiDTO inserirPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        if (Objects.isNull(pontoTaxiDTO.getDescricaoPonto()) || Objects.isNull(pontoTaxiDTO.getNumeroPonto())) {
            throw new RuntimeException("Dados inválidos para o Ponto de Táxi!");
        }
        PontoTaxi pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
        pontoTaxi.setDataCriacao(LocalDate.now());
        pontoTaxi = pontosTaxiRepository.save(pontoTaxi);
        return converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
    }

    @Transactional
    public PontoTaxiDTO atualizarPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        PontoTaxi pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
        return converterPontoTaxiToPontoTaxiDTO(pontosTaxiRepository.save(pontoTaxi));
    }

    public List<PontoTaxiDTO> listarTodosPontosTaxi() {
        List<PontoTaxi> listaPontosTaxi = pontosTaxiRepository.findAll(Sort.by(Sort.Direction.ASC, "descricaoPonto"));
        return converterEntityToDTO(listaPontosTaxi);
    }

    public PontoTaxiDTO buscarPontoTaxiId(Long idPontosTaxi) {
        PontoTaxi pontoTaxi = pontosTaxiRepository.findByIdPontosTaxi(idPontosTaxi);
        PontoTaxiDTO pontoTaxiDTO = new PontoTaxiDTO();
        if (pontoTaxi != null){
            pontoTaxiDTO = converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
        }
        return pontoTaxiDTO;
    }

    public List<PontoTaxiDTO> listarTodosPontosTaxiFiltros(PontoTaxiDTO pontoTaxiDTO) {
        List<PontoTaxi> listaPontosTaxi = pontosTaxiRepository.listarTodosPontosTaxiFiltros(
                pontoTaxiDTO.getNumeroPonto(),
                pontoTaxiDTO.getDescricaoPonto() != null ? pontoTaxiDTO.getDescricaoPonto().toUpperCase() : pontoTaxiDTO.getDescricaoPonto(),
                pontoTaxiDTO.getFatorRotatividade(),
                pontoTaxiDTO.getReferenciaPonto() != null ? pontoTaxiDTO.getReferenciaPonto().toUpperCase() : pontoTaxiDTO.getReferenciaPonto()
        );

        List<PontoTaxiDTO> listaPontoTaxiDTO = new ArrayList<>();
        if (!listaPontosTaxi.isEmpty()){
            for (PontoTaxi pontoTaxi : listaPontosTaxi) {
                PontoTaxiDTO pontoTaxiDTORetornado = converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
                listaPontoTaxiDTO.add(pontoTaxiDTORetornado);
            }
        }

        return listaPontoTaxiDTO;
    }

    @Transactional
    public ResponseEntity<Void> excluirPontoTaxi(Long idPontosTaxi) {
        try{
            pontosTaxiRepository.deletePontoTaxiByIdPontosTaxi(idPontosTaxi);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Ponto de Táxi!!!");
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
        if (pontoTaxi.getIdPontosTaxi() != null){
            pontoTaxiDTO.setIdPontosTaxi(pontoTaxi.getIdPontosTaxi());
        }
        pontoTaxiDTO.setDescricaoPonto(pontoTaxi.getDescricaoPonto());
        pontoTaxiDTO.setNumeroPonto(pontoTaxi.getNumeroPonto());
        pontoTaxiDTO.setReferenciaPonto(pontoTaxi.getReferenciaPonto());
        pontoTaxiDTO.setFatorRotatividade(pontoTaxi.getFatorRotatividade());
        pontoTaxiDTO.setDataCriacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(pontoTaxi.getDataCriacao()));

        return  pontoTaxiDTO;
    }

    public PontoTaxi converterPontoTaxiDTOToPontoTaxi(PontoTaxiDTO pontoTaxiDTO){
        PontoTaxi pontoTaxi = new PontoTaxi();
        if (pontoTaxiDTO.getIdPontosTaxi() != null && pontoTaxiDTO.getIdPontosTaxi() != 0){
            pontoTaxi = pontosTaxiRepository.findByIdPontosTaxi(pontoTaxiDTO.getIdPontosTaxi());
        }
        pontoTaxi.setDescricaoPonto(pontoTaxiDTO.getDescricaoPonto());
        pontoTaxi.setNumeroPonto(pontoTaxiDTO.getNumeroPonto());
        pontoTaxi.setReferenciaPonto(pontoTaxiDTO.getReferenciaPonto());
        pontoTaxi.setFatorRotatividade(pontoTaxiDTO.getFatorRotatividade());

        return  pontoTaxi;
    }

}
