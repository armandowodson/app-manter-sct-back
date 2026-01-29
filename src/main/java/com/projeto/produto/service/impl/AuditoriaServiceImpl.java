package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.repository.AuditoriaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AuditoriaServiceImpl {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    private static final Logger logger = LogManager.getLogger(AuditoriaServiceImpl.class);

    public Page<AuditoriaDTO> listarTodosAuditoria(PageRequest pageRequest) {
        logger.info("Início Listar Todas Auditorias");
        try {
            List<Auditoria> listaAuditoria = auditoriaRepository.buscarTodos(pageRequest);
            Integer countLista = auditoriaRepository.buscarTodos(null).size();
            List<AuditoriaDTO> auditoriaDTOList = converterEntityToDTO(listaAuditoria);
            return new PageImpl<>(auditoriaDTOList, pageRequest, countLista);
        } catch (Exception e){
            logger.error("listarTodosAuditoria: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todas Auditorias");
        }
    }

    public AuditoriaDTO buscarAuditoriaId(Long idAuditoria) {
        logger.info("Início Buscar Auditoria por ID");
        try {
            Auditoria auditoria = auditoriaRepository.findByIdAuditoria(idAuditoria);
            AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
            if (auditoria != null){
                auditoriaDTO = converterAuditoriaToAuditoriaDTO(auditoria);
            }
            return auditoriaDTO;
        } catch (Exception e){
            logger.error("buscarAuditoriaId: " + e.getMessage());
            throw new RuntimeException("Erro ao Buscar Auditoria por ID");
        }

    }

    public List<AuditoriaDTO> imprimirAuditoria(String nomeModulo, String usuarioOperacao,
                                                String operacao, String dataInicioOperacao,
                                                String dataFimOperacao, PageRequest pageRequest) throws JRException, SQLException, IOException {
        logger.info("Início Imprimir Auditoria");
        try{
            Page<AuditoriaDTO> listaAuditoriasPage = listarTodosAuditoriaFiltros(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao, pageRequest);
            List<AuditoriaDTO> listaAuditorias = listaAuditoriasPage.getContent();
            gerarRelatorio(nomeModulo, usuarioOperacao, operacao, dataInicioOperacao, dataFimOperacao, listaAuditorias);
            return listaAuditorias;
        } catch (Exception e){
            logger.error("imprimirAuditoria: " + e.getMessage());
            throw new RuntimeException("Erro ao Imprimir Auditoria");
        }
    }

    public Page<AuditoriaDTO> listarTodosAuditoriaFiltros(String nomeModulo, String usuarioOperacao,
                                                          String operacao, String dataInicioOperacao,
                                                          String dataFimOperacao, PageRequest pageRequest) {

        logger.info("Início Listar Todas Auditorias por Filtro");
        try{
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
            Integer countRegistros = 0;

            if(localDateInicio != null && localDateFim != null){
                listaAuditoria = auditoriaRepository.listarTodasAuditoriasFiltros(
                        nomeModulo != null ? nomeModulo.toUpperCase() : null, usuarioOperacao, operacao, localDateInicio, localDateFim, pageRequest
                );
                countRegistros = auditoriaRepository.listarTodasAuditoriasFiltros(
                        nomeModulo != null ? nomeModulo.toUpperCase() : null, usuarioOperacao, operacao, localDateInicio, localDateFim, null
                ).size();
            }else{
                listaAuditoria = auditoriaRepository.listarTodasAuditoriasFiltrosSemDatas(
                        nomeModulo != null ? nomeModulo.toUpperCase() : null, usuarioOperacao, operacao, pageRequest
                );
                countRegistros = auditoriaRepository.listarTodasAuditoriasFiltrosSemDatas(
                        nomeModulo != null ? nomeModulo.toUpperCase() : null, usuarioOperacao, operacao, null
                ).size();
            }

            List<AuditoriaDTO> listaAuditoriaDTO = new ArrayList<>();
            if (!listaAuditoria.isEmpty()){
                for (Auditoria auditoria : listaAuditoria) {
                    AuditoriaDTO auditoriaDTORetornado = converterAuditoriaToAuditoriaDTO(auditoria);
                    listaAuditoriaDTO.add(auditoriaDTORetornado);
                }
            }

            List<AuditoriaDTO> auditoriaDTOList = converterEntityToDTO(listaAuditoria);
            return new PageImpl<>(auditoriaDTOList, pageRequest, countRegistros);
        } catch (Exception e){
            logger.error("listarTodosAuditoriaFiltros: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todas Auditorias por Filtro");
        }
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
        logger.info("Início Converter Auditoria para DTO");
        try{
            AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
            if (auditoria.getIdAuditoria() != null){
                auditoriaDTO.setIdAuditoria(auditoria.getIdAuditoria());
            }
            auditoriaDTO.setNomeModulo(auditoria.getNomeModulo());
            auditoriaDTO.setUsuarioOperacao(auditoria.getUsuarioOperacao());
            auditoriaDTO.setOperacao(auditoria.getOperacao());
            auditoriaDTO.setDataOperacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(auditoria.getDataOperacao()));

            return  auditoriaDTO;
        } catch (Exception e){
            logger.error("converterAuditoriaToAuditoriaDTO: " + e.getMessage());
            throw new RuntimeException("Erro ao Converter Auditoria para DTO");
        }
    }

    public void gerarRelatorio(String nomeModulo, String usuarioOperacao, String operacao, String dataInicioOperacao,
                               String dataFimOperacao, List<AuditoriaDTO> listaAuditorias) throws JRException, SQLException, IOException {
        logger.info("Início Gerar Reltório");
        try{
            ClassPathResource resource = new ClassPathResource("reports/auditoria.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream  logoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/LogoPrefeitura.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemPath", logoStream);
            parameters.put("nomeModulo", Objects.nonNull(nomeModulo) ? nomeModulo : "");
            parameters.put("usuario", Objects.nonNull(usuarioOperacao) ? usuarioOperacao : "");
            parameters.put("operacao", Objects.nonNull(operacao) ? operacao : "");
            parameters.put("dataInicio", Objects.nonNull(dataInicioOperacao) ? formatarData(dataInicioOperacao) : "");
            parameters.put("dataFim", Objects.nonNull(dataFimOperacao) ? formatarData(dataFimOperacao) : "");

            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "1978");
            Statement stm = connection.createStatement();
            String query = "";
            if(Objects.nonNull(listaAuditorias)){
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaAuditorias);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                Long contador = contarArquivos();
                JasperExportManager.exportReportToPdfFile(jasperPrint, "C:/Relatorios/auditoria-" + "Nº" + contador+1 + "-" + LocalDate.now() + ".pdf");
            }else{
                query = "SELECT NOME_MODULO, USUARIO_OPERACAO, OPERACAO, TO_CHAR(DATA_OPERACAO, 'dd/MM/yyyy') DATA_OPERACAO FROM PROJ.AUDITORIA";
                ResultSet rs = stm.executeQuery( query );
                JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrRS);

                Long contador = contarArquivos();
                JasperExportManager.exportReportToPdfFile(jasperPrint, "C:/Relatorios/auditoria-" + "Nº" + contador+1 + "-" + LocalDate.now() + ".pdf");
            }
        } catch (Exception e){
            logger.error("gerarRelatorio: " + e.getMessage());
            throw new RuntimeException("Erro ao Gerar Reltório");
        }
    }

    public Long contarArquivos(){
        logger.info("Início Contar Arquivos");
        Path caminhoDiretorio = Paths.get("C:\\Relatorios");

        Long contador = 0L;
        try (Stream<Path> stream = Files.walk(caminhoDiretorio)) {
            contador = stream
                    .filter(Files::isRegularFile) // Filtra apenas arquivos regulares
                    .count(); // Conta os arquivos
        } catch (IOException e) {
            logger.error("contarArquivos: " + e.getMessage());
        }

        return contador;
    }

    public String formatarData(String dataOperacao){
        LocalDate localDate = LocalDate.now();
        Integer indexChar = dataOperacao.indexOf('(');
        String dataFormatada = "";
        if(indexChar > 0){
            dataOperacao = dataOperacao.substring(0, indexChar);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dataOperacao.trim(), formatter);
            localDate = zonedDateTime.toLocalDate();

            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataFormatada = localDate.format(formatter);
        }

        return dataFormatada;
    }

}
