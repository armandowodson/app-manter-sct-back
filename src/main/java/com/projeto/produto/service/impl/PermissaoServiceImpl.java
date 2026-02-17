package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.dto.PermissaoDTO;
import com.projeto.produto.dto.PermissaoRelatorioDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Defensor;
import com.projeto.produto.entity.Permissao;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PermissaoRepository;
import com.projeto.produto.utils.FormataData;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PermissaoServiceImpl {
    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    private static final Logger logger = LogManager.getLogger(PermissaoServiceImpl.class);

    @Transactional
    public PermissaoDTO inserirPermissao(PermissaoDTO permissaoDTO) {
        logger.info("Início Inserir Permissão");
        if(Objects.isNull(permissaoDTO.getUsuario()) || permissaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Permissao permissao = new Permissao();
        Long numeroMaximo = permissaoRepository.buscarNumeroMaximo();
        permissaoDTO.setNumeroPermissao(String.valueOf(numeroMaximo + 1));

        try{
            permissao = converterPermissaoDTOToPermissao(permissaoDTO, 1);
            permissao = permissaoRepository.save(permissao);

            //Auditoria
            salvarAuditoria("PERMISSÃO", "INCLUSÃO", permissaoDTO.getUsuario());

            return converterPermissaoToPermissaoDTO(permissao);
        } catch (Exception e){
            logger.error("inserirPermissao: " + e.getMessage());
            throw new RuntimeException("Não foi possível inserir os dados da Permissão!");
        }
    }

    @Transactional
    public PermissaoDTO atualizarPermissao(PermissaoDTO permissaoDTO) {
        logger.info("Início Atualizar Permissão");
        if(Objects.isNull(permissaoDTO.getUsuario()) || permissaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Permissao permissao = new Permissao();
        try{
            permissao =  converterPermissaoDTOToPermissao(permissaoDTO, 2);
            permissao = permissaoRepository.save(permissao);
            //Auditoria
            salvarAuditoria("PERMISSÃO", "ALTERAÇÃO", permissaoDTO.getUsuario());

            return converterPermissaoToPermissaoDTO(permissao);
        } catch (Exception e){
            logger.error("atualizarPermissao: " + e.getMessage());
            throw new RuntimeException("Não foi possível alterar os dados da Permissão!");
        }
    }

    public Page<PermissaoDTO> listarTodosPermissao(PageRequest pageRequest) {
        logger.info("Início Listar Todas as Permissões");
        try {
            List<Permissao> listaPermissao = permissaoRepository.buscarTodas(pageRequest);
            Integer countLista = permissaoRepository.buscarTodas(null).size();
            List<PermissaoDTO> permissaoDTOList = converterEntityToDTO(listaPermissao);
            return new PageImpl<>(permissaoDTOList, pageRequest, countLista);
        } catch (Exception e){
            logger.error("listarTodosPermissao: " + e.getMessage());
            throw new RuntimeException("Não foi possível Listar Todas as Permissões!");
        }
    }

    public PermissaoDTO buscarPermissaoId(Long idPermissao) {
        logger.info("Início Buscar Permissão por ID");
        Permissao permissao = permissaoRepository.findById(idPermissao).get();
        PermissaoDTO permissaoDTO = new PermissaoDTO();
        if (permissao != null){
            permissaoDTO = converterPermissaoToPermissaoDTO(permissao);
        }
        return permissaoDTO;
    }

    public Page<PermissaoDTO> listarTodasPermissoesFiltros( String numeroPermissao, String numeroAlvara,
                                                           String anoPermissao, String statusPermissao,
                                                           String periodoInicial, String periodoFinal,
                                                           PageRequest pageRequest) {

        LocalDate localDateInicial = null;
        if(Objects.nonNull(periodoInicial) && !periodoInicial.isEmpty()){
            String data = periodoInicial;
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                localDateInicial = zonedDateTime.toLocalDate();
            }
        }else{
            localDateInicial = LocalDate.parse("2026-01-01");
        }

        LocalDate localDateFinal = null;
        if(Objects.nonNull(periodoFinal) && !periodoFinal.isEmpty()){
            String data = periodoFinal;
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                localDateFinal = zonedDateTime.toLocalDate();
            }
        }else{
            localDateFinal = LocalDate.parse("2099-01-01");
        }

        List<Permissao> listaPermissao = permissaoRepository.listarTodasPermissoesFiltros(
                numeroPermissao, numeroAlvara,  anoPermissao,  statusPermissao,
                localDateInicial != null ? localDateInicial : null,
                localDateFinal != null ? localDateFinal : null,  pageRequest
        );

        Integer countRegistros = permissaoRepository.listarTodasPermissoesFiltros(
                numeroPermissao, numeroAlvara,  anoPermissao,  statusPermissao,
                localDateInicial != null ? localDateInicial : null,
                localDateFinal != null ? localDateFinal : null,  null
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

    public List<PermissaoDTO> listarPermissoesDisponiveisDefensor(String numeroPermissao) {
        List<PermissaoDTO> listaPermissaoDTO = new ArrayList<>();
        List<Permissao> listaPermissao = permissaoRepository.listarPermissaoDisponiveisDefensor();
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
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Permissao permissao = permissaoRepository.findPermissaoByIdPermissao(idPermissao);
            if(permissaoRepository.verificarPermissoaExistente(permissao.getNumeroPermissao()) > 0)
                throw new RuntimeException("Não é possível realizar a exclusão. A Permissão de Nº " + permissao.getNumeroPermissao() +
                        " está sendo utilizada por um Permissionário!");

            permissao.setStatusPermissao("8");
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
        permissaoDTO.setAnoPermissao(permissao.getAnoPermissao());
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
        permissaoDTO.setStatus(permissao.getStatus());

        return  permissaoDTO;
    }

    public Permissao converterPermissaoDTOToPermissao(PermissaoDTO permissaoDTO, Integer tipo){
        Permissao permissao = new Permissao();
        if (permissaoDTO.getIdPermissao() != null && permissaoDTO.getIdPermissao() != 0){
            permissao = permissaoRepository.findById(permissaoDTO.getIdPermissao()).get();
        }
        permissao.setNumeroAlvara(permissaoDTO.getNumeroAlvara());
        permissao.setAnoPermissao(permissaoDTO.getAnoPermissao());

        permissao.setCategoriaPermissao(permissaoDTO.getCategoriaPermissao());

        permissao.setStatusPermissao(permissaoDTO.getStatusPermissao());

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

        permissao.setPenalidade(permissaoDTO.getPenalidade());

        if(Objects.nonNull(permissaoDTO.getDataValidadePenalidade()) && permissaoDTO.getDataValidadePenalidade().equals("null")){
            permissao.setDataValidadePenalidade(null);
            permissaoDTO.setDataValidadePermissao(null);
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

        permissao.setModalidade(permissaoDTO.getModalidade());

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

    public byte[] gerarRelatorio(String anoPermissao, String statusPermissao, String dataInicioGeracao, String dataFimGeracao) {
        logger.info("Início Gerar Relatório Busca dos Dados");
        try{
            LocalDate localDateInicial = null;
            if(Objects.nonNull(dataInicioGeracao) && !dataInicioGeracao.isEmpty()){
                String data = dataInicioGeracao;
                Integer indexChar = data.indexOf('(');
                if(indexChar > 0){
                    data = data.substring(0, indexChar);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                    localDateInicial = zonedDateTime.toLocalDate();
                }
            }else{
                localDateInicial = LocalDate.parse("2026-01-01");
            }

            LocalDate localDateFinal = null;
            if(Objects.nonNull(dataFimGeracao) && !dataFimGeracao.isEmpty()){
                String data = dataFimGeracao;
                Integer indexChar = data.indexOf('(');
                if(indexChar > 0){
                    data = data.substring(0, indexChar);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                    localDateFinal = zonedDateTime.toLocalDate();
                }
            }else{
                localDateFinal = LocalDate.parse("2099-01-01");
            }

            List<Permissao> listaPermissoesRelatorio = permissaoRepository.listarTodasPermissoesRelatorio(
                    anoPermissao, statusPermissao, localDateInicial, localDateFinal
            );

            Integer totalGerada1 = 0;
            Integer totalEmUso2 = 0;
            Integer totalAbandonada9 = 0;
            if(Objects.nonNull(listaPermissoesRelatorio) && !listaPermissoesRelatorio.isEmpty()){
                List<PermissaoRelatorioDTO> permissaoRelatorioDTOList = new ArrayList<>();
                for(Permissao permissao : listaPermissoesRelatorio){
                    PermissaoRelatorioDTO permissaoRelatorioDTO = new PermissaoRelatorioDTO();
                    permissaoRelatorioDTO.setNumeroPermissao(permissao.getNumeroPermissao());
                    permissaoRelatorioDTO.setAnoPermissao(permissao.getAnoPermissao());
                    permissaoRelatorioDTO.setCategoriaPermissao(carregarCategoriaPermissao(permissao.getCategoriaPermissao()));
                    permissaoRelatorioDTO.setStatusPermissao(carregarStatusPermissao(permissao.getStatusPermissao()));
                    if(permissao.getStatusPermissao().equals("1"))
                        totalGerada1++;
                    if(permissao.getStatusPermissao().equals("2"))
                        totalEmUso2++;
                    if(permissao.getStatusPermissao().equals("9"))
                        totalAbandonada9++;
                    permissaoRelatorioDTO.setPeriodoStatus(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getPeriodoInicialStatus()) +
                            " à " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getPeriodoFinalStatus())
                    );
                    permissaoRelatorioDTO.setDataValidadePermissao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataValidadePermissao()));
                    permissaoRelatorioDTO.setNumeroAutorizacaoTrafego(permissao.getAutorizacaoTrafego());
                    permissaoRelatorioDTO.setModalidade(carregarModalidade(permissao.getModalidade()));
                    permissaoRelatorioDTO.setDataCriacao(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataCriacao()));
                    permissaoRelatorioDTOList.add(permissaoRelatorioDTO);
                }

                byte[] bytes = gerarRelatorioJasper(anoPermissao, statusPermissao, dataInicioGeracao, dataFimGeracao,
                        totalGerada1, totalEmUso2, totalAbandonada9, permissaoRelatorioDTOList);
                return bytes;
            }else{
                throw new RuntimeException("Não foi possível gerar o relatório. Lista Vazia!");
            }
        } catch (Exception e){
            logger.error("gerarRelatorio - Permissão: " + e.getMessage());
            throw new RuntimeException("Erro ao Gerar Relatório de Permissão");
        }
    }

    public byte[] gerarRelatorioJasper(String anoPermissao, String statusPermissao, String dataInicioGeracao, String dataFimGeracao,
                                       Integer totalGerada1, Integer totalEmUso2, Integer totalAbandonada9,
                                       List<PermissaoRelatorioDTO> permissaoRelatorioDTOList) {
        logger.info("Início Gerar Reltório Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/relatorioPermissao.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream logoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/LogoPrefeitura.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemPath", logoStream);
            parameters.put("anoPermissao", Objects.nonNull(anoPermissao) ? anoPermissao : "");
            parameters.put("statusPermissao", Objects.nonNull(statusPermissao) ? carregarStatusPermissao(statusPermissao) : "");
            parameters.put("dataInicioGeracao", Objects.nonNull(dataInicioGeracao) ? FormataData.formatarDataLocalDate(dataInicioGeracao) : "");
            parameters.put("dataFimGeracao", Objects.nonNull(dataFimGeracao) ? FormataData.formatarDataLocalDate(dataFimGeracao) : "");
            parameters.put("totalGerada", totalGerada1);
            parameters.put("totalEmUso", totalEmUso2);
            parameters.put("totalAbandonada", totalAbandonada9);

            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "1978");
            Statement stm = connection.createStatement();
            String query = "";
            if(Objects.nonNull(permissaoRelatorioDTOList)){
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(permissaoRelatorioDTOList);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
                return bytes;
            }else{
                query = " SELECT NUMERO_PERMISSAO numeroPermissao, ANO_PERMISSAO anoPermissao, CATEGORIA_PERMISSAO categoriaPermissao," +
                        " STATUS_PERMISSAO statusPermissao, " +
                        " TO_CHAR(PERIODO_INICIAL_STATUS, 'dd/MM/yyyy') || ' à ' || TO_CHAR(PERIODO_FINAL_STATUS, 'dd/MM/yyyy') periodoStatus," +
                        " TO_CHAR(DATA_VALIDADE_PERMISSAO, 'dd/MM/yyyy') DATA_VALIDADE_PERMISSAO dataValidadePermissao, MODALIDADE modalidade," +
                        " NUMERO_AUTORIZACAO_TRAFEGO numeroAutorizacaoTrafego, TO_CHAR(DATA_CRIACAO, 'dd/MM/yyyy') DATA_CRIACAO dataCriacao " +
                        " FROM PROJ.PERMISSAO";
                ResultSet rs = stm.executeQuery( query );
                JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrRS);

                byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
                return bytes;
            }
        } catch (Exception e){
            logger.error("gerarRelatorio: " + e.getMessage());
            throw new RuntimeException("Erro ao Gerar Reltório");
        }
    }

    public String carregarCategoriaPermissao(String categoria) {
        String strCategoria = "";
        switch (categoria) {
            case "1":
                strCategoria = "DELEGAÇÃO";
                break;
            case "2":
                strCategoria = "TÍTULO PRECÁRIO";
                break;
            case "3":
                strCategoria = "LICITAÇÃO";
                break;
            case "4":
                strCategoria = "PRESTAÇÃO DO SPTA";
                break;
        }

        return strCategoria;
    }

    public String carregarStatusPermissao(String status) {
        String strStatus = "";
        switch (status) {
            case "1":
                strStatus = "GERADA";
                break;
            case "2":
                strStatus = "EM USO";
                break;
            case "3":
                strStatus = "SUSPENSA";
                break;
            case "4":
                strStatus = "RENUNCIADA";
                break;
            case "5":
                strStatus = "RESERVADA";
                break;
            case "6":
                strStatus = "SUBSTITUÍDA";
                break;
            case "7":
                strStatus = "REVOGADA";
                break;
            case "8":
                strStatus = "EXPIRADA";
                break;
            case "9":
                strStatus = "ABANDONADA";
                break;
        }

        return strStatus;
    }

    public String carregarModalidade(String modalidade) {
        String strModalidade = "";
        switch (modalidade) {
            case "1":
                strModalidade = "FIXO";
                break;
            case "2":
                strModalidade = "ROTATIVO";
                break;
            case "3":
                strModalidade = "FIXO-ROTATIVO";
                break;
        }
        return strModalidade;
    }
}
