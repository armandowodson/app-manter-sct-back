package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.repository.AuditoriaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        List<Auditoria> listaAuditoria;

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

    public void gerarRelatorio() throws JRException, SQLException, IOException {
        ClassPathResource resource = new ClassPathResource("reports/auditoria.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        //JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/reports/auditoria.jrxml");
        FileInputStream  logoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/LogoPrefeitura.png" ).getAbsolutePath());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("imagemPath", logoStream);
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "1978");
        Statement stm = connection.createStatement();
        String query = "SELECT * FROM PROJ.AUDITORIA";
        ResultSet rs = stm.executeQuery( query );
        JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrRS);
        String destinationFileName = "C:/exports/final_report.pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint, "C:/Relatorios/auditoria-" + LocalDate.now() + ".pdf");
        //JasperViewer viewer = new JasperViewer( jasperPrint , true );
        //viewer.show();
    }

}
