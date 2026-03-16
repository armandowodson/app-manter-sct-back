package com.projeto.produto.service.impl;

import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.*;
import com.projeto.produto.repository.*;
import com.projeto.produto.utils.CarregarTipos;
import com.projeto.produto.utils.ImprimirAnexosServiceImpl;
import net.sf.jasperreports.engine.*;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ImprimirAnexosServiceImpl imprimirAnexosService;

    private static final Logger logger = LogManager.getLogger(VeiculoServiceImpl.class);

    @Transactional
    public VeiculoResponseDTO inserirVeiculo(VeiculoRequestDTO veiculoRequestDTO, MultipartFile crlv) {
        if(Objects.isNull(veiculoRequestDTO.getUsuario()) || veiculoRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Veiculo veiculo = new Veiculo();
        try{
            veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, null,1);
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
            veiculo = veiculoRepository.save(veiculo);

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

    public Page<VeiculoResponseDTO> listarTodosVeiculosFiltros(String placa, String renavam, String cilindrada,
                                                               String anoFabricacao, PageRequest pageRequest) {
        List<Veiculo> listaVeiculos = veiculoRepository.listarTodosVeiculosFiltros(
                placa, renavam, cilindrada, anoFabricacao, pageRequest
        );

        Integer countRegistros = veiculoRepository.listarTodosVeiculosFiltros(
                placa, renavam, cilindrada, anoFabricacao, null
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
                veiculoList.get(0).setCor("AMARELA");
                break;
            case "2":
                veiculoList.get(0).setCor("LARANJA");
                break;
            case "3":
                veiculoList.get(0).setCor("BRANCA");
                break;
            case "4":
                veiculoList.get(0).setCor("PRETA");
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
        veiculoResponseDTO.setCilindrada(veiculo.getCilindrada());
        veiculoResponseDTO.setCrlv(veiculo.getCrlv());
        veiculoResponseDTO.setNumeroTaximetro(veiculo.getNumeroTaximetro());
        veiculoResponseDTO.setAnoRenovacao(veiculo.getAnoRenovacao());
        if(Objects.nonNull(veiculo.getDataVistoria()) && !veiculo.getDataVistoria().equals(""))
            veiculoResponseDTO.setDataVistoria(veiculo.getDataVistoria().plusDays(1).toString());
        if(Objects.nonNull(veiculo.getDataRetorno()) && !veiculo.getDataRetorno().equals(""))
            veiculoResponseDTO.setDataRetorno(veiculo.getDataRetorno().plusDays(1).toString());
        veiculoResponseDTO.setStatusVistoria(veiculo.getStatusVistoria());
        veiculoResponseDTO.setTipoVistoria(veiculo.getTipoVistoria());
        veiculoResponseDTO.setRessalvas(veiculo.getRessalvas());
        veiculoResponseDTO.setMatriculaVistoriador(veiculo.getMatriculaVistoriador());
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
        veiculo.setPlaca(veiculoRequestDTO.getPlaca().toUpperCase());
        veiculo.setRenavam(veiculoRequestDTO.getRenavam());
        veiculo.setChassi(veiculoRequestDTO.getChassi());
        veiculo.setAnoFabricacao(veiculoRequestDTO.getAnoFabricacao());
        veiculo.setMarca(veiculoRequestDTO.getMarca());
        veiculo.setModelo(veiculoRequestDTO.getModelo());
        veiculo.setAnoModelo(veiculoRequestDTO.getAnoModelo());
        veiculo.setCor(veiculoRequestDTO.getCor());
        veiculo.setCombustivel(veiculoRequestDTO.getCombustivel());

        if(Objects.nonNull(veiculoRequestDTO.getCapacidade()) && veiculoRequestDTO.getCapacidade().equals("null"))
            veiculoRequestDTO.setCapacidade(null);
        veiculo.setCapacidade(veiculoRequestDTO.getCapacidade());

        if(Objects.nonNull(veiculoRequestDTO.getQuilometragem()) && veiculoRequestDTO.getQuilometragem().equals("null"))
            veiculoRequestDTO.setQuilometragem(null);
        veiculo.setQuilometragem(veiculoRequestDTO.getQuilometragem());

        veiculo.setCilindrada(veiculoRequestDTO.getCilindrada());

        if(Objects.nonNull(veiculoRequestDTO.getNumeroTaximetro()) && veiculoRequestDTO.getNumeroTaximetro().equals("null"))
            veiculoRequestDTO.setNumeroTaximetro(null);
        veiculo.setNumeroTaximetro(veiculoRequestDTO.getNumeroTaximetro());

        if(Objects.nonNull(veiculoRequestDTO.getAnoRenovacao()) && veiculoRequestDTO.getAnoRenovacao().equals("null")){
            veiculoRequestDTO.setAnoRenovacao(null);
        }
        veiculo.setAnoRenovacao(veiculoRequestDTO.getAnoRenovacao());

        if(Objects.nonNull(veiculoRequestDTO.getDataVistoria()) && veiculoRequestDTO.getDataVistoria().equals("null")){
            veiculoRequestDTO.setDataVistoria(null);
        }
        if(Objects.nonNull(veiculoRequestDTO.getDataVistoria()) && !veiculoRequestDTO.getDataVistoria().isEmpty())
            veiculo.setDataVistoria(LocalDate.parse(veiculoRequestDTO.getDataVistoria()));

        if(Objects.nonNull(veiculoRequestDTO.getStatusVistoria()) && veiculoRequestDTO.getStatusVistoria().equals("null")){
            veiculoRequestDTO.setStatusVistoria(null);
        }
        veiculo.setStatusVistoria(veiculoRequestDTO.getStatusVistoria());

        if(Objects.nonNull(veiculoRequestDTO.getTipoVistoria()) && veiculoRequestDTO.getTipoVistoria().equals("null")){
            veiculoRequestDTO.setTipoVistoria(null);
        }
        veiculo.setTipoVistoria(veiculoRequestDTO.getTipoVistoria());

        if(Objects.nonNull(veiculoRequestDTO.getRessalvas()) && veiculoRequestDTO.getRessalvas().equals("null")){
            veiculoRequestDTO.setRessalvas(null);
        }
        veiculo.setRessalvas(veiculoRequestDTO.getRessalvas());

        if(Objects.nonNull(veiculoRequestDTO.getMatriculaVistoriador()) && veiculoRequestDTO.getMatriculaVistoriador().equals("null")){
            veiculoRequestDTO.setMatriculaVistoriador(null);
        }
        veiculo.setMatriculaVistoriador(veiculoRequestDTO.getMatriculaVistoriador());

        if(Objects.nonNull(veiculoRequestDTO.getDataRetorno()) && veiculoRequestDTO.getDataRetorno().equals("null")){
            veiculoRequestDTO.setDataRetorno(null);
        }
        if(Objects.nonNull(veiculoRequestDTO.getDataRetorno()) && !veiculoRequestDTO.getDataRetorno().isEmpty()) {
            veiculo.setDataRetorno(LocalDate.parse(veiculoRequestDTO.getDataRetorno()));
        }

        if(Objects.nonNull(crlv))
            veiculo.setCrlv(crlv.getBytes());

        veiculo.setSituacaoVeiculo(veiculoRequestDTO.getSituacaoVeiculo());
        veiculo.setTipoVeiculo(veiculoRequestDTO.getTipoVeiculo());
        veiculo.setNumeroCrlv(veiculoRequestDTO.getNumeroCrlv());
        veiculo.setAnoCrlv(veiculoRequestDTO.getAnoCrlv());

        if(Objects.nonNull(veiculoRequestDTO.getCertificadoAfericao()) && veiculoRequestDTO.getCertificadoAfericao().equals("null"))
            veiculoRequestDTO.setCertificadoAfericao(null);
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

    public byte[] gerarCertificadoAnualVistoria(String idVeiculo, String modulo) {
        logger.info("Início Gerar Certificado Anual de Vistoria Busca dos Dados");
        try{
            Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(Long.valueOf(idVeiculo));
            if(Objects.isNull(veiculo))
                throw new RuntimeException("400");

            if((Objects.isNull(veiculo.getDataVistoria()) || veiculo.getDataVistoria().equals("")) ||
                    Objects.isNull(veiculo.getStatusVistoria()) || veiculo.getStatusVistoria().equals(""))
                throw new RuntimeException("401");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(veiculo.getPermissionario().getIdPermissionario());
            if(Objects.isNull(permissionario))
                throw new RuntimeException("402");

            byte[] bytes = gerarCertificadoAnualVistoriaJasper(veiculo, permissionario, modulo);
            return bytes;
        } catch (Exception e){
            logger.error("gerarCertificadoAnualVistoria - Autorizatário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarCertificadoAnualVistoriaJasper(Veiculo veiculo, Permissionario permissionario, String modulo) {
        logger.info("Início Gerar Certificado Anual Vistoria Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/certificadoAnualVistoriaMoto.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoCertificadoAnualVistoriaMoto.png" ).getAbsolutePath());
            FileInputStream rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeCertificadoAnualVistoriaMoto.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemRodape", rodapeStream);

            //CERTIFICADO
            parameters.put("numeroCavEmitido", Objects.nonNull(veiculo.getNumeroCavEmitido()) ? veiculo.getNumeroCavEmitido() : StringUtils.leftPad(veiculo.getIdVeiculo().toString(), 5, "0") + "/" + LocalDate.now().getYear());
            LocalDate localDate = LocalDate.now();
            parameters.put("dataEmissao", localDate.getDayOfMonth() + "/" + mesDoAno(localDate.getMonthValue())  + "/" + localDate.getYear());
            parameters.put("numeroTas", StringUtils.leftPad(permissionario.getIdPermissionario().toString() + veiculo.getIdVeiculo().toString(), 8, "0") + "/" + permissionario.getDataCriacao().getYear());
            parameters.put("dataValidadeCav", "De " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataVistoria()) + " até " +
                    DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataVistoria().plusYears(1L)));

            //VEÍCULO
            parameters.put("numeroCcmt", String.valueOf(permissionario.getIdPermissionario()));
            parameters.put("numeroCvmt", String.valueOf(veiculo.getIdVeiculo()));
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("cilindrada", Objects.nonNull(veiculo.getCilindrada()) ? veiculo.getCilindrada() : "");
            parameters.put("cor", obterCor(veiculo.getCor()));
            parameters.put("chassi", veiculo.getChassi());
            parameters.put("nomePermissionario", permissionario.getNomePermissionario());

            //VISTORIA
            parameters.put("dataVistoria", Objects.nonNull(veiculo.getDataVistoria()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataVistoria()) : "");
            parameters.put("quilometragem", Objects.nonNull(veiculo.getQuilometragem()) ? veiculo.getQuilometragem() : "");
            parameters.put("numeroLaudoVistoria", veiculo.getIdVeiculo().toString());
            parameters.put("proximaVistoria", Objects.nonNull(veiculo.getDataRetorno()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataRetorno()) : "");
            parameters.put("matriculaVistoriador", Objects.nonNull(veiculo.getMatriculaVistoriador()) ? veiculo.getMatriculaVistoriador() : "");
            parameters.put("tipoVistoria", "[x] " + CarregarTipos.carregarTipoVistoriaVeiculo(veiculo.getTipoVistoria()));
            parameters.put("statusVistoria", "[x] " + CarregarTipos.carregarStatusVistoriaVeiculo(veiculo.getStatusVistoria()));
            parameters.put("ressalvas", Objects.nonNull(veiculo.getRessalvas()) ? veiculo.getRessalvas() : "");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            if(Objects.isNull(veiculo.getNumeroCavEmitido()) || veiculo.getNumeroCavEmitido().equals("")){
                veiculo.setNumeroCavEmitido(StringUtils.leftPad(veiculo.getIdVeiculo().toString(), 5, "0") + "/" + LocalDate.now().getYear());
                veiculoRepository.save(veiculo);
            }

            return bytes;
        } catch (Exception e){
            logger.error("gerarCertificadoAnualVistoriaJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }

    public byte[] gerarLaudoVistoria(String idVeiculo, String modulo) {
        logger.info("Início Gerar Laudo Vistoria Busca dos Dados");
        try{
            Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(Long.valueOf(idVeiculo));
            if(Objects.isNull(veiculo))
                throw new RuntimeException("400");

            if((Objects.isNull(veiculo.getDataVistoria()) || veiculo.getDataVistoria().equals("")) ||
                    Objects.isNull(veiculo.getStatusVistoria()) || veiculo.getStatusVistoria().equals(""))
                throw new RuntimeException("401");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(veiculo.getPermissionario().getIdPermissionario());
            if(Objects.isNull(permissionario))
                throw new RuntimeException("402");

            byte[] bytes = gerarLaudoVistoriaJasper(veiculo, permissionario, modulo);
            return bytes;
        } catch (Exception e){
            logger.error("gerarLaudoVistoria - Permissão: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarLaudoVistoriaJasper(Veiculo veiculo, Permissionario permissionario, String modulo) {
        logger.info("Início Gerar Laudo Vistoria Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/laudoVistoriaMoto.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoLaudoVistoriaMoto.png" ).getAbsolutePath());
            FileInputStream itensAvaliacaoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/itensAvaliacaoVeiculoMoto.png" ).getAbsolutePath());
            FileInputStream rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeLaudoVistoriaMoto.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemItensAvaliacaoVeiculo", itensAvaliacaoStream);
            parameters.put("imagemRodape", rodapeStream);
            parameters.put("numeroProcesso", veiculo.getIdVeiculo().toString());
            LocalDate localDate = LocalDate.now();
            parameters.put("diaMesAno", localDate.getDayOfMonth() + " de " + mesDoAno(localDate.getMonthValue())  + " de " + localDate.getYear());
            parameters.put("nomePermissionario", permissionario.getNomePermissionario());
            parameters.put("numeroTas", StringUtils.leftPad(permissionario.getIdPermissionario().toString() + veiculo.getIdVeiculo().toString(), 8, "0") + "/" + permissionario.getDataCriacao().getYear());
            parameters.put("numeroCcmt", String.valueOf(permissionario.getIdPermissionario()));
            parameters.put("numeroCvmt", String.valueOf(veiculo.getIdVeiculo()));
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("tipoCombustivel", CarregarTipos.carregarTipoCombustivelVeiculo(veiculo.getCombustivel()));
            parameters.put("cor", obterCor(veiculo.getCor()));
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("capacidade", veiculo.getCapacidade());
            parameters.put("quilometragem", veiculo.getQuilometragem());
            parameters.put("ressalvas", Objects.nonNull(veiculo.getRessalvas()) ? veiculo.getRessalvas() : "");
            parameters.put("tipoVistoria", "[x] " + CarregarTipos.carregarTipoVistoriaVeiculo(veiculo.getTipoVistoria()));
            parameters.put("statusVistoria", "[x] " + CarregarTipos.carregarStatusVistoriaVeiculo(veiculo.getStatusVistoria()));
            parameters.put("numeroCavEmitido", Objects.nonNull(veiculo.getNumeroCavEmitido()) ? veiculo.getNumeroCavEmitido() : "");
            parameters.put("proximaVistoria", Objects.nonNull(veiculo.getDataRetorno()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataRetorno()) : "");
            parameters.put("observacoes", veiculo.getObservacao());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarLaudoVistoriaJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }

    public byte[] imprimirAnexo(String idAplicacao, String aplicacao, String anexo, String modulo) {
        logger.info("Início Impressão do Anexo do Veículo Busca dos Dados");
        try{
            byte[] bytes = imprimirAnexosService.imprimirAnexo(idAplicacao, aplicacao, anexo, modulo);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirAnexo - Veículo: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
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

    public String obterCor(String idCor){
        switch (idCor){
            case "1":
                return "Amarela";
            case "2":
                return "Laranja";
            case "3":
                return "Branca";
            case "4":
                return "Preta";
            default:
                return "";
        }
    }

}
