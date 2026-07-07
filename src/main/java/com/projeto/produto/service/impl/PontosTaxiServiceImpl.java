package com.projeto.produto.service.impl;

import com.projeto.produto.dto.AuditoriaDTO;
import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PontosTaxiRepository;
import com.projeto.produto.repository.VeiculoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PontosTaxiServiceImpl {
    @Autowired
    private PontosTaxiRepository pontosTaxiRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    private static final Logger logger = LogManager.getLogger(PontosTaxiServiceImpl.class);

    @Transactional
    public PontoTaxiDTO inserirPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        if(Objects.isNull(pontoTaxiDTO.getUsuario()) || pontoTaxiDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        PontoTaxi pontoTaxiExiste = pontosTaxiRepository.findPontoTaxiByNumeroPonto(pontoTaxiDTO.getNumeroPonto());
        if(Objects.nonNull(pontoTaxiExiste))
            throw new RuntimeException("O Ponto de Nº " + pontoTaxiDTO.getNumeroPonto() + " já existe!");

        PontoTaxi pontoTaxi = new PontoTaxi();
        try{
            pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
            pontoTaxi.setDataCriacao(LocalDate.now());
            pontoTaxi = pontosTaxiRepository.save(pontoTaxi);

            //Auditoria
            salvarAuditoria("PONTO DE ESTACIONAMENTO DE MOTO TÁXI", "INCLUSÃO", pontoTaxiDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Ponto de Estacionamento de Moto Táxi!");
        }

        return converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
    }

    @Transactional
    public PontoTaxiDTO atualizarPontoTaxi(PontoTaxiDTO pontoTaxiDTO) {
        if(Objects.isNull(pontoTaxiDTO.getUsuario()) || pontoTaxiDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        PontoTaxi pontoTaxi = new PontoTaxi();
        try{
            pontoTaxi = converterPontoTaxiDTOToPontoTaxi(pontoTaxiDTO);
            pontoTaxi = pontosTaxiRepository.save(pontoTaxi);
            //Auditoria
            salvarAuditoria("PONTO DE ESTACIONAMENTO DE MOTO TÁXI", "ALTERAÇÃO", pontoTaxiDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível alterar os dados do Ponto de Estacionamento de Moto Táxi!");
        }

        return converterPontoTaxiToPontoTaxiDTO(pontoTaxi);
    }

    public Page<PontoTaxiDTO> listarTodosPontosTaxi(PageRequest pageRequest) {
        List<PontoTaxi> listaPontosTaxi = pontosTaxiRepository.buscarTodos(pageRequest);
        Integer countLista = pontosTaxiRepository.buscarTodos(null).size();
        List<PontoTaxiDTO> pontosTaxiDTOList = converterEntityToDTO(listaPontosTaxi);
        return new PageImpl<>(pontosTaxiDTOList, pageRequest, countLista);
    }

    public PontoTaxiDTO buscarPontoTaxiId(Integer idPontoTaxi) {
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

    public List<PontoTaxiDTO> listarPontosTaxiDisponiveis() {
        List<PontoTaxiDTO> listaPontoTaxiDTO = new ArrayList<>();
        List<PontoTaxi> listaPontoTaxi = pontosTaxiRepository.listarPontosTaxiDisponiveis();

        if (!listaPontoTaxi.isEmpty()){
            for (PontoTaxi pontoTaxiDisponivel : listaPontoTaxi) {
                PontoTaxiDTO pontoTaxiDTORetornado = converterPontoTaxiToPontoTaxiDTO(pontoTaxiDisponivel);
                listaPontoTaxiDTO.add(pontoTaxiDTORetornado);
            }
        }

        return listaPontoTaxiDTO;
    }

    public byte[] imprimirPontosEstacionamentosMotoTaxi(String numeroPonto, String descricaoPonto,
                                                        String fatorRotatividade, String numeroVagas,
                                                        String referenciaPonto, PageRequest pageRequest) {
        logger.info("Início Imprimir Pontos de Estacionamentos de Moto Táxi");
        try{
            Page<PontoTaxiDTO> listaPontosTaxiPage = listarTodosPontosTaxiFiltros(numeroPonto, descricaoPonto, fatorRotatividade,
                    numeroVagas, referenciaPonto, null, pageRequest);
            List<PontoTaxiDTO> listaPontosTaxi = listaPontosTaxiPage.getContent();
            byte[] bytes = gerarRelatorio(numeroPonto, descricaoPonto, fatorRotatividade, numeroVagas, referenciaPonto, listaPontosTaxi);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirPontosTaxi: " + e.getMessage());
            throw new RuntimeException("Erro ao Imprimir Pontos de Estacionamentos de Moto Táxi");
        }
    }

    public byte[] gerarRelatorio(String numeroPonto, String descricaoPonto, String fatorRotatividade, String numeroVagas,
                                 String referenciaPonto, List<PontoTaxiDTO> listaPontosTaxi) {
        logger.info("Início Gerar Reltório");
        List<PontoTaxiDTO> listaPontosTaxiFinal = new ArrayList<>();
        try{
            ClassPathResource resource = new ClassPathResource("reports/relatorioPontosEstacionamentosMotoTaxi.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream logoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/tituloRelatorioPontosEstacionamentosMotoTaxi.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemPath", logoStream);
            parameters.put("numeroPonto", Objects.nonNull(numeroPonto) ? numeroPonto : "");
            parameters.put("descricaoPonto", Objects.nonNull(descricaoPonto) ? descricaoPonto : "");
            parameters.put("referenciaPonto", Objects.nonNull(referenciaPonto) ? referenciaPonto : "");
            parameters.put("numeroVagas", Objects.nonNull(numeroVagas) ? numeroVagas : "");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataEmissao = LocalDate.now();
            parameters.put("dataEmissaoRelatorio", dataEmissao.format(formatter));

            if(Objects.nonNull(listaPontosTaxi)){

                Integer totalVagas = 0;
                Integer totalFixo = 0;
                Integer totalRotativo = 0;
                Integer totalFixoRotativo = 0;

                for(PontoTaxiDTO pontoTaxiDTO : listaPontosTaxi){
                    totalVagas = totalVagas + Integer.valueOf(pontoTaxiDTO.getNumeroVagas());
                    if (pontoTaxiDTO.getModalidade().equals("1")){
                        totalFixo++;
                        pontoTaxiDTO.setModalidade("FIXO");
                    }

                    if (pontoTaxiDTO.getModalidade().equals("2")){
                        totalRotativo++;
                        pontoTaxiDTO.setModalidade("ROTATIVO");
                    }

                    if (pontoTaxiDTO.getModalidade().equals("3")){
                        totalFixoRotativo++;
                        pontoTaxiDTO.setModalidade("FIXO-ROTATIVO");
                    }

                    listaPontosTaxiFinal.add(pontoTaxiDTO);
                }

                parameters.put("totalVagas", String.valueOf(totalVagas));
                List<Veiculo> listaVeiculos = veiculoRepository.buscarVeiculosAtivos();
                Integer totalVagasOcupadas = listaVeiculos.size();
                parameters.put("totalVagasOcupadas", String.valueOf(totalVagasOcupadas));
                if(totalVagas.compareTo(0) != 0){
                    BigDecimal percentualOcupacao = new BigDecimal(totalVagasOcupadas).multiply(new BigDecimal(100)).divide(new BigDecimal(totalVagas), 2, RoundingMode.HALF_UP);
                    parameters.put("percentualOcupacao", String.valueOf(percentualOcupacao));
                } else {
                    parameters.put("percentualOcupacao", "0");
                }

                parameters.put("totalFixo", String.valueOf(totalFixo));
                parameters.put("totalRotativo", String.valueOf(totalRotativo));
                parameters.put("totalFixoRotativo", String.valueOf(totalFixoRotativo));

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaPontosTaxiFinal);
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

    @Transactional
    public ResponseEntity<Void> excluirPontoTaxi(Integer idPontoTaxi, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            PontoTaxi pontoTaxi = pontosTaxiRepository.findByIdPontoTaxi(idPontoTaxi);
            pontoTaxi.setStatus("INATIVO");
            pontosTaxiRepository.save(pontoTaxi);

            //Auditoria
            salvarAuditoria("PONTO DE ESTACIONAMENTO DE MOTO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Ponto de Estacionamento de Moto Táxi!!!");
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
        pontoTaxiDTO.setStatus(pontoTaxi.getStatus());

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
        pontoTaxi.setStatus("ATIVO");

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

    public String converterIdModalidadePermissao(String modalidade){
        switch (modalidade){
            case "1":
                return "FIXO";
            case "2":
                return "ROTATIVO";
            case "3":
                return "FIXO-ROTATIVO";
        }

        return "";
    }

    public String converterNomeModalidade(String modalidade){
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
