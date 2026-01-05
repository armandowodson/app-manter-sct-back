package com.projeto.produto.service.impl;

import com.projeto.produto.dto.DefensorRequestDTO;
import com.projeto.produto.dto.DefensorResponseDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Defensor;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.DefensorRepository;
import com.projeto.produto.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class DefensorServiceImpl {
    @Autowired
    private DefensorRepository defensorRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public DefensorResponseDTO inserirDefensor(    DefensorRequestDTO defensorRequestDTO,
                                                               MultipartFile certidaoNegativaCriminal,
                                                               MultipartFile certidaoNegativaMunicipal,
                                                               MultipartFile foto) throws IOException {
        if (Objects.isNull(defensorRequestDTO.getNomeDefensor()) || Objects.isNull(defensorRequestDTO.getCpfDefensor()) ||
                Objects.isNull(defensorRequestDTO.getRgDefensor()) || Objects.isNull(defensorRequestDTO.getCnhDefensor()) ||
                Objects.isNull(defensorRequestDTO.getEnderecoDefensor()) || Objects.isNull(defensorRequestDTO.getCelularDefensor()) ||
                Objects.isNull(defensorRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Defensor!");
        }
        if(Objects.isNull(defensorRequestDTO.getUsuario()) || defensorRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Defensor defensor = new Defensor();
        try {
            defensor = converterDefensorDTOToDefensor(
                    defensorRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 1
            );
            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "INCLUSÃO", defensorRequestDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Defensor!");
        }

        return converterDefensorToDefensorDTO(defensor);
    }

    @Transactional
    public DefensorResponseDTO atualizarDefensor(DefensorRequestDTO defensorRequestDTO,
                                                             MultipartFile certidaoNegativaCriminal,
                                                             MultipartFile certidaoNegativaMunicipal,
                                                             MultipartFile foto) throws IOException {
        if (Objects.isNull(defensorRequestDTO.getNomeDefensor()) || Objects.isNull(defensorRequestDTO.getCpfDefensor()) ||
                Objects.isNull(defensorRequestDTO.getRgDefensor()) || Objects.isNull(defensorRequestDTO.getCnhDefensor()) ||
                Objects.isNull(defensorRequestDTO.getEnderecoDefensor()) || Objects.isNull(defensorRequestDTO.getCelularDefensor()) ||
                Objects.isNull(defensorRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Defensor!");
        }
        if(Objects.isNull(defensorRequestDTO.getUsuario()) || defensorRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Defensor defensor = new Defensor();
        try{
            defensor = converterDefensorDTOToDefensor(
                    defensorRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 2
            );

            defensor = defensorRepository.save(defensor);

            //Auditoria
            salvarAuditoria("DEFENSOR TÁXI", "ALTERAÇÃO", defensorRequestDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível alterar os dados do Defensor!");
        }

        return converterDefensorToDefensorDTO(defensorRepository.save(defensor));
    }

    public Page<DefensorResponseDTO> listarTodosDefensors(PageRequest pageRequest) {
        List<Defensor> defensorList = defensorRepository.buscarTodos(pageRequest);
        Integer countLista = defensorRepository.buscarTodos(null).size();
        List<DefensorResponseDTO> defensorResponseDTOList = converterEntityToDTO(defensorList);
        return new PageImpl<>(defensorResponseDTOList, pageRequest, countLista);
    }

    public DefensorResponseDTO buscarDefensorId(Long idDefensor) {
        Defensor defensor = defensorRepository.findDefensorByIdDefensor(idDefensor);
        DefensorResponseDTO defensorResponseDTO = new DefensorResponseDTO();
        if (defensor != null){
            defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
        }
        return defensorResponseDTO;
    }

    public Page<DefensorResponseDTO> listarTodosDefensorFiltros(   String numeroPermissao, String nomeDefensor,
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

        List<DefensorResponseDTO> listaDefensorResponseDTO = new ArrayList<>();
        if (!listaDefensor.isEmpty()){
            for (Defensor defensor : listaDefensor) {
                DefensorResponseDTO defensorResponseDTORetornado = converterDefensorToDefensorDTO(defensor);
                listaDefensorResponseDTO.add(defensorResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaDefensorResponseDTO, pageRequest, countRegistros);
    }

    public List<DefensorResponseDTO> listarDefensoresDisponiveis(Long idDefensor) {
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

    public List<DefensorResponseDTO> converterEntityToDTO(List<Defensor> listaDefensor){
        List<DefensorResponseDTO> listaDefensorResponseDTO = new ArrayList<>();
        for(Defensor defensor : listaDefensor){
            DefensorResponseDTO defensorResponseDTO = converterDefensorToDefensorDTO(defensor);
            listaDefensorResponseDTO.add(defensorResponseDTO);
        }

        return listaDefensorResponseDTO;
    }

    public DefensorResponseDTO converterDefensorToDefensorDTO(Defensor defensor){
        DefensorResponseDTO defensorResponseDTO = new DefensorResponseDTO();
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

    public Defensor converterDefensorDTOToDefensor(DefensorRequestDTO defensorRequestDTO,
                                                                     MultipartFile certidaoNegativaCriminal,
                                                                     MultipartFile certidaoNegativaMunicipal,
                                                                     MultipartFile foto, Integer tipo) throws IOException {
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

        if(Objects.nonNull(defensorRequestDTO.getCnpjEmpresa()) && !defensorRequestDTO.getCnpjEmpresa().isEmpty() &&
                !defensorRequestDTO.getCnpjEmpresa().equals("null")){
            defensorRequestDTO.setCnpjEmpresa(
                    defensorRequestDTO.getCnpjEmpresa().replace(".", "").replace("-", "").replace("/", "")
            );
            defensor.setCnpjEmpresa(defensorRequestDTO.getCnpjEmpresa());
        }

        defensor.setRgDefensor(defensorRequestDTO.getRgDefensor());
        defensor.setOrgaoEmissor(defensorRequestDTO.getOrgaoEmissor());

        if(tipo == 1){
            defensor.setNaturezaPessoa(defensorRequestDTO.getNaturezaPessoa());
        }else{
            defensor.setNaturezaPessoa(defensorRequestDTO.getNaturezaPessoa().equals("FÍSICA") ? "1" : "2");
        }

        defensor.setUfDefensor(defensorRequestDTO.getUfDefensor());
        defensor.setBairroDefensor(defensorRequestDTO.getBairroDefensor());
        defensor.setEnderecoDefensor(defensorRequestDTO.getEnderecoDefensor());
        defensor.setCelularDefensor(defensorRequestDTO.getCelularDefensor());
        defensor.setCnhDefensor(defensorRequestDTO.getCnhDefensor());

        if(tipo == 1){
            defensor.setCategoriaCnhDefensor(defensorRequestDTO.getCategoriaCnhDefensor());
        }else{
            defensor.setCategoriaCnhDefensor(converterNomeCategoriaCnh(defensorRequestDTO.getCategoriaCnhDefensor()));
        }

        defensor.setNumeroQuitacaoMilitar(defensorRequestDTO.getNumeroQuitacaoMilitar());
        defensor.setNumeroQuitacaoEleitoral(defensorRequestDTO.getNumeroQuitacaoEleitoral());
        defensor.setNumeroCertificadoCondutor(defensorRequestDTO.getNumeroCertificadoCondutor());
        defensor.setNumeroInscricaoInss(defensorRequestDTO.getNumeroInscricaoInss());

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
    }

}

