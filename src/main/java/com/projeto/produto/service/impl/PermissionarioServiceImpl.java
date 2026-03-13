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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    private DefensorRepository defensorRepository;

    private static final Logger logger = LogManager.getLogger(Permissionario.class);

    @Transactional
    public PermissionarioResponseDTO inserirPermissionario(    PermissionarioRequestDTO permissionarioRequestDTO,
                                                               MultipartFile anexoRg,
                                                               MultipartFile anexoCpf,
                                                               MultipartFile anexoCnh,
                                                               MultipartFile comprovanteResidencia,
                                                               MultipartFile certidaoNegativaMunicipal,
                                                               MultipartFile certidaoNegativaCriminal,
                                                               MultipartFile certificadoPropriedade,
                                                               MultipartFile certificadoCondutor,
                                                               MultipartFile apoliceSeguroVida,
                                                               MultipartFile apoliceSeguroMotocicleta,
                                                               MultipartFile foto) {

        logger.info("Início Inserir Autorizatário");
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
                    permissionarioRequestDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia, certidaoNegativaMunicipal,
                    certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor, apoliceSeguroVida,
                    apoliceSeguroMotocicleta, foto, 1
            );
            permissionario = permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("AUTORIZATÁRIO TÁXI", "INCLUSÃO", permissionarioRequestDTO.getUsuario());
        } catch (Exception e){
            logger.error("inserirPermissionario - " + e.getMessage());
            throw new RuntimeException("Não foi possível inserir os dados do Autorizatário!");
        }

        return converterPermissionarioToPermissionarioDTO(permissionario);
    }

    @Transactional
    public PermissionarioResponseDTO atualizarPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                             MultipartFile anexoRg,
                                                             MultipartFile anexoCpf,
                                                             MultipartFile anexoCnh,
                                                             MultipartFile comprovanteResidencia,
                                                             MultipartFile certidaoNegativaMunicipal,
                                                             MultipartFile certidaoNegativaCriminal,
                                                             MultipartFile certificadoPropriedade,
                                                             MultipartFile certificadoCondutor,
                                                             MultipartFile apoliceSeguroVida,
                                                             MultipartFile apoliceSeguroMotocicleta,
                                                             MultipartFile foto) {
        logger.info("Início Atualização Autorizatário");
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
                    permissionarioRequestDTO, anexoRg, anexoCpf, anexoCnh, comprovanteResidencia, certidaoNegativaMunicipal,
                    certidaoNegativaCriminal, certificadoPropriedade, certificadoCondutor, apoliceSeguroVida,
                    apoliceSeguroMotocicleta, foto, 2
            );

            permissionario = permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("AUTORIZATÁRIO TÁXI", "ALTERAÇÃO", permissionarioRequestDTO.getUsuario());
        } catch (Exception e){
            logger.error("atualizarPermissionario - " + e.getMessage());
            throw new RuntimeException("Não foi possível alterar os dados do Autorizatário!");
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

    public Page<PermissionarioResponseDTO> listarTodosPermissionarioFiltros(String nomePermissionario, String cpfPermissionario,
                                                                            String cnhPermissionario, PageRequest pageRequest) {
        logger.error("Início da Listagem de Todos os Dados do Autorizatário");
        try{
            if(Objects.nonNull(cpfPermissionario) && !cpfPermissionario.equals(""))
                cpfPermissionario = StringUtils.leftPad(cpfPermissionario, 11, "0");
            List<Permissionario> listaPermissionario = permissionarioRepository.listarTodosPermissionariosFiltros(
                    nomePermissionario != null ? nomePermissionario.toUpperCase() : nomePermissionario,
                    cpfPermissionario, cnhPermissionario, pageRequest
            );

            Integer countRegistros = permissionarioRepository.listarTodosPermissionariosFiltros(
                    nomePermissionario != null ? nomePermissionario.toUpperCase() : nomePermissionario,
                    cpfPermissionario, cnhPermissionario, null
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
            throw new RuntimeException("Não foi possível listar todos os dados dos Autorizatários!");
        }
    }

    public List<PermissionarioResponseDTO> listarPermissionariosDisponiveis(Long idPermissionario) {
        logger.info("Início da Listagem dos Autorizatários disponíveis para o Veículo/Defensor");
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
            throw new RuntimeException("Não foi possível listar os Autorizatários disponíveis para o Veículo/Defensor!");
        }
    }

    public List<PermissionarioResponseDTO> listarPermissionariosDisponiveisDefensor(Long idPermissionario) {
        logger.info("Início da Listagem dos Autorizatários disponíveis para o Defensor");
        try{
            List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
            List<Permissionario> listaPermissionario = permissionarioRepository.listarPermissionariosDisponiveisDefensor();
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
            throw new RuntimeException("Não foi possível listar os Autorizatários disponíveis para o Defensor!");
        }
    }

    @Transactional
    public ResponseEntity<Void> excluirPermissionario(Long idPermissionario, String usuario) {
        logger.info("Início da Exclusão do Autorizatário");
        String msgErro = "Erro ao Excluir o Autorizatário!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
            Veiculo veiculo = veiculoRepository.findVeiculoByPermissionarioAndStatus(permissionario, "ATIVO");
            if(Objects.nonNull(veiculo)){
                msgErro = "Existe um Veículo associado a este Autorizatário";
                logger.error("excluirPermissionario - " + msgErro);
                throw new Exception();
            }

            permissionario.setStatus("INATIVO");
            permissionarioRepository.save(permissionario);

            //Auditoria
            salvarAuditoria("AUTORIZATÁRIO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            logger.error("excluirPermissionario - " + e.getMessage());
            throw new RuntimeException(msgErro);
        }
    }

    public List<PermissionarioResponseDTO> converterEntityToDTO(List<Permissionario> listaPermissionario){
        List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
        for(Permissionario permissionario : listaPermissionario){
            permissionario.setDataNascimento(permissionario.getDataNascimento().plusDays(1));
            permissionario.setDataValidadeCnh(permissionario.getDataValidadeCnh().plusDays(1));
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
        permissionarioResponseDTO.setFiliacaoMae(permissionario.getFiliacaoMae());
        permissionarioResponseDTO.setFiliacaoPai(permissionario.getFiliacaoPai());
        permissionarioResponseDTO.setSexo(permissionario.getSexo());
        permissionarioResponseDTO.setEstadoCivil(permissionario.getEstadoCivil());
        permissionarioResponseDTO.setDataNascimento(permissionario.getDataNascimento().toString());
        permissionarioResponseDTO.setCnhPermissionario(permissionario.getCnhPermissionario());
        permissionarioResponseDTO.setCategoriaCnhPermissionario(permissionario.getCategoriaCnhPermissionario());
        permissionarioResponseDTO.setDataValidadeCnh(permissionario.getDataValidadeCnh().toString());
        permissionarioResponseDTO.setUfPermissionario(permissionario.getUfPermissionario());
        permissionarioResponseDTO.setCidadePermissionario(permissionario.getCidadePermissionario());
        permissionarioResponseDTO.setBairroPermissionario(permissionario.getBairroPermissionario());
        permissionarioResponseDTO.setEnderecoPermissionario(permissionario.getEnderecoPermissionario());
        permissionarioResponseDTO.setCep(permissionario.getCep());
        permissionarioResponseDTO.setCelularPermissionario(permissionario.getCelularPermissionario());
        permissionarioResponseDTO.setEmailPermissionario(permissionario.getEmailPermissionario());
        permissionarioResponseDTO.setNumeroQuitacaoMilitar(permissionario.getNumeroQuitacaoMilitar());
        permissionarioResponseDTO.setNumeroQuitacaoEleitoral(permissionario.getNumeroQuitacaoEleitoral());
        permissionarioResponseDTO.setNumeroInscricaoInss(permissionario.getNumeroInscricaoInss());
        permissionarioResponseDTO.setNumeroCertificadoCondutor(permissionario.getNumeroCertificadoCondutor());
        permissionarioResponseDTO.setAnexoRg(permissionario.getAnexoRg());
        permissionarioResponseDTO.setAnexoCpf(permissionario.getAnexoCpf());
        permissionarioResponseDTO.setAnexoCnh(permissionario.getAnexoCnh());
        permissionarioResponseDTO.setComprovanteResidencia(permissionario.getComprovanteResidencia());
        permissionarioResponseDTO.setCertidaoNegativaMunicipal(permissionario.getCertidaoNegativaMunicipal());
        permissionarioResponseDTO.setCertidaoNegativaCriminal(permissionario.getCertidaoNegativaCriminal());
        permissionarioResponseDTO.setCertificadoPropriedade(permissionario.getCertificadoPropriedade());
        permissionarioResponseDTO.setCertificadoCondutor(permissionario.getCertificadoCondutor());
        permissionarioResponseDTO.setApoliceSeguroVida(permissionario.getApoliceSeguroVida());
        permissionarioResponseDTO.setApoliceSeguroMotocicleta(permissionario.getApoliceSeguroMotocicleta());
        permissionarioResponseDTO.setFoto(permissionario.getFoto());
        permissionarioResponseDTO.setDataCriacao(permissionario.getDataCriacao().toString());
        permissionarioResponseDTO.setStatus(permissionario.getStatus());
        permissionarioResponseDTO.setAplicativoAlternativo(permissionario.getAplicativoAlternativo());
        permissionarioResponseDTO.setObservacao(permissionario.getObservacao());

        return permissionarioResponseDTO;
    }

    public Permissionario converterPermissionarioDTOToPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                                     MultipartFile anexoRg,
                                                                     MultipartFile anexoCpf,
                                                                     MultipartFile anexoCnh,
                                                                     MultipartFile comprovanteResidencia,
                                                                     MultipartFile certidaoNegativaMunicipal,
                                                                     MultipartFile certidaoNegativaCriminal,
                                                                     MultipartFile certificadoPropriedade,
                                                                     MultipartFile certificadoCondutor,
                                                                     MultipartFile apoliceSeguroVida,
                                                                     MultipartFile apoliceSeguroMotocicleta,
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
            permissionario.setCpfPermissionario(StringUtils.leftPad(permissionarioRequestDTO.getCpfPermissionario(), 11, "0"));
        }

        permissionario.setRgPermissionario(permissionarioRequestDTO.getRgPermissionario());
        permissionario.setOrgaoEmissor(permissionarioRequestDTO.getOrgaoEmissor());
        permissionario.setFiliacaoMae(permissionarioRequestDTO.getFiliacaoMae());
        permissionario.setFiliacaoPai(permissionarioRequestDTO.getFiliacaoPai());
        permissionario.setSexo(permissionarioRequestDTO.getSexo());
        permissionario.setEstadoCivil(permissionarioRequestDTO.getEstadoCivil());
        if(Objects.nonNull(permissionarioRequestDTO.getDataNascimento())) {
            permissionario.setDataNascimento(LocalDate.parse(permissionarioRequestDTO.getDataNascimento()));
        }
        permissionario.setUfPermissionario(permissionarioRequestDTO.getUfPermissionario());
        permissionario.setCidadePermissionario(permissionarioRequestDTO.getCidadePermissionario());
        permissionario.setBairroPermissionario(permissionarioRequestDTO.getBairroPermissionario());
        permissionario.setEnderecoPermissionario(permissionarioRequestDTO.getEnderecoPermissionario());
        permissionario.setCep(permissionarioRequestDTO.getCep());
        permissionario.setCelularPermissionario(permissionarioRequestDTO.getCelularPermissionario());
        permissionario.setEmailPermissionario(permissionarioRequestDTO.getEmailPermissionario());
        permissionario.setCnhPermissionario(permissionarioRequestDTO.getCnhPermissionario());
        permissionario.setCategoriaCnhPermissionario(permissionarioRequestDTO.getCategoriaCnhPermissionario());
        if(Objects.nonNull(permissionarioRequestDTO.getDataValidadeCnh())) {
            permissionario.setDataValidadeCnh(LocalDate.parse(permissionarioRequestDTO.getDataValidadeCnh()));
        }
        permissionario.setNumeroQuitacaoMilitar(permissionarioRequestDTO.getNumeroQuitacaoMilitar());
        permissionario.setNumeroQuitacaoEleitoral(permissionarioRequestDTO.getNumeroQuitacaoEleitoral());
        permissionario.setNumeroCertificadoCondutor(permissionarioRequestDTO.getNumeroCertificadoCondutor());
        permissionario.setNumeroInscricaoInss(permissionarioRequestDTO.getNumeroInscricaoInss());

        if(Objects.nonNull(anexoRg))
            permissionario.setAnexoRg(anexoRg.getBytes());
        if(Objects.nonNull(anexoCpf))
            permissionario.setAnexoCpf(anexoCpf.getBytes());
        if(Objects.nonNull(anexoCnh))
            permissionario.setAnexoCnh(anexoCnh.getBytes());
        if(Objects.nonNull(comprovanteResidencia))
            permissionario.setComprovanteResidencia(comprovanteResidencia.getBytes());
        if(Objects.nonNull(certidaoNegativaMunicipal))
            permissionario.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
        if(Objects.nonNull(certidaoNegativaCriminal))
            permissionario.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
        if(Objects.nonNull(certificadoPropriedade))
            permissionario.setCertificadoPropriedade(certificadoPropriedade.getBytes());
        if(Objects.nonNull(certificadoCondutor))
            permissionario.setCertificadoCondutor(certificadoCondutor.getBytes());
        if(Objects.nonNull(apoliceSeguroVida))
            permissionario.setApoliceSeguroVida(apoliceSeguroVida.getBytes());
        if(Objects.nonNull(apoliceSeguroMotocicleta))
            permissionario.setApoliceSeguroMotocicleta(apoliceSeguroMotocicleta.getBytes());
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

    public byte[] gerarRegistroCondutor(String cpfPermissionario, String modulo) {
        logger.info("Início Gerar Registro Condutor Busca dos Dados");
        try{
            Permissionario permissionario = permissionarioRepository.findPermissionarioByCpfPermissionario(cpfPermissionario);
            if(Objects.isNull(permissionario))
                throw new RuntimeException("400");


            Veiculo veiculo = veiculoRepository.findVeiculoByPermissionarioAndStatus(permissionario, "ATIVO");
            if(Objects.isNull(veiculo))
                throw new RuntimeException("401");

            Defensor defensor = defensorRepository.findDefensorByPermissionario(permissionario);

            byte[] bytes = gerarRegistroCondutorJasper(defensor, veiculo, permissionario, modulo);
            return bytes;
        } catch (Exception e){
            logger.error("gerarRegistroCondutor - Autorizatário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarRegistroCondutorJasper(Defensor defensor, Veiculo veiculo, Permissionario permissionario, String modulo) {
        logger.info("Início Gerar Registro Condutor Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/registroCondutorMoto.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoRegistroCondutorMoto.png" ).getAbsolutePath());
            FileInputStream rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeRegistroCondutorMoto.png" ).getAbsolutePath());
            InputStream fotoStream = new ByteArrayInputStream(permissionario.getFoto());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemRodape", rodapeStream);
            parameters.put("imagemFoto", fotoStream);

            //REGISTRO DO CONDUTOR
            parameters.put("numeroRcmt", permissionario.getDataCriacao().getYear() + " / " + permissionario.getIdPermissionario());
            parameters.put("dataEmissao", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataCriacao()));
            parameters.put("tipoCondutor", "[x] Autorizatário [ ] Defensor");
            parameters.put("numeroTas", permissionario.getDataCriacao().getYear() + " / " + veiculo.getIdVeiculo());
            parameters.put("numeroCcmt", String.valueOf(permissionario.getIdPermissionario()));
            parameters.put("numeroCvmt", String.valueOf(veiculo.getIdVeiculo()));
            parameters.put("numeroCav", Objects.nonNull(veiculo.getNumeroCavEmitido()) ? veiculo.getNumeroCavEmitido() : "");
            parameters.put("statusRegistro", permissionario.getStatus());

            parameters.put("categoriaServicoAutorizado", CarregarTipos.carregarCategoriaVeiculo(veiculo.getTipoVeiculo()));
            parameters.put("validadeRegistroCondutor", "De " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataCriacao()) +
                    " até " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataValidadeCertificadoCondutor()));

            //PERMISSIONÁRIO/AUTORIZATÁRIO
            parameters.put("nome", permissionario.getNomePermissionario());
            parameters.put("cpf", permissionario.getCpfPermissionario());
            parameters.put("rg", permissionario.getRgPermissionario());
            parameters.put("orgao", permissionario.getOrgaoEmissor());
            parameters.put("cnh", permissionario.getCnhPermissionario());
            parameters.put("categoriaCnh", CarregarTipos.carregarCategoriaCnh(permissionario.getCategoriaCnhPermissionario()));
            parameters.put("dataValidadeCnh", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataValidadeCnh()));
            parameters.put("dataNascimento", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(permissionario.getDataNascimento()));
            parameters.put("sexo", permissionario.getSexo().equals("1") ? "Masculino" : "Feminino");
            parameters.put("estadoCivil", CarregarTipos.carregarEstadoCivil(permissionario.getEstadoCivil()));
            parameters.put("filiacao", permissionario.getFiliacaoMae() + "/" + (Objects.nonNull(permissionario.getFiliacaoPai()) ? permissionario.getFiliacaoPai() : ""));
            parameters.put("endereco", permissionario.getEnderecoPermissionario() + " - CEP: " + permissionario.getCep());
            parameters.put("celular", permissionario.getCelularPermissionario());
            parameters.put("email", Objects.nonNull(permissionario.getEmailPermissionario()) ? permissionario.getEmailPermissionario() : "");

            //DOCUMENTAÇÃO EXIGIDA
            String documentacaoExigida1 = "";
            if(Objects.nonNull(permissionario.getAnexoRg()))
                documentacaoExigida1 = documentacaoExigida1 + "[x] RG (Carteira de Identidade)\n";
            else
                documentacaoExigida1 = documentacaoExigida1 + "[ ] RG (Carteira de Identidade)\n";
            if(Objects.nonNull(permissionario.getAnexoCpf()))
                documentacaoExigida1 = documentacaoExigida1 + "[x] CPF\n";
            else
                documentacaoExigida1 = documentacaoExigida1 + "[ ] CPF\n";
            if(Objects.nonNull(permissionario.getAnexoCnh()))
                documentacaoExigida1 = documentacaoExigida1 + "[x] CNH Categoria 'A' (mín. 2 anos de experiência)\n";
            else
                documentacaoExigida1 = documentacaoExigida1 + "[ ] CNH Categoria 'A' (mín. 2 anos de experiência)\n";
            if(Objects.nonNull(permissionario.getComprovanteResidencia()))
                documentacaoExigida1 = documentacaoExigida1 + "[x] Comprovante residência em Barreirinhas (mín. 3 anos)\n";
            else
                documentacaoExigida1 = documentacaoExigida1 + "[ ] Comprovante residência em Barreirinhas (mín. 3 anos)\n";
            if(Objects.nonNull(permissionario.getCertidaoNegativaMunicipal()))
                documentacaoExigida1 = documentacaoExigida1 + "[x] Certidão negativa de multas e ocorrências DETRAN-MA\n";
            else
                documentacaoExigida1 = documentacaoExigida1 + "[ ] Certidão negativa de multas e ocorrências DETRAN-MA\n";

            parameters.put("documentacaoExigida1", documentacaoExigida1);

            String documentacaoExigida2 = "";
            if(Objects.nonNull(permissionario.getCertidaoNegativaCriminal()))
                documentacaoExigida2 = documentacaoExigida2 + "[x] Certidão negativa de antecedentes criminais\n";
            else
                documentacaoExigida2 = documentacaoExigida2 + "[ ] Certidão negativa de antecedentes criminais\n";
            if(Objects.nonNull(permissionario.getCertificadoPropriedade()))
                documentacaoExigida2 = documentacaoExigida2 + "[x] Certificado de propriedade da motocicleta\n";
            else
                documentacaoExigida2 = documentacaoExigida2 + "[ ] Certificado de propriedade da motocicleta\n";
            if(Objects.nonNull(permissionario.getCertificadoCondutor()))
                documentacaoExigida2 = documentacaoExigida2 + "[x] Certificado de curso específico (emitido máx. 2 anos)\n";
            else
                documentacaoExigida2 = documentacaoExigida2 + "[ ] Certificado de curso específico (emitido máx. 2 anos)\n";
            if(Objects.nonNull(permissionario.getApoliceSeguroVida()))
                documentacaoExigida2 = documentacaoExigida2 + "[x] Apólice de seguro de vida\n";
            else
                documentacaoExigida2 = documentacaoExigida2 + "[ ] Apólice de seguro de vida\n";
            if(Objects.nonNull(permissionario.getApoliceSeguroMotocicleta()))
                documentacaoExigida2 = documentacaoExigida2 + "[x] Apólice de seguro da motocicleta (acidentes, furto, incêndio, terceiros)\n";
            else
                documentacaoExigida2 = documentacaoExigida2 + "[ ] Apólice de seguro da motocicleta (acidentes, furto, incêndio, terceiros)\n";

            parameters.put("documentacaoExigida2", documentacaoExigida2);

            //VEÍCULO
            parameters.put("nomePermissionario", permissionario.getNomePermissionario());
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("renavam", veiculo.getRenavam());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("cor", obterCor(veiculo.getCor()));
            parameters.put("cilindrada", Objects.nonNull(veiculo.getCilindrada()) ? veiculo.getCilindrada() : "");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarRegistroCondutorJasper: " + e.getMessage());
            throw new RuntimeException("500");
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

