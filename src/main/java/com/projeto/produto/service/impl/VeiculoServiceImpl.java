package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.*;
import com.projeto.produto.repository.*;
import com.projeto.produto.utils.CarregarTipos;
import net.sf.jasperreports.engine.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VeiculoServiceImpl {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private PontosTaxiRepository pontosTaxiRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    private static final Logger logger = LogManager.getLogger(VeiculoServiceImpl.class);

    @Transactional
    public VeiculoResponseDTO inserirVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                             MultipartFile crlv,
                                             MultipartFile comprovanteVistoria) {
        if(Objects.isNull(veiculoRequestDTO.getUsuario()) || veiculoRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Veiculo veiculo = new Veiculo();
        try{
            veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria, 1);
            veiculo.setDataCriacao(LocalDate.now());
            veiculo = veiculoRepository.save(veiculo);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "INCLUSÃO", veiculoRequestDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Veículo!");
        }

        return converterVeiculoToVeiculoDTO(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizarVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                               MultipartFile crlv,
                                               MultipartFile comprovanteVistoria) {
        if(Objects.isNull(veiculoRequestDTO.getUsuario()) || veiculoRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Veiculo veiculo = new Veiculo();
        try{
            veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria, 2);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "ALTERAÇÃO", veiculoRequestDTO.getUsuario());
        }catch (Exception e){
            throw new RuntimeException("Não foi possível atualizar os dados do Veículo!");
        }

        return converterVeiculoToVeiculoDTO(veiculoRepository.save(veiculo));
    }

    public Page<VeiculoResponseDTO> listarTodosVeiculos(PageRequest pageRequest) {
        List<Veiculo> veiculoList = veiculoRepository.buscarTodos(pageRequest);
        Integer countLista = veiculoRepository.buscarTodos(null).size();
        List<VeiculoResponseDTO> veiculoResponseDTOList = converterEntityToDTO(veiculoList);
        return new PageImpl<>(veiculoResponseDTOList, pageRequest, countLista);
    }

    public VeiculoResponseDTO buscarVeiculoId(Long idVeiculo) {
        Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
        VeiculoResponseDTO veiculoResponseDTO = new VeiculoResponseDTO();
        if (veiculo != null){
            veiculoResponseDTO = converterVeiculoToVeiculoDTO(veiculo);
        }
        return veiculoResponseDTO;
    }

    public Page<VeiculoResponseDTO> listarTodosVeiculosFiltros(String numeroPermissao, String placa,
                                                               String renavam, String numeroTaximetro,
                                                               String anoFabricacao, PageRequest pageRequest) {
        List<Veiculo> listaVeiculos = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao, pageRequest
        );

        Integer countRegistros = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao, null
        ).size();

        List<VeiculoResponseDTO> listaVeiculoResponseDTO = new ArrayList<>();
        if (!listaVeiculos.isEmpty()){
            for (Veiculo veiculo : listaVeiculos) {
                VeiculoResponseDTO veiculoResponseDTORetornado = converterVeiculoToVeiculoDTO(veiculo);
                listaVeiculoResponseDTO.add(veiculoResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaVeiculoResponseDTO, pageRequest, countRegistros);
    }

    public VeiculoResponseDTO buscarVeiculoPlaca(String placa){
        List<Veiculo> veiculoList = veiculoRepository.buscarVeiculoPlaca(placa);

        if(Objects.isNull(veiculoList) || veiculoList.isEmpty())
            throw new RuntimeException("Não foi possível localizar o veículo com a placa " + placa + " informada!");

        switch (veiculoList.get(0).getCor()){
            case "1":
                veiculoList.get(0).setCor("BRANCA");
                break;
            case "2":
                veiculoList.get(0).setCor("PRATA");
                break;
            default:
                veiculoList.get(0).setCor("");
        }

        return converterVeiculoToVeiculoDTO(veiculoList.get(0));
    }

    @Transactional
    public ResponseEntity<Void> excluirVeiculo(Long idVeiculo, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
            veiculo.setStatus("INATIVO");
            veiculoRepository.save(veiculo);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Não foi possível excluir o Veículo!!!");
        }
    }

    public List<VeiculoResponseDTO> converterEntityToDTO(List<Veiculo> listaVeiculos){
        List<VeiculoResponseDTO> listaVeiculosDTO = new ArrayList<>();
        for(Veiculo veiculo : listaVeiculos){
            VeiculoResponseDTO veiculoResponseDTO = converterVeiculoToVeiculoDTO(veiculo);
            listaVeiculosDTO.add(veiculoResponseDTO);
        }

        return  listaVeiculosDTO;
    }

    public VeiculoResponseDTO converterVeiculoToVeiculoDTO(Veiculo veiculo){
        VeiculoResponseDTO veiculoResponseDTO = new VeiculoResponseDTO();
        if (veiculo.getIdVeiculo() != null){
            veiculoResponseDTO.setIdVeiculo(veiculo.getIdVeiculo());
        }

        veiculoResponseDTO.setIdPermissionario(veiculo.getPermissionario().getIdPermissionario());
        veiculoResponseDTO.setNumeroPermissao(veiculo.getNumeroPermissao());
        veiculoResponseDTO.setIdPontoTaxi(veiculo.getPontoTaxi().getIdPontoTaxi());
        veiculoResponseDTO.setPlaca(veiculo.getPlaca().toUpperCase());
        veiculoResponseDTO.setRenavam(veiculo.getRenavam());
        veiculoResponseDTO.setChassi(veiculo.getChassi());
        veiculoResponseDTO.setAnoFabricacao(veiculo.getAnoFabricacao());
        veiculoResponseDTO.setMarca(veiculo.getMarca());
        veiculoResponseDTO.setModelo(veiculo.getModelo());
        veiculoResponseDTO.setAnoModelo(veiculo.getAnoModelo());
        veiculoResponseDTO.setCor(veiculo.getCor());
        veiculoResponseDTO.setCombustivel(veiculo.getCombustivel());
        veiculoResponseDTO.setCapacidade(veiculo.getCapacidade());
        veiculoResponseDTO.setQuilometragem(veiculo.getQuilometragem());
        veiculoResponseDTO.setCrlv(veiculo.getCrlv());
        veiculoResponseDTO.setNumeroTaximetro(veiculo.getNumeroTaximetro());
        veiculoResponseDTO.setAnoRenovacao(veiculo.getAnoRenovacao());
        veiculoResponseDTO.setDataVistoria(veiculo.getDataVistoria().plusDays(1).toString());
        veiculoResponseDTO.setDataRetorno(veiculo.getDataRetorno().plusDays(1).toString());
        veiculoResponseDTO.setStatusVistoria(veiculo.getStatusVistoria());
        veiculoResponseDTO.setComprovanteVistoria(veiculo.getComprovanteVistoria());
        veiculoResponseDTO.setSituacaoVeiculo(veiculo.getSituacaoVeiculo());
        veiculoResponseDTO.setTipoVeiculo(veiculo.getTipoVeiculo());
        veiculoResponseDTO.setNumeroCrlv(veiculo.getNumeroCrlv());
        veiculoResponseDTO.setAnoCrlv(veiculo.getAnoCrlv());
        veiculoResponseDTO.setCertificadoAfericao(veiculo.getCertificadoAfericao());
        veiculoResponseDTO.setObservacao(veiculo.getObservacao());
        veiculoResponseDTO.setDataCriacao(veiculo.getDataCriacao().toString());
        veiculoResponseDTO.setStatus(veiculo.getStatus());

        return veiculoResponseDTO;
    }

    public Veiculo converterVeiculoDTOToVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                                MultipartFile crlv,
                                                MultipartFile comprovanteVistoria,
                                                Integer tipo) throws IOException {
        Veiculo veiculo = new Veiculo();
        if (veiculoRequestDTO.getIdVeiculo() != null && veiculoRequestDTO.getIdVeiculo() != 0){
            veiculo = veiculoRepository.findVeiculoByIdVeiculo(veiculoRequestDTO.getIdVeiculo());
        }

        veiculo.setPermissionario(permissionarioRepository.findPermissionarioByIdPermissionario(veiculoRequestDTO.getIdPermissionario()));
        veiculo.setPontoTaxi(pontosTaxiRepository.findByIdPontoTaxi(veiculoRequestDTO.getIdPontoTaxi()));
        veiculo.setNumeroPermissao(veiculoRequestDTO.getNumeroPermissao());
        veiculo.setPlaca(veiculoRequestDTO.getPlaca().toUpperCase());
        veiculo.setRenavam(veiculoRequestDTO.getRenavam());
        veiculo.setChassi(veiculoRequestDTO.getChassi());
        veiculo.setAnoFabricacao(veiculoRequestDTO.getAnoFabricacao());
        veiculo.setMarca(veiculoRequestDTO.getMarca());
        veiculo.setModelo(veiculoRequestDTO.getModelo());
        veiculo.setAnoModelo(veiculoRequestDTO.getAnoModelo());
        veiculo.setCor(veiculoRequestDTO.getCor());
        veiculo.setCombustivel(veiculoRequestDTO.getCombustivel());
        veiculo.setCapacidade(veiculoRequestDTO.getCapacidade());
        veiculo.setQuilometragem(veiculoRequestDTO.getQuilometragem());
        veiculo.setNumeroTaximetro(veiculoRequestDTO.getNumeroTaximetro());
        veiculo.setAnoRenovacao(veiculoRequestDTO.getAnoRenovacao());

        if(Objects.nonNull(veiculoRequestDTO.getDataVistoria()) && veiculoRequestDTO.getDataVistoria().equals("null")){
            veiculo.setDataVistoria(null);
            veiculoRequestDTO.setDataVistoria(null);
        }
        veiculo.setStatusVistoria(veiculoRequestDTO.getStatusVistoria());

        if(Objects.nonNull(veiculoRequestDTO.getDataVistoria())) {
            String data = veiculoRequestDTO.getDataVistoria();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateVistoria = zonedDateTime.toLocalDate();
                veiculo.setDataVistoria(localDateVistoria);
            }
        }

        if(Objects.nonNull(veiculoRequestDTO.getDataRetorno()) && veiculoRequestDTO.getDataRetorno().equals("null")){
            veiculo.setDataRetorno(null);
            veiculoRequestDTO.setDataRetorno(null);
        }

        if(Objects.nonNull(veiculoRequestDTO.getDataRetorno())) {
            String data = veiculoRequestDTO.getDataRetorno();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateRetorno = zonedDateTime.toLocalDate();
                veiculo.setDataRetorno(localDateRetorno);
            }
        }

        if(Objects.nonNull(crlv))
            veiculo.setCrlv(crlv.getBytes());
        if(Objects.nonNull(comprovanteVistoria))
            veiculo.setComprovanteVistoria(comprovanteVistoria.getBytes());

        veiculo.setSituacaoVeiculo(veiculoRequestDTO.getSituacaoVeiculo());
        veiculo.setTipoVeiculo(veiculoRequestDTO.getTipoVeiculo());

        veiculo.setNumeroCrlv(veiculoRequestDTO.getNumeroCrlv());
        veiculo.setAnoCrlv(veiculoRequestDTO.getAnoCrlv());
        veiculo.setCertificadoAfericao(veiculoRequestDTO.getCertificadoAfericao());
        veiculo.setObservacao(veiculoRequestDTO.getObservacao());

        if(Objects.nonNull(veiculoRequestDTO.getDataCriacao()) && !veiculoRequestDTO.getDataCriacao().isEmpty())
            veiculo.setDataCriacao(LocalDate.parse(veiculoRequestDTO.getDataCriacao()));
        else
            veiculo.setDataCriacao(LocalDate.now());

        veiculo.setStatus("ATIVO");

        return  veiculo;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public String converterNomeCor(String cor){
        switch (cor){
            case "BRANCA":
                return "1";
            case "PRATA":
                return "2";
        }

        return "";
    }

    public byte[] gerarAutorizacaoTrafego(String numeroPermissao) {
        logger.info("Início Gerar Autorização Tráfego Busca dos Dados");
        try{
            Permissao permissao = permissaoRepository.findPermissaoByNumeroPermissao(numeroPermissao);
            if(Objects.isNull(permissao))
                throw new RuntimeException("400");

            Veiculo veiculo = veiculoRepository.findVeiculoByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(veiculo))
                throw new RuntimeException("401");

            PontoTaxi pontoTaxi = pontosTaxiRepository.findByIdPontoTaxi(veiculo.getPontoTaxi().getIdPontoTaxi());
            if(Objects.isNull(pontoTaxi))
                throw new RuntimeException("402");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(permissionario))
                throw new RuntimeException("403");

            byte[] bytes = gerarAutorizacaoTrafegoJasper(permissao, veiculo, pontoTaxi, permissionario);
            return bytes;
        } catch (Exception e){
            logger.error("gerarAutorizacaoTrafego - Permissão: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarAutorizacaoTrafegoJasper(Permissao permissao, Veiculo veiculo, PontoTaxi pontoTaxi, Permissionario permissionario) {
        logger.info("Início Gerar Autorização Tráfego Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/autorizacaoTrafego.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoAutorizacaoTrafego.png" ).getAbsolutePath());
            FileInputStream  rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeAutorizacaoTrafego.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemRodape", rodapeStream);
            parameters.put("numeroAutorizacao", permissao.getAutorizacaoTrafego());
            parameters.put("dataEmissao", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
            parameters.put("categoriaServicoAutorizado", CarregarTipos.carregarCategoriaVeiculo(veiculo.getTipoVeiculo()));
            parameters.put("validadeAutorizacao", "De " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataCriacao()) +
                    " até " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataValidadePermissao()));
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("tipoCombustivel", CarregarTipos.carregarTipoCombustivelVeiculo(veiculo.getCombustivel()));
            parameters.put("cor", veiculo.getCor().equals("1") ? "Branca" : "Prata");
            parameters.put("capacidade", veiculo.getCapacidade());
            parameters.put("pet", pontoTaxi.getDescricaoPonto());
            parameters.put("numeroPermissao", permissao.getNumeroPermissao());
            parameters.put("permissionario", permissionario.getNomePermissionario());
            parameters.put("ultimaVistoria", Objects.nonNull(veiculo.getDataVistoria()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataVistoria()) : "");
            parameters.put("quilometragem", veiculo.getQuilometragem());
            parameters.put("proximaVistoria", Objects.nonNull(veiculo.getDataRetorno()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataRetorno()) : "");
            parameters.put("statusVistoria", CarregarTipos.carregarStatusVistoriaVeiculo(veiculo.getStatusVistoria()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarAutorizacaoTrafegoJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }

    public byte[] gerarLaudoVistoria(String numeroPermissao) {
        logger.info("Início Gerar Laudo Vistoria Busca dos Dados");
        try{
            Permissao permissao = permissaoRepository.findPermissaoByNumeroPermissao(numeroPermissao);
            if(Objects.isNull(permissao))
                throw new RuntimeException("400");

            Veiculo veiculo = veiculoRepository.findVeiculoByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(veiculo))
                throw new RuntimeException("401");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(permissionario))
                throw new RuntimeException("403");

            byte[] bytes = gerarLaudoVistoriaJasper(permissao, veiculo, permissionario);
            return bytes;
        } catch (Exception e){
            logger.error("gerarAutorizacaoTrafego - Permissão: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarLaudoVistoriaJasper(Permissao permissao, Veiculo veiculo, Permissionario permissionario) {
        logger.info("Início Gerar Laudo Vistoria Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/laudoVistoria.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoLaudoVistoria.png" ).getAbsolutePath());
            FileInputStream  itensAvaliacaoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/itensAvaliacaoVeiculo.png" ).getAbsolutePath());
            FileInputStream  rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeLaudoVistoria.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemItensAvaliacaoVeiculo", itensAvaliacaoStream);
            parameters.put("imagemRodape", rodapeStream);
            parameters.put("numeroProcesso", veiculo.getIdVeiculo().toString());
            LocalDate localDate = LocalDate.now();
            parameters.put("diaMesAno", localDate.getDayOfMonth() + " de " + mesDoAno(localDate.getMonthValue())  + " de " + localDate.getYear());
            parameters.put("nomePermissionario", permissionario.getNomePermissionario());
            parameters.put("numeroPermissao", permissao.getNumeroPermissao());
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("tipoCombustivel", CarregarTipos.carregarTipoCombustivelVeiculo(veiculo.getCombustivel()));
            parameters.put("cor", veiculo.getCor().equals("1") ? "Branca" : "Prata");
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("capacidade", veiculo.getCapacidade());
            parameters.put("quilometragem", veiculo.getQuilometragem());
            parameters.put("statusVistoria", CarregarTipos.carregarStatusVistoriaVeiculo(veiculo.getStatusVistoria()));
            parameters.put("proximaVistoria", Objects.nonNull(veiculo.getDataRetorno()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataRetorno()) : "");
            parameters.put("observacoes", "OBSERVAÇÕES:\n"+veiculo.getObservacao());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarLaudoVistoriaJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }

    public String mesDoAno(Integer mes){
        switch (mes){
            case 1: return "Janeiro";
            case 2: return "Fevereiro";
            case 3: return "Março";
            case 4: return "Abril";
            case 5: return "Maio";
            case 6: return "Junho";
            case 7: return "Julho";
            case 8: return "Agosto";
            case 9: return "Setembro";
            case 10: return "Outubro";
            case 11: return "Novembro";
            case 12: return "Dezembro";
            default: return "";
        }
    }

}
