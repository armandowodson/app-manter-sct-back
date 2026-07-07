package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.dto.TasDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PermissionarioRepository;
import com.projeto.produto.repository.VeiculoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TasServiceImpl {

    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    private static final Logger logger = LogManager.getLogger(TasServiceImpl.class);

    public Page<TasDTO> listarTodosTas(PageRequest pageRequest) {
        logger.info("Início Listar Todos TAS");
        try {
            List<TasDTO> tasDTOList = new ArrayList<>();

            List<Permissionario> listaPermissionario = permissionarioRepository.buscarTodos(pageRequest);
            Integer countLista = listaPermissionario.size();

            for(Permissionario permissionario : listaPermissionario){
                tasDTOList.add(montarTasDto(permissionario));
            }

            return new PageImpl<>(tasDTOList, pageRequest, countLista);
        } catch (Exception e){
            logger.error("listarTodosAuditoria: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todos os TAS");
        }
    }

    public Page<TasDTO> listarTodosTasFiltros(String operacao, String qtdDias, String inicioValidadeTas,
                                              String fimValidadeTas, PageRequest pageRequest) {
        logger.info("Início Listar Todos os TAS por Filtro");
        try{
            LocalDate localDateInicio = null;
            if(Objects.nonNull(inicioValidadeTas) && !inicioValidadeTas.isEmpty()) {
                localDateInicio = LocalDate.parse(inicioValidadeTas);
            }

            LocalDate localDateFim = null;
            if(Objects.nonNull(fimValidadeTas) && !fimValidadeTas.isEmpty()) {
                localDateFim = LocalDate.parse(fimValidadeTas);
            }

            List<TasDTO> tasDTOList = new ArrayList<>();

            List<Permissionario> listaPermissionario = permissionarioRepository.buscarTodos(pageRequest);

            Integer countRegistros = listaPermissionario.size();


            for(Permissionario permissionario : listaPermissionario){
                Boolean adicionarNaLista = false;
                TasDTO tasDTOCheck = montarTasDto(permissionario);
                if(Objects.nonNull(qtdDias) && !qtdDias.isEmpty() && Objects.nonNull(operacao) && !operacao.isEmpty()){
                    if(operacao.equals(">=")) {
                        if(Integer.valueOf(qtdDias) >= Integer.valueOf(tasDTOCheck.getQtdDiasVencer()))
                            adicionarNaLista = true;
                    }
                    if(operacao.equals("<=")) {
                        if(Integer.valueOf(qtdDias) <= Integer.valueOf(tasDTOCheck.getQtdDiasVencer()))
                            adicionarNaLista = true;
                    }
                }

                if(Objects.nonNull(localDateInicio) && Objects.isNull(localDateFim)){
                    if(permissionario.getDataCriacao().isAfter(localDateInicio))
                        adicionarNaLista = true;
                }
                if(Objects.nonNull(localDateFim) && Objects.isNull(localDateInicio)){
                    if(permissionario.getDataCriacao().plusYears(3).isBefore(localDateFim))
                        adicionarNaLista = true;
                }
                if(Objects.nonNull(localDateInicio) && Objects.nonNull(localDateFim)){
                    if(permissionario.getDataCriacao().isAfter(localDateInicio) &&
                            permissionario.getDataCriacao().plusYears(3).isBefore(localDateFim))
                        adicionarNaLista = true;
                }

                if((Objects.isNull(qtdDias) || qtdDias.isEmpty()) && (Objects.isNull(operacao) && operacao.isEmpty()) &&
                        (Objects.isNull(inicioValidadeTas) || inicioValidadeTas.isEmpty()) &&
                        (Objects.isNull(fimValidadeTas) || fimValidadeTas.isEmpty())){
                    adicionarNaLista = true;
                }

                if(adicionarNaLista)
                    tasDTOList.add(tasDTOCheck);
            }

            return new PageImpl<>(tasDTOList, pageRequest, countRegistros);
        } catch (Exception e){
            logger.error("listarTodosAuditoriaFiltros: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todas Auditorias por Filtro");
        }
    }

    public byte[] imprimirRelatorio(String operacao, String qtdDias, String inicioValidadeTas,
                                    String fimValidadeTas, PageRequest pageRequest) {
        logger.info("Início Imprimir TAS");
        try{
            LocalDate localDateInicio = null;
            if(Objects.nonNull(inicioValidadeTas) && !inicioValidadeTas.isEmpty()) {
                localDateInicio = LocalDate.parse(inicioValidadeTas);
            }

            LocalDate localDateFim = null;
            if(Objects.nonNull(fimValidadeTas) && !fimValidadeTas.isEmpty()) {
                localDateFim = LocalDate.parse(fimValidadeTas);
            }

            List<TasDTO> tasDTOList = new ArrayList<>();

            List<Permissionario> listaPermissionario = permissionarioRepository.buscarTodos(pageRequest);

            Integer countRegistros = listaPermissionario.size();


            for(Permissionario permissionario : listaPermissionario){
                Boolean adicionarNaLista = false;
                TasDTO tasDTOCheck = montarTasDto(permissionario);
                if(Objects.nonNull(qtdDias) && !qtdDias.isEmpty() && Objects.nonNull(operacao) && !operacao.isEmpty()){
                    if(operacao.equals(">=")) {
                        if(Integer.valueOf(qtdDias) >= Integer.valueOf(tasDTOCheck.getQtdDiasVencer()))
                            adicionarNaLista = true;
                    }
                    if(operacao.equals("<=")) {
                        if(Integer.valueOf(qtdDias) <= Integer.valueOf(tasDTOCheck.getQtdDiasVencer()))
                            adicionarNaLista = true;
                    }
                }

                if(Objects.nonNull(localDateInicio) && Objects.isNull(localDateFim)){
                    if(permissionario.getDataCriacao().isAfter(localDateInicio))
                        adicionarNaLista = true;
                }
                if(Objects.nonNull(localDateFim) && Objects.isNull(localDateInicio)){
                    if(permissionario.getDataCriacao().plusYears(3).isBefore(localDateFim))
                        adicionarNaLista = true;
                }
                if(Objects.nonNull(localDateInicio) && Objects.nonNull(localDateFim)){
                    if(permissionario.getDataCriacao().isAfter(localDateInicio) &&
                            permissionario.getDataCriacao().plusYears(3).isBefore(localDateFim))
                        adicionarNaLista = true;
                }

                if((Objects.isNull(qtdDias) || qtdDias.isEmpty()) && (Objects.isNull(operacao) && operacao.isEmpty()) &&
                        (Objects.isNull(inicioValidadeTas) || inicioValidadeTas.isEmpty()) &&
                        (Objects.isNull(fimValidadeTas) || fimValidadeTas.isEmpty())){
                    adicionarNaLista = true;
                }

                if(adicionarNaLista)
                    tasDTOList.add(tasDTOCheck);
            }

            byte[] bytes = gerarRelatorio(operacao, qtdDias, inicioValidadeTas, fimValidadeTas, tasDTOList);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirTas: " + e.getMessage());
            throw new RuntimeException("Erro ao Imprimir TAS");
        }
    }

    public TasDTO montarTasDto(Permissionario permissionario){
        TasDTO tasDTO = new TasDTO();
        Veiculo veiculo = veiculoRepository.findVeiculoByPermissionario(permissionario);
        tasDTO.setNumeroTas(StringUtils.leftPad(permissionario.getIdPermissionario().toString() + veiculo.getIdVeiculo().toString(), 8, "0") + "/" + permissionario.getDataCriacao().getYear());
        tasDTO.setNomeAutorizatario(permissionario.getNomePermissionario());
        tasDTO.setCpfAutorizatario(permissionario.getCpfPermissionario());
        tasDTO.setPlaca(veiculo.getPlaca());
        tasDTO.setRenavam(veiculo.getRenavam());
        tasDTO.setInicioValidadeTas(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataCriacao()));
        tasDTO.setFimValidadeTas(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataCriacao().plusYears(3)));
        LocalDate localDateAtual = LocalDate.now();
        String qtdDias = String.valueOf(ChronoUnit.DAYS.between(localDateAtual, permissionario.getDataCriacao().plusYears(3)));
        tasDTO.setQtdDiasVencer(qtdDias);

        return tasDTO;
    }

    public byte[] gerarRelatorio( String operacao, String qtdDias, String inicioValidadeTas,
                                  String fimValidadeTas, List<TasDTO> tasDTOList) throws JRException, SQLException, IOException {
        logger.info("Início Gerar Reltório");
        try{
            ClassPathResource resource = new ClassPathResource("reports/relatorioTas.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream  logoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/tituloRelatorioTas.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            parameters.put("imagemPath", logoStream);
            parameters.put("qtDias", Objects.nonNull(qtdDias) ? operacao + " " +  qtdDias : "");
            parameters.put("inicioValidadeTas", (Objects.nonNull(inicioValidadeTas) && !inicioValidadeTas.isEmpty()) ?
                    LocalDate.parse(inicioValidadeTas).format(formatter) : "");
            parameters.put("fimValidadeTas", (Objects.nonNull(fimValidadeTas) && !fimValidadeTas.isEmpty()) ?
                    LocalDate.parse(fimValidadeTas).format(formatter) : "");

            parameters.put("operacao", Objects.nonNull(operacao) ? operacao : "");

            LocalDate dataEmissao = LocalDate.now();
            parameters.put("dataEmissaoRelatorio", dataEmissao.format(formatter));

            Integer qtdRegular = 0;
            Integer qtdVencido = 0;
            List<TasDTO> tasDTOListFinal = new ArrayList<>();
            for(TasDTO tasDTO : tasDTOList){
                if(Integer.valueOf(tasDTO.getQtdDiasVencer()) <= 0){
                    tasDTO.setVencido("SIM");
                    qtdVencido++;
                }else{
                    tasDTO.setVencido("NÃO");
                    qtdRegular++;
                }
                tasDTOListFinal.add(tasDTO);
            }
            parameters.put("qtdRegular", qtdRegular);
            parameters.put("qtdVencido", qtdVencido);

            if(Objects.nonNull(tasDTOListFinal)){
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(tasDTOListFinal);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
                return bytes;
            }else{
                throw new SQLException();
            }
        } catch (SQLException s) {
            logger.error("gerarRelatorio: " + s.getMessage());
            throw new RuntimeException("Não há dados para gerar Relatório");
        } catch (Exception e){
            logger.error("gerarRelatorio: " + e.getMessage());
            throw new RuntimeException("Erro ao Gerar Reltório");
        }
    }

}
