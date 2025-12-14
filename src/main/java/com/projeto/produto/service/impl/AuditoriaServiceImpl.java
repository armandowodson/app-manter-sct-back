package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class AuditoriaServiceImpl {

    @Autowired
    private AuditoriaRepository auditoriaRepository;
    

    public List<AuditoriaDTO> listarTodosAuditoria() {
        List<Auditoria> listaAuditoria = auditoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "nomeModulo"));
        return converterEntityToDTO(listaAuditoria);
    }

    public AuditoriaDTO buscarAuditoriaId(Long idAuditoria) {
        Auditoria auditoria = auditoriaRepository.findByIdAuditoria(idAuditoria);
        AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
        if (auditoria != null){
            auditoriaDTO = converterAuditoriaToAuditoriaDTO(auditoria);
        }
        return auditoriaDTO;
    }

    public List<AuditoriaDTO> listarTodosAuditoriaFiltros(String nomeModulo, String usuarioOperacao,
                                                          String operacao, String dataInicioOperacao,
                                                          String dataFimOperacao) {

        LocalDate localDateInicio = LocalDate.now();
        if(Objects.nonNull(dataInicioOperacao)) {
            String data = dataInicioOperacao;
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                localDateInicio = zonedDateTime.toLocalDate();
            }
        }else{
            localDateInicio = null;
        }

        LocalDate localDateFim = LocalDate.now();
        if(Objects.nonNull(dataFimOperacao)) {
            String data = dataFimOperacao;
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                localDateFim = zonedDateTime.toLocalDate();
            }
        }else{
            localDateFim = null;
        }

        List<Auditoria> listaAuditoria = new ArrayList<>();

        if(localDateInicio != null && localDateFim != null){
            listaAuditoria = auditoriaRepository.listarTodasAuditoriasFiltros(
                    nomeModulo != null ? nomeModulo.toUpperCase() : null,
                    usuarioOperacao, operacao, localDateInicio, localDateFim
            );
        }else{
            listaAuditoria = auditoriaRepository.listarTodasAuditoriasFiltrosSemDatas(
                    nomeModulo != null ? nomeModulo.toUpperCase() : null,
                    usuarioOperacao, operacao
            );
        }

        List<AuditoriaDTO> listaAuditoriaDTO = new ArrayList<>();
        if (!listaAuditoria.isEmpty()){
            for (Auditoria auditoria : listaAuditoria) {
                AuditoriaDTO auditoriaDTORetornado = converterAuditoriaToAuditoriaDTO(auditoria);
                listaAuditoriaDTO.add(auditoriaDTORetornado);
            }
        }

        return listaAuditoriaDTO;
    }

    public List<AuditoriaDTO> converterEntityToDTO(List<Auditoria> listaAuditoria){
        List<AuditoriaDTO> listaAuditoriaDTO = new ArrayList<>();
        for(Auditoria auditoria : listaAuditoria){
            AuditoriaDTO auditoriaDTO = converterAuditoriaToAuditoriaDTO(auditoria);
            listaAuditoriaDTO.add(auditoriaDTO);
        }

        return  listaAuditoriaDTO;
    }

    public AuditoriaDTO converterAuditoriaToAuditoriaDTO(Auditoria auditoria){
        AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
        if (auditoria.getIdAuditoria() != null){
            auditoriaDTO.setIdAuditoria(auditoria.getIdAuditoria());
        }
        auditoriaDTO.setNomeModulo(auditoria.getNomeModulo());
        auditoriaDTO.setUsuarioOperacao(auditoria.getUsuarioOperacao());
        auditoriaDTO.setOperacao(auditoria.getOperacao());
        auditoriaDTO.setDataOperacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(auditoria.getDataOperacao()));

        return  auditoriaDTO;
    }

}
