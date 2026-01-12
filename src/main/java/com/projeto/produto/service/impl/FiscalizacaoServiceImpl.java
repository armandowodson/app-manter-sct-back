package com.projeto.produto.service.impl;

import com.projeto.produto.dto.FiscalizacaoDTO;
import com.projeto.produto.dto.FiscalizacaoDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Defensor;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.DefensorRepository;
import com.projeto.produto.repository.FiscalizacaoRepository;
import com.projeto.produto.repository.VeiculoRepository;
import com.projeto.produto.utils.ValidaCNPJ;
import com.projeto.produto.utils.ValidaCPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FiscalizacaoServiceImpl {
    @Autowired
    private FiscalizacaoRepository fiscalizacaoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    /*@Transactional
    public FiscalizacaoDTO inserirDefensor(FiscalizacaoDTO fiscalizacaoDTO) throws IOException {
        
        if (Objects.isNull(fiscalizacaoDTO.getIdVeiculo()) || Objects.isNull(fiscalizacaoDTO.getIdPermissionario()) ||
                Objects.isNull(fiscalizacaoDTO.getDataFiscalizacao()) || Objects.isNull(fiscalizacaoDTO.getMotivoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getPrazoRegularizacao()) || Objects.isNull(fiscalizacaoDTO.getTipoInfracao()) ||
                Objects.isNull(fiscalizacaoDTO.getNumeroPermissao()) || Objects.isNull(fiscalizacaoDTO.getGrupoMultas()) ||
                Objects.isNull(fiscalizacaoDTO.getNaturezaInfracao())) {
            throw new RuntimeException("Dados inválidos para o Defensor!");
        }

        if(Objects.isNull(fiscalizacaoDTO.getUsuario()) || fiscalizacaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Defensor defensor = new Defensor();
        try {
            defensor = converterDefensorDTOToDefensor(
                    fiscalizacaoDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 1
            );
            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "INCLUSÃO", fiscalizacaoDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Defensor!");
        }

        return converterDefensorToDefensorDTO(defensor);
    }

    @Transactional
    public FiscalizacaoDTO atualizarDefensor(FiscalizacaoDTO fiscalizacaoDTO,
                                                             MultipartFile certidaoNegativaCriminal,
                                                             MultipartFile certidaoNegativaMunicipal,
                                                             MultipartFile foto) throws IOException {
        if (Objects.isNull(fiscalizacaoDTO.getNomeDefensor()) || Objects.isNull(fiscalizacaoDTO.getCpfDefensor()) ||
                Objects.isNull(fiscalizacaoDTO.getRgDefensor()) || Objects.isNull(fiscalizacaoDTO.getCnhDefensor()) ||
                Objects.isNull(fiscalizacaoDTO.getEnderecoDefensor()) || Objects.isNull(fiscalizacaoDTO.getCelularDefensor()) ||
                Objects.isNull(fiscalizacaoDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Defensor!");
        }

        if(Objects.isNull(fiscalizacaoDTO.getUsuario()) || fiscalizacaoDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Defensor defensor = new Defensor();
        try{
            defensor = converterDefensorDTOToDefensor(
                    fiscalizacaoDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 2
            );

            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "ALTERAÇÃO", fiscalizacaoDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível alterar os dados do Defensor!");
        }

        return converterDefensorToDefensorDTO(defensorRepository.save(defensor));
    }

    public Page<FiscalizacaoDTO> listarTodosDefensors(PageRequest pageRequest) {
        List<Defensor> defensorList = defensorRepository.buscarTodos(pageRequest);
        Integer countLista = defensorRepository.buscarTodos(null).size();
        List<FiscalizacaoDTO> defensorResponseDTOList = converterEntityToDTO(defensorList);
        return new PageImpl<>(defensorResponseDTOList, pageRequest, countLista);
    }

    public FiscalizacaoDTO buscarDefensorId(Long idDefensor) {
        Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
        FiscalizacaoDTO defensorResponseDTO = new FiscalizacaoDTO();
        if (defensor != null){
            defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
        }
        return defensorResponseDTO;
    }

    public Page<FiscalizacaoDTO> listarTodosDefensorFiltros(   String numeroPermissao, String nomeDefensor,
                                                                   String cpfDefensor, String cnpjEmpresa,
                                                                   String cnhDefensor, PageRequest pageRequest) {
        List<Defensor> listaDefensor = defensorRepository.listarTodosDefensorsFiltros(
                numeroPermissao,  nomeDefensor != null ? nomeDefensor.toUpperCase() : nomeDefensor,
                cpfDefensor, cnpjEmpresa, cnhDefensor, pageRequest
        );

        Integer countRegistros = defensorRepository.listarTodosDefensorsFiltros(
                numeroPermissao,  nomeDefensor != null ? nomeDefensor.toUpperCase() : nomeDefensor,
                cpfDefensor, cnpjEmpresa, cnhDefensor, null
        ).size();

        List<FiscalizacaoDTO> listaFiscalizacaoDTO = new ArrayList<>();
        if (!listaDefensor.isEmpty()){
            for (Defensor defensor : listaDefensor) {
                FiscalizacaoDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensor);
                listaFiscalizacaoDTO.add(defensorResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaFiscalizacaoDTO, pageRequest, countRegistros);
    }

    public List<FiscalizacaoDTO> listarDefensoresDisponiveis(Long idDefensor) {
        List<FiscalizacaoDTO> listaFiscalizacaoDTO = new ArrayList<>();
        List<Defensor> listaDefensor = defensorRepository.listarDefensorsDisponiveis();
        if(Objects.nonNull(idDefensor)){
            Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
            FiscalizacaoDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensor);
            listaFiscalizacaoDTO.add(defensorResponseDTORetornado);
        }

        if (!listaDefensor.isEmpty()){
            for (Defensor defensorDisponivel : listaDefensor) {
                FiscalizacaoDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensorDisponivel);
                listaFiscalizacaoDTO.add(defensorResponseDTORetornado);
            }
        }

        return listaFiscalizacaoDTO;
    }

    @Transactional
    public ResponseEntity<Void> excluirDefensor(Long idDefensor, String usuario) {
        String msgErro = "Erro ao Excluir o Defensor!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            defensorRepository.deleteDefensorByIdDefensor(idDefensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException(msgErro);
        }
    }

    public List<FiscalizacaoDTO> converterEntityToDTO(List<Defensor> listaDefensor){
        List<FiscalizacaoDTO> listaFiscalizacaoDTO = new ArrayList<>();
        for(Defensor defensor : listaDefensor){
            FiscalizacaoDTO defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
            listaFiscalizacaoDTO.add(defensorResponseDTO);
        }

        return listaFiscalizacaoDTO;
    }

    public FiscalizacaoDTO converterDefensorToDefensorDTO(Defensor defensor){
        FiscalizacaoDTO defensorResponseDTO = new FiscalizacaoDTO();
        if (defensor.getIdDefensor() != null){
            defensorResponseDTO.setIdDefensor(defensor.getIdDefensor());
        }

        defensorResponseDTO.setNumeroPermissao(defensor.getNumeroPermissao());
        defensorResponseDTO.setNomeDefensor(defensor.getNomeDefensor());
        defensorResponseDTO.setCpfDefensor(defensor.getCpfDefensor());
        defensorResponseDTO.setCnpjEmpresa((defensor.getCnpjEmpresa() != null && !defensor.getCnpjEmpresa().equals("null")) ? defensor.getCnpjEmpresa() : "");
        defensorResponseDTO.setRgDefensor(defensor.getRgDefensor());
        defensorResponseDTO.setOrgaoEmissor(defensor.getOrgaoEmissor());
        defensorResponseDTO.setNaturezaPessoa(defensor.getNaturezaPessoa().equals("1") ? "FÍSICA" : "JURÍDICA");
        defensorResponseDTO.setCnhDefensor(defensor.getCnhDefensor());
        defensorResponseDTO.setCategoriaCnhDefensor(converterIdCategoriaCnh(defensor.getCategoriaCnhDefensor()));
        defensorResponseDTO.setUfDefensor(defensor.getUfDefensor());
        defensorResponseDTO.setBairroDefensor(defensor.getBairroDefensor());
        defensorResponseDTO.setEnderecoDefensor(defensor.getEnderecoDefensor());
        defensorResponseDTO.setCelularDefensor(defensor.getCelularDefensor());
        defensorResponseDTO.setNumeroQuitacaoMilitar(defensor.getNumeroQuitacaoMilitar());
        defensorResponseDTO.setNumeroQuitacaoEleitoral(defensor.getNumeroQuitacaoEleitoral());
        defensorResponseDTO.setNumeroInscricaoInss(defensor.getNumeroInscricaoInss());
        defensorResponseDTO.setNumeroCertificadoCondutor(defensor.getNumeroCertificadoCondutor());
        defensorResponseDTO.setCertidaoNegativaCriminal(defensor.getCertidaoNegativaCriminal());
        defensorResponseDTO.setCertidaoNegativaMunicipal(defensor.getCertidaoNegativaMunicipal());
        defensorResponseDTO.setFoto(defensor.getFoto());
        defensorResponseDTO.setDataCriacao(defensor.getDataCriacao().toString());

        return defensorResponseDTO;
    }

    public Defensor converterDefensorDTOToDefensor(FiscalizacaoDTO fiscalizacaoDTO,
                                                                     MultipartFile certidaoNegativaCriminal,
                                                                     MultipartFile certidaoNegativaMunicipal,
                                                                     MultipartFile foto, Integer tipo) throws IOException {
        Defensor defensor = new Defensor();
        if (fiscalizacaoDTO.getIdDefensor() != null && fiscalizacaoDTO.getIdDefensor() != 0){
            defensor = defensorRepository.findDefensorByIdDefensor(fiscalizacaoDTO.getIdDefensor());
        }

        defensor.setNumeroPermissao(fiscalizacaoDTO.getNumeroPermissao());
        defensor.setNomeDefensor(fiscalizacaoDTO.getNomeDefensor());

        if(Objects.nonNull(fiscalizacaoDTO.getCpfDefensor()) && !fiscalizacaoDTO.getCpfDefensor().isEmpty()){
            fiscalizacaoDTO.setCpfDefensor(
                    fiscalizacaoDTO.getCpfDefensor().replace(".", "").replace("-", "").replace("/", "")
            );
            defensor.setCpfDefensor(fiscalizacaoDTO.getCpfDefensor());
        }

        if(Objects.nonNull(fiscalizacaoDTO.getCnpjEmpresa()) && !fiscalizacaoDTO.getCnpjEmpresa().isEmpty() &&
                !fiscalizacaoDTO.getCnpjEmpresa().equals("null")){
            fiscalizacaoDTO.setCnpjEmpresa(
                    fiscalizacaoDTO.getCnpjEmpresa().replace(".", "").replace("-", "").replace("/", "")
            );
            defensor.setCnpjEmpresa(fiscalizacaoDTO.getCnpjEmpresa());
        }

        defensor.setRgDefensor(fiscalizacaoDTO.getRgDefensor());
        defensor.setOrgaoEmissor(fiscalizacaoDTO.getOrgaoEmissor());

        if(tipo == 1){
            defensor.setNaturezaPessoa(fiscalizacaoDTO.getNaturezaPessoa());
        }else{
            defensor.setNaturezaPessoa(fiscalizacaoDTO.getNaturezaPessoa().equals("FÍSICA") ? "1" : "2");
        }

        defensor.setUfDefensor(fiscalizacaoDTO.getUfDefensor());
        defensor.setBairroDefensor(fiscalizacaoDTO.getBairroDefensor());
        defensor.setEnderecoDefensor(fiscalizacaoDTO.getEnderecoDefensor());
        defensor.setCelularDefensor(fiscalizacaoDTO.getCelularDefensor());
        defensor.setCnhDefensor(fiscalizacaoDTO.getCnhDefensor());

        if(tipo == 1){
            defensor.setCategoriaCnhDefensor(fiscalizacaoDTO.getCategoriaCnhDefensor());
        }else{
            defensor.setCategoriaCnhDefensor(converterNomeCategoriaCnh(fiscalizacaoDTO.getCategoriaCnhDefensor()));
        }

        defensor.setNumeroQuitacaoMilitar(fiscalizacaoDTO.getNumeroQuitacaoMilitar());
        defensor.setNumeroQuitacaoEleitoral(fiscalizacaoDTO.getNumeroQuitacaoEleitoral());
        defensor.setNumeroCertificadoCondutor(fiscalizacaoDTO.getNumeroCertificadoCondutor());
        defensor.setNumeroInscricaoInss(fiscalizacaoDTO.getNumeroInscricaoInss());

        if(Objects.nonNull(certidaoNegativaCriminal))
            defensor.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
        if(Objects.nonNull(certidaoNegativaMunicipal))
            defensor.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
        if(Objects.nonNull(foto))
            defensor.setFoto(foto.getBytes());

        if(Objects.nonNull(fiscalizacaoDTO.getDataCriacao()) && !fiscalizacaoDTO.getDataCriacao().isEmpty())
            defensor.setDataCriacao(LocalDate.parse(fiscalizacaoDTO.getDataCriacao()));
        else
            defensor.setDataCriacao(LocalDate.now());

        return  defensor;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }

    public String converterIdCategoriaCnh(String categoria){
        switch (categoria){
            case "1":
                return "B";
            case "2":
                return "C";
            case "3":
                return "D";
            case "4":
                return "E";
        }

        return "";
    }

    public String converterNomeCategoriaCnh(String categoria){
        switch (categoria){
            case "B":
                return "1";
            case "C":
                return "2";
            case "D":
                return "3";
            case "E":
                return "4";
        }

        return "";
    }*/

}

