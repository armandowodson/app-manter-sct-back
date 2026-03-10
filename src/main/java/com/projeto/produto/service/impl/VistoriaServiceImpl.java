package com.projeto.produto.service.impl;

import com.projeto.produto.dto.VistoriaRequestDTO;
import com.projeto.produto.dto.VistoriaResponseDTO;
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
public class VistoriaServiceImpl {
    @Autowired
    private VistoriaRepository vistoriaRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PermissionarioRepository permissionarioRepository;

    private static final Logger logger = LogManager.getLogger(VistoriaServiceImpl.class);

    @Transactional
    public VistoriaResponseDTO inserirVistoria(VistoriaRequestDTO vistoriaRequestDTO,
                                               MultipartFile comprovanteVistoria) {
        if(Objects.isNull(vistoriaRequestDTO.getUsuario()) || vistoriaRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Vistoria vistoria = new Vistoria();
        try{
            vistoria = converterVistoriaDTOToVistoria(vistoriaRequestDTO, comprovanteVistoria, 1);
            vistoria.setDataCriacao(LocalDate.now());
            vistoria = vistoriaRepository.save(vistoria);

            //Auditoria
            salvarAuditoria("VISTORIA MOTO TÁXI", "INCLUSÃO", vistoriaRequestDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados da Vistoria!");
        }

        return converterVistoriaToVistoriaDTO(vistoria);
    }

    @Transactional
    public VistoriaResponseDTO atualizarVistoria(VistoriaRequestDTO vistoriaRequestDTO,
                                               MultipartFile comprovanteVistoria) {
        if(Objects.isNull(vistoriaRequestDTO.getUsuario()) || vistoriaRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Vistoria vistoria = new Vistoria();
        try{
            vistoria = converterVistoriaDTOToVistoria(vistoriaRequestDTO, comprovanteVistoria, 2);

            //Auditoria
            salvarAuditoria("VISTORIA MOTO TÁXI", "ALTERAÇÃO", vistoriaRequestDTO.getUsuario());
        }catch (Exception e){
            throw new RuntimeException("Não foi possível atualizar os dados da Vistoria!");
        }

        return converterVistoriaToVistoriaDTO(vistoriaRepository.save(vistoria));
    }

    public Page<VistoriaResponseDTO> listarTodasVistorias(PageRequest pageRequest) {
        List<Vistoria> vistoriaList = vistoriaRepository.buscarTodos(pageRequest);
        Integer countLista = vistoriaRepository.buscarTodos(null).size();
        List<VistoriaResponseDTO> vistoriaResponseDTOList = converterEntityToDTO(vistoriaList);
        return new PageImpl<>(vistoriaResponseDTOList, pageRequest, countLista);
    }

    public VistoriaResponseDTO buscarVistoriaId(Long idVistoria) {
        Vistoria vistoria = vistoriaRepository.findVistoriaByIdVistoria(idVistoria);
        VistoriaResponseDTO vistoriaResponseDTO = new VistoriaResponseDTO();
        if (vistoria != null){
            vistoriaResponseDTO = converterVistoriaToVistoriaDTO(vistoria);
        }
        return vistoriaResponseDTO;
    }

    public Page<VistoriaResponseDTO> listarTodasVistoriasFiltros(String numeroPermissao, String placa,
                                                               String statusVistoria, PageRequest pageRequest) {
        List<Vistoria> listaVistorias = vistoriaRepository.listarTodasVistoriasFiltros(
                numeroPermissao,  placa, statusVistoria, pageRequest
        );

        Integer countRegistros = vistoriaRepository.listarTodasVistoriasFiltros(
                numeroPermissao,  placa, statusVistoria, null
        ).size();

        List<VistoriaResponseDTO> listaVistoriaResponseDTO = new ArrayList<>();
        if (!listaVistorias.isEmpty()){
            for (Vistoria vistoria : listaVistorias) {
                VistoriaResponseDTO vistoriaResponseDTORetornado = converterVistoriaToVistoriaDTO(vistoria);
                listaVistoriaResponseDTO.add(vistoriaResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaVistoriaResponseDTO, pageRequest, countRegistros);
    }

    @Transactional
    public ResponseEntity<Void> excluirVistoria(Long idVistoria, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Vistoria vistoria = vistoriaRepository.findVistoriaByIdVistoria(idVistoria);
            vistoria.setStatus("INATIVO");
            vistoriaRepository.save(vistoria);

            //Auditoria
            salvarAuditoria("VISTORIA MOTO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Não foi possível excluir o Veículo!!!");
        }
    }

    public List<VistoriaResponseDTO> converterEntityToDTO(List<Vistoria> listaVistorias){
        List<VistoriaResponseDTO> listaVistoriasDTO = new ArrayList<>();
        for(Vistoria vistoria : listaVistorias){
            VistoriaResponseDTO vistoriaResponseDTO = converterVistoriaToVistoriaDTO(vistoria);
            listaVistoriasDTO.add(vistoriaResponseDTO);
        }

        return  listaVistoriasDTO;
    }

    public VistoriaResponseDTO converterVistoriaToVistoriaDTO(Vistoria vistoria){
        VistoriaResponseDTO vistoriaResponseDTO = new VistoriaResponseDTO();
        if (vistoria.getIdVistoria() != null){
            vistoriaResponseDTO.setIdVistoria(vistoria.getIdVistoria());
        }

        vistoriaResponseDTO.setIdVeiculo(vistoria.getVeiculo().getIdVeiculo());
        vistoriaResponseDTO.setNumeroPermissao(vistoria.getVeiculo().getNumeroPermissao());
        vistoriaResponseDTO.setPlaca(vistoria.getVeiculo().getPlaca());
        vistoriaResponseDTO.setChassiFunilariaPintura(vistoria.getChassiFunilariaPintura());
        vistoriaResponseDTO.setInstalacaoEletrica(vistoria.getInstalacaoEletrica());
        vistoriaResponseDTO.setFarolAtlaBaixa(vistoria.getFarolAtlaBaixa());
        vistoriaResponseDTO.setBuzina(vistoria.getBuzina());
        vistoriaResponseDTO.setLanternaTraseira(vistoria.getLanternaTraseira());
        vistoriaResponseDTO.setFreioDianteiro(vistoria.getFreioDianteiro());
        vistoriaResponseDTO.setLuzPlaca(vistoria.getLuzPlaca());
        vistoriaResponseDTO.setFreioTraseiro(vistoria.getFreioTraseiro());
        vistoriaResponseDTO.setLuzesDirecao(vistoria.getLuzesDirecao());
        vistoriaResponseDTO.setPneusDesgateCalibragem(vistoria.getPneusDesgateCalibragem());
        vistoriaResponseDTO.setLuzFreio(vistoria.getLuzFreio());
        vistoriaResponseDTO.setCorrenteCorreia(vistoria.getCorrenteCorreia());
        vistoriaResponseDTO.setPlacasDianteiraTraseira(vistoria.getPlacasDianteiraTraseira());
        vistoriaResponseDTO.setVazamentoOleoCombustivel(vistoria.getVazamentoOleoCombustivel());
        vistoriaResponseDTO.setLimpezaGeralInterna(vistoria.getLimpezaGeralInterna());
        vistoriaResponseDTO.setEscapamento(vistoria.getEscapamento());
        vistoriaResponseDTO.setAssentoFixacao(vistoria.getAssentoFixacao());
        vistoriaResponseDTO.setEquipamentosObrigatorios(vistoria.getEquipamentosObrigatorios());
        vistoriaResponseDTO.setEspelhosRetrovisores(vistoria.getEspelhosRetrovisores());
        vistoriaResponseDTO.setSelosVistoria(vistoria.getSelosVistoria());
        vistoriaResponseDTO.setGuidaoManoplas(vistoria.getGuidaoManoplas());
        vistoriaResponseDTO.setOutros(vistoria.getOutros());
        vistoriaResponseDTO.setDataVistoria(vistoria.getDataVistoria().plusDays(1).toString());
        vistoriaResponseDTO.setDataRetorno(vistoria.getDataRetorno().plusDays(1).toString());
        vistoriaResponseDTO.setStatusVistoria(vistoria.getStatusVistoria());
        vistoriaResponseDTO.setRessalvas(vistoria.getRessalvas());
        vistoriaResponseDTO.setComprovanteVistoria(vistoria.getComprovanteVistoria());
        vistoriaResponseDTO.setObservacao(vistoria.getObservacao());
        vistoriaResponseDTO.setDataCriacao(vistoria.getDataCriacao().toString());
        vistoriaResponseDTO.setStatus(vistoria.getStatus());

        return vistoriaResponseDTO;
    }

    public Vistoria converterVistoriaDTOToVistoria(VistoriaRequestDTO vistoriaRequestDTO,
                                                MultipartFile comprovanteVistoria,
                                                Integer tipo) throws IOException {
        Vistoria vistoria = new Vistoria();
        if (vistoriaRequestDTO.getIdVistoria() != null && vistoriaRequestDTO.getIdVistoria() != 0){
            vistoria = vistoriaRepository.findVistoriaByIdVistoria(vistoriaRequestDTO.getIdVistoria());
        }
        vistoria.setVeiculo(veiculoRepository.findVeiculoByIdVeiculo(vistoriaRequestDTO.getIdVeiculo()));

        vistoria.setChassiFunilariaPintura(vistoriaRequestDTO.getChassiFunilariaPintura());
        vistoria.setInstalacaoEletrica(vistoriaRequestDTO.getInstalacaoEletrica());
        vistoria.setFarolAtlaBaixa(vistoriaRequestDTO.getFarolAtlaBaixa());
        vistoria.setBuzina(vistoriaRequestDTO.getBuzina());
        vistoria.setLanternaTraseira(vistoriaRequestDTO.getLanternaTraseira());
        vistoria.setFreioDianteiro(vistoriaRequestDTO.getFreioDianteiro());
        vistoria.setLuzPlaca(vistoriaRequestDTO.getLuzPlaca());
        vistoria.setFreioTraseiro(vistoriaRequestDTO.getFreioTraseiro());
        vistoria.setLuzesDirecao(vistoriaRequestDTO.getLuzesDirecao());
        vistoria.setPneusDesgateCalibragem(vistoriaRequestDTO.getPneusDesgateCalibragem());
        vistoria.setLuzFreio(vistoriaRequestDTO.getLuzFreio());
        vistoria.setCorrenteCorreia(vistoriaRequestDTO.getCorrenteCorreia());
        vistoria.setPlacasDianteiraTraseira(vistoriaRequestDTO.getPlacasDianteiraTraseira());
        vistoria.setVazamentoOleoCombustivel(vistoriaRequestDTO.getVazamentoOleoCombustivel());
        vistoria.setLimpezaGeralInterna(vistoriaRequestDTO.getLimpezaGeralInterna());
        vistoria.setEscapamento(vistoriaRequestDTO.getEscapamento());
        vistoria.setAssentoFixacao(vistoriaRequestDTO.getAssentoFixacao());
        vistoria.setEquipamentosObrigatorios(vistoriaRequestDTO.getEquipamentosObrigatorios());
        vistoria.setEspelhosRetrovisores(vistoriaRequestDTO.getEspelhosRetrovisores());
        vistoria.setSelosVistoria(vistoriaRequestDTO.getSelosVistoria());
        vistoria.setGuidaoManoplas(vistoriaRequestDTO.getGuidaoManoplas());
        vistoria.setOutros(vistoriaRequestDTO.getOutros());

        if(Objects.nonNull(vistoriaRequestDTO.getDataVistoria()) && vistoriaRequestDTO.getDataVistoria().equals("null")){
            vistoria.setDataVistoria(null);
            vistoriaRequestDTO.setDataVistoria(null);
        }else{
            String data = vistoriaRequestDTO.getDataVistoria();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateVistoria = zonedDateTime.toLocalDate();
                vistoria.setDataVistoria(localDateVistoria);
            }
        }
        vistoria.setStatusVistoria(vistoriaRequestDTO.getStatusVistoria());
        vistoria.setRessalvas(vistoriaRequestDTO.getRessalvas());

        if(Objects.nonNull(vistoriaRequestDTO.getDataRetorno()) && vistoriaRequestDTO.getDataRetorno().equals("null")){
            vistoria.setDataRetorno(null);
            vistoriaRequestDTO.setDataRetorno(null);
        }else{
            String data = vistoriaRequestDTO.getDataRetorno();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateRetorno = zonedDateTime.toLocalDate();
                vistoria.setDataRetorno(localDateRetorno);
            }
        }

        if(Objects.nonNull(comprovanteVistoria))
            vistoria.setComprovanteVistoria(comprovanteVistoria.getBytes());

        vistoria.setObservacao(vistoriaRequestDTO.getObservacao());

        if(Objects.nonNull(vistoriaRequestDTO.getDataCriacao()) && !vistoriaRequestDTO.getDataCriacao().isEmpty())
            vistoria.setDataCriacao(LocalDate.parse(vistoriaRequestDTO.getDataCriacao()));
        else
            vistoria.setDataCriacao(LocalDate.now());

        vistoria.setStatus("ATIVO");

        return  vistoria;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public byte[] gerarLaudoVistoria(String idVistoria, String idVeiculo, String modulo) {
        logger.info("Início Gerar Laudo Vistoria Busca dos Dados");
        try{
            Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(Long.parseLong(idVeiculo));
            if(Objects.isNull(veiculo))
                throw new RuntimeException("400");

            Permissao permissao = permissaoRepository.findPermissaoByNumeroPermissao(veiculo.getNumeroPermissao());
            if(Objects.isNull(permissao))
                throw new RuntimeException("401");

            Vistoria vistoria = vistoriaRepository.findVistoriaByIdVistoria(Long.valueOf(idVistoria));

            Permissionario permissionario = permissionarioRepository.findPermissionarioByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(permissionario))
                throw new RuntimeException("402");

            byte[] bytes = gerarLaudoVistoriaJasper(permissao, vistoria, permissionario, modulo);
            return bytes;
        } catch (Exception e){
            logger.error("gerarLaudoVistoria: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarLaudoVistoriaJasper(Permissao permissao, Vistoria vistoria, Permissionario permissionario, String modulo) {
        logger.info("Início Gerar Laudo Vistoria Jasper");
        try{
            ClassPathResource resource;
            if(modulo.equals("1"))
                resource = new ClassPathResource("reports/laudoVistoria.jrxml");
            else
                resource = new ClassPathResource("reports/laudoVistoriaMoto.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            FileInputStream cabecalhoStream;
            FileInputStream  itensAvaliacaoStream;
            FileInputStream  rodapeStream;

            if(modulo.equals("1")){
                cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoLaudoVistoria.png" ).getAbsolutePath());
                itensAvaliacaoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/itensAvaliacaoVistoria.png" ).getAbsolutePath());
                rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeLaudoVistoria.png" ).getAbsolutePath());
            }else{
                cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoLaudoVistoriaMoto.png" ).getAbsolutePath());
                itensAvaliacaoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/itensAvaliacaoVistoriaMoto.png" ).getAbsolutePath());
                rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeLaudoVistoriaMoto.png" ).getAbsolutePath());
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemItensAvaliacaoVistoria", itensAvaliacaoStream);
            parameters.put("imagemRodape", rodapeStream);
            parameters.put("numeroProcesso", vistoria.getIdVistoria().toString());
            LocalDate localDate = LocalDate.now();
            parameters.put("diaMesAno", localDate.getDayOfMonth() + " de " + mesDoAno(localDate.getMonthValue())  + " de " + localDate.getYear());
            parameters.put("nomePermissionario", permissionario.getNomePermissionario());
            parameters.put("numeroPermissao", permissao.getNumeroPermissao());
            parameters.put("statusVistoria", CarregarTipos.carregarStatusVistoriaVeiculo(vistoria.getStatusVistoria()));
            parameters.put("proximaVistoria", Objects.nonNull(vistoria.getDataRetorno()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(vistoria.getDataRetorno()) : "");
            parameters.put("observacoes", "OBSERVAÇÕES:\n"+vistoria.getObservacao());

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
