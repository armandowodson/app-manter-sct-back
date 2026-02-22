package com.projeto.produto.service.impl;

import com.projeto.produto.dto.DefensorRequestDTO;
import com.projeto.produto.dto.DefensorResponseDTO;
import com.projeto.produto.entity.*;
import com.projeto.produto.repository.*;
import com.projeto.produto.utils.CarregarTipos;
import com.projeto.produto.utils.ValidaCPF;
import com.projeto.produto.utils.ValidaEmail;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DefensorServiceImpl {
    @Autowired
    private DefensorRepository defensorRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    private static final Logger logger = LogManager.getLogger(AuditoriaServiceImpl.class);

    @Transactional
    public DefensorResponseDTO inserirDefensor(    DefensorRequestDTO defensorRequestDTO,
                                                   MultipartFile certificadoCondutor,
                                                   MultipartFile certidaoNegativaCriminal,
                                                   MultipartFile certidaoNegativaMunicipal,
                                                   MultipartFile foto) {
        logger.info("Início Inserir Defensor");
        try {
            if(Objects.nonNull(defensorRequestDTO.getCpfDefensor()) && !defensorRequestDTO.getCpfDefensor().isEmpty() &&
                    !ValidaCPF.isCPF(defensorRequestDTO.getCpfDefensor()))
                throw new RuntimeException("O CPF " + defensorRequestDTO.getCpfDefensor() + " é inválido!");

            if(Objects.nonNull(defensorRequestDTO.getEmailDefensor()) && !defensorRequestDTO.getEmailDefensor().isEmpty() &&
                    !ValidaEmail.isEmail(defensorRequestDTO.getEmailDefensor()))
                throw new RuntimeException("O E-mail " + defensorRequestDTO.getEmailDefensor() + " é inválido!");

            if(Objects.isNull(defensorRequestDTO.getUsuario()) || defensorRequestDTO.getUsuario().isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Defensor defensor = new Defensor();

            defensor = converterDefensorDTOToDefensor(
                    defensorRequestDTO, certificadoCondutor, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 1
            );
            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "INCLUSÃO", defensorRequestDTO.getUsuario());

            return converterDefensorToDefensorDTO(defensor);
        } catch (Exception e){
            logger.error("inserirDefensor: " + e.getMessage());
            throw new RuntimeException("Não foi possível inserir os dados do Defensor!");
        }
    }

    @Transactional
    public DefensorResponseDTO atualizarDefensor(DefensorRequestDTO defensorRequestDTO,
                                                 MultipartFile certificadoCondutor,
                                                 MultipartFile certidaoNegativaCriminal,
                                                 MultipartFile certidaoNegativaMunicipal,
                                                 MultipartFile foto) {
        logger.info("Início Atualizar Defensor");
        try{
            if(Objects.nonNull(defensorRequestDTO.getCpfDefensor()) && !defensorRequestDTO.getCpfDefensor().isEmpty() &&
                    !ValidaCPF.isCPF(defensorRequestDTO.getCpfDefensor()))
                throw new RuntimeException("O CPF " + defensorRequestDTO.getCpfDefensor() + " é inválido!");

            if(Objects.nonNull(defensorRequestDTO.getEmailDefensor()) && !defensorRequestDTO.getEmailDefensor().isEmpty() &&
                    !ValidaEmail.isEmail(defensorRequestDTO.getEmailDefensor()))
                throw new RuntimeException("O E-mail " + defensorRequestDTO.getEmailDefensor() + " é inválido!");

            if(Objects.isNull(defensorRequestDTO.getUsuario()) || defensorRequestDTO.getUsuario().isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Defensor defensor = new Defensor();

            defensor = converterDefensorDTOToDefensor(
                    defensorRequestDTO, certificadoCondutor, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 2
            );

            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "ALTERAÇÃO", defensorRequestDTO.getUsuario());

            return converterDefensorToDefensorDTO(defensorRepository.save(defensor));
        } catch (Exception e){
            logger.error("atualizarDefensor: " + e.getMessage());
            throw new RuntimeException("Não foi possível alterar os dados do Defensor!");
        }
    }

    public Page<DefensorResponseDTO> listarTodosDefensors(PageRequest pageRequest) {
        logger.info("Início Listar Todos Defensores");
        try{
            List<Defensor> defensorList = defensorRepository.buscarTodos(pageRequest);
            Integer countLista = defensorRepository.buscarTodos(null).size();
            List<DefensorResponseDTO> defensorResponseDTOList = converterEntityToDTO(defensorList);
            return new PageImpl<>(defensorResponseDTOList, pageRequest, countLista);
        } catch (Exception e){
            logger.error("listarTodosDefensors: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todos os Defensores!");
        }
    }

    public DefensorResponseDTO buscarDefensorId(Long idDefensor) {
        logger.info("Início Buscar Defensor por ID");
        try{
            Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
            DefensorResponseDTO defensorResponseDTO = new DefensorResponseDTO();
            if (defensor != null){
                defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
            }
            return defensorResponseDTO;
        } catch (Exception e){
            logger.error("buscarDefensorId: " + e.getMessage());
            throw new RuntimeException("Erro ao Buscar Defensor por ID!");
        }
    }

    public DefensorResponseDTO buscarDefensorNumeroPermissao(String numeroPermissao) {
        logger.info("Início Buscar Defensor por Número de Permissão");
        try{
            Defensor defensor = defensorRepository.findDefensorByNumeroPermissao(numeroPermissao);
            DefensorResponseDTO defensorResponseDTO = new DefensorResponseDTO();
            if (defensor != null){
                defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
            }
            return defensorResponseDTO;
        } catch (Exception e){
            logger.error("buscarDefensorNumeroPermissao: " + e.getMessage());
            throw new RuntimeException("Erro ao Buscar Defensor por Número de Permissão!");
        }
    }

    public Page<DefensorResponseDTO> listarTodosDefensorFiltros(   String numeroPermissao, String nomeDefensor, String cpfDefensor,
                                                                   String cnhDefensor, String nomePermissionario,
                                                                   String cpfPermissionario, PageRequest pageRequest) {
        logger.info("Início Listar Todos os Defensores por Filtros");
        try{
            List<Defensor> listaDefensor = defensorRepository.listarTodosDefensorsFiltros(
                    numeroPermissao,  nomeDefensor != null ? nomeDefensor.toUpperCase() : nomeDefensor,
                    cpfDefensor, cnhDefensor, nomePermissionario, cpfPermissionario, pageRequest
            );

            Integer countRegistros = defensorRepository.listarTodosDefensorsFiltros(
                    numeroPermissao,  nomeDefensor != null ? nomeDefensor.toUpperCase() : nomeDefensor,
                    cpfDefensor, cnhDefensor, nomePermissionario, cpfPermissionario, null
            ).size();

            List<DefensorResponseDTO> listaDefensorResponseDTO = new ArrayList<>();
            if (!listaDefensor.isEmpty()){
                for (Defensor defensor : listaDefensor) {
                    DefensorResponseDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensor);
                    listaDefensorResponseDTO.add(defensorResponseDTORetornado);
                }
            }

            return new PageImpl<>(listaDefensorResponseDTO, pageRequest, countRegistros);
        } catch (Exception e){
            logger.error("listarTodosDefensorFiltros: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todos os Defensores por Filtros!");
        }
    }

    public List<DefensorResponseDTO> listarDefensoresDisponiveis(Long idDefensor) {
        logger.info("Início Listar Defensores Disponíveis");
        try{
            List<DefensorResponseDTO> listaDefensorResponseDTO = new ArrayList<>();
            List<Defensor> listaDefensor = defensorRepository.listarDefensorsDisponiveis();
            if(Objects.nonNull(idDefensor)){
                Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
                DefensorResponseDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensor);
                listaDefensorResponseDTO.add(defensorResponseDTORetornado);
            }

            if (!listaDefensor.isEmpty()){
                for (Defensor defensorDisponivel : listaDefensor) {
                    DefensorResponseDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensorDisponivel);
                    listaDefensorResponseDTO.add(defensorResponseDTORetornado);
                }
            }

            return listaDefensorResponseDTO;
        } catch (Exception e){
            logger.error("listarDefensoresDisponiveis: " + e.getMessage());
            throw new RuntimeException("Erro ao Listar Todos os Defensores Disponíveis!");
        }
    }

    @Transactional
    public ResponseEntity<Void> excluirDefensor(Long idDefensor, String usuario) {
        logger.info("Início Excluir Defensor");
        String msgErro = "Erro ao Excluir o Defensor!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário não logado ou não identificado!");

            Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
            defensor.setStatus("INATIVO");
            defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        } catch (Exception e){
            logger.error("excluirDefensor: " + e.getMessage());
            throw new RuntimeException(msgErro);
        }
    }

    public List<DefensorResponseDTO> converterEntityToDTO(List<Defensor> listaDefensor){
        List<DefensorResponseDTO> listaDefensorResponseDTO = new ArrayList<>();
        for(Defensor defensor : listaDefensor){
            DefensorResponseDTO defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
            listaDefensorResponseDTO.add(defensorResponseDTO);
        }

        return listaDefensorResponseDTO;
    }

    public DefensorResponseDTO converterDefensorToDefensorDTO(Defensor defensor){
        logger.info("Início Converter Defensor para DTO");
        try{
            DefensorResponseDTO defensorResponseDTO = new DefensorResponseDTO();
            if (defensor.getIdDefensor() != null){
                defensorResponseDTO.setIdDefensor(defensor.getIdDefensor());
            }

            defensorResponseDTO.setNumeroPermissao(defensor.getNumeroPermissao());
            defensorResponseDTO.setNomeDefensor(defensor.getNomeDefensor());
            defensorResponseDTO.setCpfDefensor(defensor.getCpfDefensor());
            defensorResponseDTO.setRgDefensor(defensor.getRgDefensor());
            defensorResponseDTO.setOrgaoEmissor(defensor.getOrgaoEmissor());
            defensorResponseDTO.setSexo(defensor.getSexo());
            defensorResponseDTO.setEstadoCivil(defensor.getEstadoCivil());
            defensorResponseDTO.setDataNascimento(defensor.getDataNascimento().toString());
            defensorResponseDTO.setCnhDefensor(defensor.getCnhDefensor());
            defensorResponseDTO.setCategoriaCnhDefensor(defensor.getCategoriaCnhDefensor());
            defensorResponseDTO.setUfDefensor(defensor.getUfDefensor());
            defensorResponseDTO.setCidadeDefensor(defensor.getCidadeDefensor());
            defensorResponseDTO.setBairroDefensor(defensor.getBairroDefensor());
            defensorResponseDTO.setEnderecoDefensor(defensor.getEnderecoDefensor());
            defensorResponseDTO.setCelularDefensor(defensor.getCelularDefensor());
            defensorResponseDTO.setEmailDefensor(defensor.getEmailDefensor());
            defensorResponseDTO.setNumeroQuitacaoMilitar(defensor.getNumeroQuitacaoMilitar());
            defensorResponseDTO.setNumeroQuitacaoEleitoral(defensor.getNumeroQuitacaoEleitoral());
            defensorResponseDTO.setNumeroInscricaoInss(defensor.getNumeroInscricaoInss());
            defensorResponseDTO.setNumeroCertificadoCondutor(defensor.getNumeroCertificadoCondutor());
            defensorResponseDTO.setCertificadoCondutor(defensor.getCertificadoCondutor());
            defensorResponseDTO.setCertidaoNegativaCriminal(defensor.getCertidaoNegativaCriminal());
            defensorResponseDTO.setCertidaoNegativaMunicipal(defensor.getCertidaoNegativaMunicipal());
            defensorResponseDTO.setFoto(defensor.getFoto());
            defensorResponseDTO.setDataCriacao(defensor.getDataCriacao().toString());
            defensorResponseDTO.setStatus(defensor.getStatus());

            return defensorResponseDTO;
        } catch (Exception e){
            logger.error("converterDefensorToDefensorDTO: " + e.getMessage());
            throw new RuntimeException("Erro ao Converter Defensor para DTO");
        }
    }

    public Defensor converterDefensorDTOToDefensor(DefensorRequestDTO defensorRequestDTO,
                                                   MultipartFile certificadoCondutor,
                                                   MultipartFile certidaoNegativaCriminal,
                                                   MultipartFile certidaoNegativaMunicipal,
                                                   MultipartFile foto, Integer tipo) throws IOException {
        logger.info("Início Converter DTO para Defensor");

        try{
            Defensor defensor = new Defensor();
            if (defensorRequestDTO.getIdDefensor() != null && defensorRequestDTO.getIdDefensor() != 0){
                defensor = defensorRepository.findDefensorByIdDefensor(defensorRequestDTO.getIdDefensor());
            }

            defensor.setNumeroPermissao(defensorRequestDTO.getNumeroPermissao());
            defensor.setNomeDefensor(defensorRequestDTO.getNomeDefensor());

            if(Objects.nonNull(defensorRequestDTO.getCpfDefensor()) && !defensorRequestDTO.getCpfDefensor().isEmpty()){
                defensorRequestDTO.setCpfDefensor(
                        defensorRequestDTO.getCpfDefensor().replace(".", "").replace("-", "").replace("/", "")
                );
                defensor.setCpfDefensor(defensorRequestDTO.getCpfDefensor());
            }

            defensor.setRgDefensor(defensorRequestDTO.getRgDefensor());
            defensor.setOrgaoEmissor(defensorRequestDTO.getOrgaoEmissor());
            defensor.setSexo(defensorRequestDTO.getSexo());
            defensor.setEstadoCivil(defensorRequestDTO.getEstadoCivil());
            if(Objects.nonNull(defensorRequestDTO.getDataNascimento())) {
                String data = defensorRequestDTO.getDataNascimento();
                Integer indexChar = data.indexOf('T');
                if(indexChar > 0){
                    data = data.substring(0, indexChar);
                    if(tipo == 1){
                        defensor.setDataNascimento(LocalDate.parse(data));
                    }else{
                        defensor.setDataNascimento(LocalDate.parse(data).minusDays(1));
                    }
                }
            }
            defensor.setUfDefensor(defensorRequestDTO.getUfDefensor());
            defensor.setCidadeDefensor(defensorRequestDTO.getCidadeDefensor());
            defensor.setBairroDefensor(defensorRequestDTO.getBairroDefensor());
            defensor.setEnderecoDefensor(defensorRequestDTO.getEnderecoDefensor());
            defensor.setCelularDefensor(defensorRequestDTO.getCelularDefensor());
            defensor.setEmailDefensor(defensorRequestDTO.getEmailDefensor());
            defensor.setCnhDefensor(defensorRequestDTO.getCnhDefensor());
            defensor.setCategoriaCnhDefensor(defensorRequestDTO.getCategoriaCnhDefensor());
            defensor.setNumeroQuitacaoMilitar(defensorRequestDTO.getNumeroQuitacaoMilitar());
            defensor.setNumeroQuitacaoEleitoral(defensorRequestDTO.getNumeroQuitacaoEleitoral());
            defensor.setNumeroCertificadoCondutor(defensorRequestDTO.getNumeroCertificadoCondutor());
            defensor.setNumeroInscricaoInss(defensorRequestDTO.getNumeroInscricaoInss());

            if(Objects.nonNull(certificadoCondutor))
                defensor.setCertificadoCondutor(certificadoCondutor.getBytes());
            if(Objects.nonNull(certidaoNegativaCriminal))
                defensor.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
            if(Objects.nonNull(certidaoNegativaMunicipal))
                defensor.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
            if(Objects.nonNull(foto))
                defensor.setFoto(foto.getBytes());

            if(Objects.nonNull(defensorRequestDTO.getDataCriacao()) && !defensorRequestDTO.getDataCriacao().isEmpty())
                defensor.setDataCriacao(LocalDate.parse(defensorRequestDTO.getDataCriacao()));
            else
                defensor.setDataCriacao(LocalDate.now());

            defensor.setStatus("ATIVO");

            return  defensor;
        } catch (Exception e){
            logger.error("converterDefensorDTOToDefensor: " + e.getMessage());
            throw new RuntimeException("Erro ao Converter DTO para Defensor");
        }
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        logger.info("Início Salvar Auditoria");
        try{
            Auditoria auditoria = new Auditoria();
            auditoria.setNomeModulo(modulo);
            auditoria.setOperacao(operacao);
            auditoria.setUsuarioOperacao(usuario);
            auditoria.setDataOperacao(LocalDate.now());
            auditoriaRepository.save(auditoria);
        } catch (Exception e){
            logger.error("salvarAuditoria: " + e.getMessage());
        }
    }

    public byte[] gerarRegistroCondutor(String numeroPermissao) {
        logger.info("Início Gerar Registro Condutor Busca dos Dados");
        try{
            Permissao permissao = permissaoRepository.findPermissaoByNumeroPermissao(numeroPermissao);
            if(Objects.isNull(permissao))
                throw new RuntimeException("400");

            Veiculo veiculo = veiculoRepository.findVeiculoByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(veiculo))
                throw new RuntimeException("401");

            Defensor defensor = defensorRepository.findDefensorByNumeroPermissao(permissao.getNumeroPermissao());
            if(Objects.isNull(defensor))
                throw new RuntimeException("403");

            byte[] bytes = gerarRegistroCondutorJasper(permissao, veiculo, defensor);
            return bytes;
        } catch (Exception e){
            logger.error("gerarPermissaoTaxi - Permissionário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] gerarRegistroCondutorJasper(Permissao permissao, Veiculo veiculo, Defensor defensor) {
        logger.info("Início Gerar Registro Condutor Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/registroCondutor.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            FileInputStream cabecalhoStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/cabecalhoRegistroCondutor.png" ).getAbsolutePath());
            FileInputStream  rodapeStream  =  new FileInputStream(ResourceUtils.getFile( "src/main/resources/imagens/rodapeRegistroCondutor.png" ).getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemCabecalho", cabecalhoStream);
            parameters.put("imagemRodape", rodapeStream);

            //REGISTRO DO CONDUTOR
            parameters.put("numeroRc", defensor.getNumeroCertificadoCondutor());
            parameters.put("dataEmissao", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(defensor.getDataCriacao()));
            parameters.put("tipoCondutor", "[x] Defensor");
            parameters.put("categoriaServicoAutorizado", CarregarTipos.carregarCategoriaVeiculo(veiculo.getTipoVeiculo()));
            parameters.put("validadeRegistroCondutor", "De " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(defensor.getDataCriacao()) +
                    " até " + "");
            //PERMISSIONÁRIO
            parameters.put("nome", defensor.getNomeDefensor());
            parameters.put("cpf", defensor.getCpfDefensor());
            parameters.put("rg", defensor.getRgDefensor());
            parameters.put("cnh", defensor.getCnhDefensor());
            parameters.put("categoriaCnh", CarregarTipos.carregarCategoriaCnh(defensor.getCategoriaCnhDefensor()));
            parameters.put("dataNascimento", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(defensor.getDataNascimento()));
            parameters.put("sexo", defensor.getSexo().equals("1") ? "Masculino" : "Feminino");
            parameters.put("estadoCivil", CarregarTipos.carregarEstadoCivilPermissionario(defensor.getEstadoCivil()));
            parameters.put("endereco", defensor.getEnderecoDefensor());
            parameters.put("celular", defensor.getCelularDefensor());
            parameters.put("email", Objects.nonNull(defensor.getEmailDefensor()) ? defensor.getEmailDefensor() : "");
            //DOCUMENTAÇÃO EXIGIDA
            String documentacaoExigida = "[x] CPF " + "[x] RG " + "[x] CNH ";
            if(Objects.nonNull(defensor.getNumeroQuitacaoMilitar()) && !defensor.getNumeroQuitacaoMilitar().isEmpty())
                documentacaoExigida = documentacaoExigida + "[x] Quitação Militar ";
            else
                documentacaoExigida = documentacaoExigida + "[ ] Quitação Militar ";

            if(Objects.nonNull(defensor.getNumeroQuitacaoEleitoral()) && !defensor.getNumeroQuitacaoEleitoral().isEmpty())
                documentacaoExigida = documentacaoExigida + "[x] Quitação Eleitoral ";
            else
                documentacaoExigida = documentacaoExigida + "[ ] Quitação Eleitoral ";

            if(Objects.nonNull(defensor.getNumeroInscricaoInss()) && !defensor.getNumeroInscricaoInss().isEmpty())
                documentacaoExigida = documentacaoExigida + "[x] INSS ";
            else
                documentacaoExigida = documentacaoExigida + "[ ] INSS ";

            if(Objects.nonNull(defensor.getCertificadoCondutor()))
                documentacaoExigida = documentacaoExigida + "[x] Curso Transporte Público ";
            else
                documentacaoExigida = documentacaoExigida + "[ ] Curso Transporte Público ";

            documentacaoExigida = documentacaoExigida + "[ ] Atestado Médico ";

            documentacaoExigida = documentacaoExigida + "[ ] Comprovação Residência ";

            if(Objects.nonNull(defensor.getCertidaoNegativaCriminal()) && Objects.nonNull(defensor.getCertidaoNegativaMunicipal()))
                documentacaoExigida = documentacaoExigida + "[x] Certidões Negativas ";
            else
                documentacaoExigida = documentacaoExigida + "[ ] Certidões Negativas ";

            parameters.put("documentacaoExigida", documentacaoExigida);

            //VEÍCULO
            parameters.put("nomePermissionario", defensor.getNomeDefensor());
            parameters.put("numeroPermissao", permissao.getNumeroPermissao());
            parameters.put("placa", veiculo.getPlaca());
            parameters.put("marcaModelo", veiculo.getMarca() + "/" + veiculo.getModelo());
            parameters.put("anoFabricacao", veiculo.getAnoFabricacao());
            parameters.put("cor", veiculo.getCor().equals("1") ? "Branca" : "Prata");
            parameters.put("capacidade", veiculo.getCapacidade());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("gerarRegistroCondutorJasper: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }
}

