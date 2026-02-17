package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.entity.*;
import com.projeto.produto.repository.*;
import com.projeto.produto.utils.CarregarTipos;
import com.projeto.produto.utils.ValidaCPF;
import com.projeto.produto.utils.ValidaEmail;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PermissionarioServiceImpl {
    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PontosTaxiRepository pontosTaxiRepository;

    private static final Logger logger = LogManager.getLogger(Permissionario.class);

    @Transactional
    public PermissionarioResponseDTO inserirPermissionario(    PermissionarioRequestDTO permissionarioRequestDTO,
                                                               MultipartFile certificadoCondutor,
                                                               MultipartFile certidaoNegativaCriminal,
                                                               MultipartFile certidaoNegativaMunicipal,
                                                               MultipartFile foto) {

        logger.info("Início Inserir Permissionário");
        if(Objects.nonNull(permissionarioRequestDTO.getCpfPermissionario()) && !permissionarioRequestDTO.getCpfPermissionario().isEmpty() &&
                permissionarioRequestDTO.getCpfPermissionario().length() < 11){
            permissionarioRequestDTO.setCpfPermissionario(StringUtils.leftPad(permissionarioRequestDTO.getCpfPermissionario(), 11, "0"));
        }

        if(Objects.nonNull(permissionarioRequestDTO.getCpfPermissionario()) && !permissionarioRequestDTO.getCpfPermissionario().isEmpty() &&
                !ValidaCPF.isCPF(permissionarioRequestDTO.getCpfPermissionario()))
            throw new RuntimeException("O CPF " + permissionarioRequestDTO.getCpfPermissionario() + " é inválido!");

        if(Objects.nonNull(permissionarioRequestDTO.getEmailPermissionario()) && !permissionarioRequestDTO.getEmailPermissionario().isEmpty() &&
                !ValidaEmail.isEmail(permissionarioRequestDTO.getEmailPermissionario()))
            throw new RuntimeException("O E-mail " + permissionarioRequestDTO.getEmailPermissionario() + " é inválido!");

        if(Objects.isNull(permissionarioRequestDTO.getUsuario()) || permissionarioRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Permissionario permissionario = new Permissionario();
        try{
            permissionario = converterPermissionarioDTOToPermissionario(
                    permissionarioRequestDTO, certificadoCondutor, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 1
            );
            permissionario = permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("PERMISSIONÁRIO TÁXI", "INCLUSÃO", permissionarioRequestDTO.getUsuario());
        } catch (Exception e){
            logger.error("inserirPermissionario - " + e.getMessage());
            throw new RuntimeException("Não foi possível inserir os dados do Permissionário!");
        }

        return converterPermissionarioToPermissionarioDTO(permissionario);
    }

    @Transactional
    public PermissionarioResponseDTO atualizarPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                             MultipartFile certificadoCondutor,
                                                             MultipartFile certidaoNegativaCriminal,
                                                             MultipartFile certidaoNegativaMunicipal,
                                                             MultipartFile foto) {
        logger.info("Início Atualização Permissionário");
        if(Objects.nonNull(permissionarioRequestDTO.getCpfPermissionario()) && !permissionarioRequestDTO.getCpfPermissionario().isEmpty() &&
                permissionarioRequestDTO.getCpfPermissionario().length() < 11){
            permissionarioRequestDTO.setCpfPermissionario(StringUtils.leftPad(permissionarioRequestDTO.getCpfPermissionario(), 11, "0"));
        }

        if(Objects.nonNull(permissionarioRequestDTO.getCpfPermissionario()) && !permissionarioRequestDTO.getCpfPermissionario().isEmpty() &&
                !ValidaCPF.isCPF(permissionarioRequestDTO.getCpfPermissionario()))
            throw new RuntimeException("O CPF " + permissionarioRequestDTO.getCpfPermissionario() + " é inválido!");

        if(Objects.nonNull(permissionarioRequestDTO.getEmailPermissionario()) && !permissionarioRequestDTO.getEmailPermissionario().isEmpty() &&
                !ValidaEmail.isEmail(permissionarioRequestDTO.getEmailPermissionario()))
            throw new RuntimeException("O E-mail " + permissionarioRequestDTO.getEmailPermissionario() + " é inválido!");

        if(Objects.isNull(permissionarioRequestDTO.getUsuario()) || permissionarioRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário não logado ou não identificado!");

        Permissionario permissionario = new Permissionario();
        try{
            permissionario = converterPermissionarioDTOToPermissionario(
                    permissionarioRequestDTO, certificadoCondutor, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 2
            );

            permissionario = permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("PERMISSIONÁRIO TÁXI", "ALTERAÇÃO", permissionarioRequestDTO.getUsuario());
        } catch (Exception e){
            logger.error("atualizarPermissionario - " + e.getMessage());
            throw new RuntimeException("Não foi possível alterar os dados do Permissionário!");
        }

        return converterPermissionarioToPermissionarioDTO(permissionarioRepository.save(permissionario));
    }

    public Page<PermissionarioResponseDTO> listarTodosPermissionarios(PageRequest pageRequest) {
        List<Permissionario> permissionarioList = permissionarioRepository.buscarTodos(pageRequest);
        Integer countLista = permissionarioRepository.buscarTodos(null).size();
        List<PermissionarioResponseDTO> permissionarioResponseDTOList = converterEntityToDTO(permissionarioList);
        return new PageImpl<>(permissionarioResponseDTOList, pageRequest, countLista);
    }

    public PermissionarioResponseDTO buscarPermissionarioId(Long idPermissionario) {
        Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
        PermissionarioResponseDTO permissionarioResponseDTO = new PermissionarioResponseDTO();
        if (permissionario != null){
            permissionarioResponseDTO = converterPermissionarioToPermissionarioDTO(permissionario);
        }
        return permissionarioResponseDTO;
    }

    public Page<PermissionarioResponseDTO> listarTodosPermissionarioFiltros(String numeroPermissao, String nomePermissionario,
                                                                           String cpfPermissionario, String cnpjEmpresa,
                                                                           String cnhPermissionario, PageRequest pageRequest) {
        logger.error("Início da Listagem de Todos os Dados do Permissionário");
        try{
            List<Permissionario> listaPermissionario = permissionarioRepository.listarTodosPermissionariosFiltros(
                    numeroPermissao, nomePermissionario != null ? nomePermissionario.toUpperCase() : nomePermissionario,
                    cpfPermissionario, cnpjEmpresa, cnhPermissionario, pageRequest
            );

            Integer countRegistros = permissionarioRepository.listarTodosPermissionariosFiltros(
                    numeroPermissao, nomePermissionario != null ? nomePermissionario.toUpperCase() : nomePermissionario,
                    cpfPermissionario, cnpjEmpresa, cnhPermissionario, null
            ).size();

            List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
            if (!listaPermissionario.isEmpty()){
                for (Permissionario permissionario : listaPermissionario) {
                    PermissionarioResponseDTO permissionarioResponseDTORetornado = converterPermissionarioToPermissionarioDTO(permissionario);
                    listaPermissionarioResponseDTO.add(permissionarioResponseDTORetornado);
                }
            }

            return new PageImpl<>(listaPermissionarioResponseDTO, pageRequest, countRegistros);
        } catch (Exception e){
            logger.error("listarTodosPermissionarioFiltros - " + e.getMessage());
            throw new RuntimeException("Não foi possível listar todos os dados dos Permissionários!");
        }
    }

    public List<PermissionarioResponseDTO> listarPermissionariosDisponiveis(Long idPermissionario) {
        logger.info("Início da Listagem dos Permissionários disponíveis");
        try{
            List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
            List<Permissionario> listaPermissionario = permissionarioRepository.listarPermissionariosDisponiveis();
            if(Objects.nonNull(idPermissionario)){
                Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
                PermissionarioResponseDTO permissionarioResponseDTORetornado = converterPermissionarioToPermissionarioDTO(permissionario);
                listaPermissionarioResponseDTO.add(permissionarioResponseDTORetornado);
            }

            if (!listaPermissionario.isEmpty()){
                for (Permissionario permissionarioDisponivel : listaPermissionario) {
                    PermissionarioResponseDTO permissionarioResponseDTORetornado = converterPermissionarioToPermissionarioDTO(permissionarioDisponivel);
                    listaPermissionarioResponseDTO.add(permissionarioResponseDTORetornado);
                }
            }

            return listaPermissionarioResponseDTO;
        } catch (Exception e){
            logger.error("listarTodosPermissionarioFiltros - " + e.getMessage());
            throw new RuntimeException("Não foi possível listar os Permissionários disponíveis!");
        }
    }

    @Transactional
    public ResponseEntity<Void> excluirPermissionario(Long idPermissionario, String usuario) {
        logger.info("Início da Exclusão do Permissionário");
        String msgErro = "Erro ao Excluir o Permissionário!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
            Veiculo veiculo = veiculoRepository.findVeiculoByPermissionario(permissionario);
            if(Objects.nonNull(veiculo)){
                msgErro = "Existe um Veículo associado a este Permissionário";
                logger.error("excluirPermissionario - " + msgErro);
                throw new Exception();
            }

            permissionario.setStatus("INATIVO");
            permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("PERMISSIONÁRIO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            logger.error("excluirPermissionario - " + e.getMessage());
            throw new RuntimeException(msgErro);
        }
    }

    public List<PermissionarioResponseDTO> converterEntityToDTO(List<Permissionario> listaPermissionario){
        List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
        for(Permissionario permissionario : listaPermissionario){
            PermissionarioResponseDTO permissionarioResponseDTO = converterPermissionarioToPermissionarioDTO(permissionario);
            listaPermissionarioResponseDTO.add(permissionarioResponseDTO);
        }

        return listaPermissionarioResponseDTO;
    }

    public PermissionarioResponseDTO converterPermissionarioToPermissionarioDTO(Permissionario permissionario){
        PermissionarioResponseDTO permissionarioResponseDTO = new PermissionarioResponseDTO();
        if (permissionario.getIdPermissionario() != null){
            permissionarioResponseDTO.setIdPermissionario(permissionario.getIdPermissionario());
        }

        permissionarioResponseDTO.setNumeroPermissao(permissionario.getNumeroPermissao());
        permissionarioResponseDTO.setNomePermissionario(permissionario.getNomePermissionario());
        permissionarioResponseDTO.setCpfPermissionario(permissionario.getCpfPermissionario());
        permissionarioResponseDTO.setRgPermissionario(permissionario.getRgPermissionario());
        permissionarioResponseDTO.setOrgaoEmissor(permissionario.getOrgaoEmissor());
        permissionarioResponseDTO.setCnhPermissionario(permissionario.getCnhPermissionario());
        permissionarioResponseDTO.setCategoriaCnhPermissionario(permissionario.getCategoriaCnhPermissionario());
        permissionarioResponseDTO.setUfPermissionario(permissionario.getUfPermissionario());
        permissionarioResponseDTO.setCidadePermissionario(permissionario.getCidadePermissionario());
        permissionarioResponseDTO.setBairroPermissionario(permissionario.getBairroPermissionario());
        permissionarioResponseDTO.setEnderecoPermissionario(permissionario.getEnderecoPermissionario());
        permissionarioResponseDTO.setCelularPermissionario(permissionario.getCelularPermissionario());
        permissionarioResponseDTO.setEmailPermissionario(permissionario.getEmailPermissionario());
        permissionarioResponseDTO.setNumeroQuitacaoMilitar(permissionario.getNumeroQuitacaoMilitar());
        permissionarioResponseDTO.setNumeroQuitacaoEleitoral(permissionario.getNumeroQuitacaoEleitoral());
        permissionarioResponseDTO.setNumeroInscricaoInss(permissionario.getNumeroInscricaoInss());
        permissionarioResponseDTO.setNumeroCertificadoCondutor(permissionario.getNumeroCertificadoCondutor());
        permissionarioResponseDTO.setCertidaoNegativaCriminal(permissionario.getCertidaoNegativaCriminal());
        permissionarioResponseDTO.setCertidaoNegativaMunicipal(permissionario.getCertidaoNegativaMunicipal());
        permissionarioResponseDTO.setFoto(permissionario.getFoto());
        permissionarioResponseDTO.setDataCriacao(permissionario.getDataCriacao().toString());
        permissionarioResponseDTO.setStatus(permissionario.getStatus());
        permissionarioResponseDTO.setAplicativoAlternativo(permissionario.getAplicativoAlternativo());
        permissionarioResponseDTO.setObservacao(permissionario.getObservacao());

        return permissionarioResponseDTO;
    }

    public Permissionario converterPermissionarioDTOToPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                                     MultipartFile certificadoCondutor,
                                                                     MultipartFile certidaoNegativaCriminal,
                                                                     MultipartFile certidaoNegativaMunicipal,
                                                                     MultipartFile foto, Integer tipo) throws IOException {
        Permissionario permissionario = new Permissionario();
        if (permissionarioRequestDTO.getIdPermissionario() != null && permissionarioRequestDTO.getIdPermissionario() != 0){
            permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(permissionarioRequestDTO.getIdPermissionario());
        }

        permissionario.setNumeroPermissao(permissionarioRequestDTO.getNumeroPermissao());
        permissionario.setNomePermissionario(permissionarioRequestDTO.getNomePermissionario());

        if(Objects.nonNull(permissionarioRequestDTO.getCpfPermissionario()) && !permissionarioRequestDTO.getCpfPermissionario().isEmpty()){
            permissionarioRequestDTO.setCpfPermissionario(
                    permissionarioRequestDTO.getCpfPermissionario().replace(".", "").replace("-", "").replace("/", "")
            );
            permissionario.setCpfPermissionario(permissionarioRequestDTO.getCpfPermissionario());
        }

        permissionario.setRgPermissionario(permissionarioRequestDTO.getRgPermissionario());
        permissionario.setOrgaoEmissor(permissionarioRequestDTO.getOrgaoEmissor());
        permissionario.setUfPermissionario(permissionarioRequestDTO.getUfPermissionario());
        permissionario.setCidadePermissionario(permissionarioRequestDTO.getCidadePermissionario());
        permissionario.setBairroPermissionario(permissionarioRequestDTO.getBairroPermissionario());
        permissionario.setEnderecoPermissionario(permissionarioRequestDTO.getEnderecoPermissionario());
        permissionario.setCelularPermissionario(permissionarioRequestDTO.getCelularPermissionario());
        permissionario.setEmailPermissionario(permissionarioRequestDTO.getEmailPermissionario());
        permissionario.setCnhPermissionario(permissionarioRequestDTO.getCnhPermissionario());
        permissionario.setCategoriaCnhPermissionario(permissionarioRequestDTO.getCategoriaCnhPermissionario());
        permissionario.setNumeroQuitacaoMilitar(permissionarioRequestDTO.getNumeroQuitacaoMilitar());
        permissionario.setNumeroQuitacaoEleitoral(permissionarioRequestDTO.getNumeroQuitacaoEleitoral());
        permissionario.setNumeroCertificadoCondutor(permissionarioRequestDTO.getNumeroCertificadoCondutor());
        permissionario.setNumeroInscricaoInss(permissionarioRequestDTO.getNumeroInscricaoInss());

        if(Objects.nonNull(certificadoCondutor))
            permissionario.setCertificadoCondutor(certificadoCondutor.getBytes());
        if(Objects.nonNull(certidaoNegativaCriminal))
            permissionario.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
        if(Objects.nonNull(certidaoNegativaMunicipal))
            permissionario.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
        if(Objects.nonNull(foto))
            permissionario.setFoto(foto.getBytes());

        if(Objects.nonNull(permissionarioRequestDTO.getDataCriacao()) && !permissionarioRequestDTO.getDataCriacao().isEmpty())
            permissionario.setDataCriacao(LocalDate.parse(permissionarioRequestDTO.getDataCriacao()));
        else
            permissionario.setDataCriacao(LocalDate.now());

        permissionario.setAplicativoAlternativo(permissionarioRequestDTO.getAplicativoAlternativo());
        permissionario.setObservacao(permissionarioRequestDTO.getObservacao());

        permissionario.setStatus("ATIVO");

        return  permissionario;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public byte[] gerarPermissaoTaxi(String numeroPermissao) {
        logger.info("Início Gerar Permissão de Táxi Busca dos Dados");
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
            if(Objects.isNull(pontoTaxi))
                throw new RuntimeException("403");

            byte[] bytes = gerarPermissaoTaxiJasper(permissao, veiculo, pontoTaxi, permissionario);
            return bytes;
        } catch (Exception e){
            logger.error("gerarPermissaoTaxi - Permissionário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarPermissaoTaxiJasper(Permissao permissao, Veiculo veiculo, PontoTaxi pontoTaxi, Permissionario permissionario) {
        logger.info("Início Gerar Permissão de Táxi Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/permissaoTaxi.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoPermissaoTaxi.png" ).getAbsolutePath());
            FileInputStream  rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapePermissaoTaxi.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemRodape", rodapeStream);
            //PERMISSÃO
            parameters.put("numeroPermissao", permissao.getNumeroPermissao());
            parameters.put("dataEmissao", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
            parameters.put("categoriaServicoAutorizado", CarregarTipos.carregarCategoriaVeiculo(veiculo.getTipoVeiculo()));
            parameters.put("pet", pontoTaxi.getDescricaoPonto());
            parameters.put("statusPermissao", CarregarTipos.carregarStatusPermissao(permissao.getStatusPermissao()));
            parameters.put("validadePermissao", "De " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataCriacao()) +
                    " até " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissao.getDataValidadePermissao()));
            //PERMISSIONÁRIO
            parameters.put("nome", permissionario.getNomePermissionario());
            parameters.put("cpf", permissionario.getCpfPermissionario());
            parameters.put("rg", permissionario.getRgPermissionario());
            parameters.put("cnh", permissionario.getCnhPermissionario());
            parameters.put("categoriaCnh", CarregarTipos.carregarCategoriaCnh(permissionario.getCategoriaCnhPermissionario()));
            parameters.put("dataNascimento", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataNascimento()));
            parameters.put("sexo", permissionario.getSexo().equals("1") ? "Masculino" : "Feminino");
            parameters.put("estadoCivil", CarregarTipos.carregarEstadoCivilPermissionario(permissionario.getEstadoCivil()));
            parameters.put("endereco", permissionario.getEnderecoPermissionario());
            parameters.put("celular", permissionario.getCelularPermissionario());
            parameters.put("email", permissionario.getEmailPermissionario());
            //VEÍCULO
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("tipoCombustivel", CarregarTipos.carregarTipoCombustivelVeiculo(veiculo.getCombustivel()));
            parameters.put("cor", veiculo.getCor().equals("1") ? "Branca" : "Prata");
            parameters.put("chassi", veiculo.getChassi());
            parameters.put("dataVistoriaKilometragem", Objects.nonNull(veiculo.getDataVistoria()) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(veiculo.getDataVistoria()) + " -- " + veiculo.getQuilometragem() + "km" : "");
            parameters.put("quilometragem", veiculo.getQuilometragem());
            parameters.put("capacidade", veiculo.getCapacidade());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarPermissaoTaxiJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }
}

