package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PermissionarioRepository;
import com.projeto.produto.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PermissionarioServiceImpl {
    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public PermissionarioResponseDTO inserirPermissionario(    PermissionarioRequestDTO permissionarioRequestDTO,
                                                               MultipartFile certidaoNegativaCriminal,
                                                               MultipartFile certidaoNegativaMunicipal,
                                                               MultipartFile foto) throws IOException {
        if (Objects.isNull(permissionarioRequestDTO.getNomePermissionario()) || Objects.isNull(permissionarioRequestDTO.getCpfPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getRgPermissionario()) || Objects.isNull(permissionarioRequestDTO.getCnhPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getEnderecoPermissionario()) || Objects.isNull(permissionarioRequestDTO.getCelularPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Permissionário/Proprietário!");
        }
        if(Objects.isNull(permissionarioRequestDTO.getUsuario()) || permissionarioRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Permissionario permissionario = converterPermissionarioDTOToPermissionario(
                permissionarioRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 1
        );
        permissionario.setDataCriacao(LocalDate.now());
        permissionario = permissionarioRepository.save(permissionario);

        //Auditoria
        salvarAuditoria("PERMISSIONÁRIO TÁXI", "INCLUSÃO", permissionarioRequestDTO.getUsuario());

        return converterPermissionarioToPermissionarioDTO(permissionario);
    }

    @Transactional
    public PermissionarioResponseDTO atualizarPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                             MultipartFile certidaoNegativaCriminal,
                                                             MultipartFile certidaoNegativaMunicipal,
                                                             MultipartFile foto) throws IOException {
        if (Objects.isNull(permissionarioRequestDTO.getNomePermissionario()) || Objects.isNull(permissionarioRequestDTO.getCpfPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getRgPermissionario()) || Objects.isNull(permissionarioRequestDTO.getCnhPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getEnderecoPermissionario()) || Objects.isNull(permissionarioRequestDTO.getCelularPermissionario()) ||
                Objects.isNull(permissionarioRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Permissionário/Proprietário!");
        }
        if(Objects.isNull(permissionarioRequestDTO.getUsuario()) || permissionarioRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Permissionario permissionario = converterPermissionarioDTOToPermissionario(
                permissionarioRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto, 2
        );

        permissionario = permissionarioRepository.save(permissionario);

        //Auditoria
        salvarAuditoria("PERMISSIONÁRIO TÁXI", "ALTERAÇÃO", permissionarioRequestDTO.getUsuario());

        return converterPermissionarioToPermissionarioDTO(permissionarioRepository.save(permissionario));
    }

    public List<PermissionarioResponseDTO> listarTodosPermissionarios() {
        List<Permissionario> listaPermissionario = permissionarioRepository.findAll(Sort.by(Sort.Direction.ASC, "nomePermissionario"));
        return converterEntityToDTO(listaPermissionario);
    }

    public PermissionarioResponseDTO buscarPermissionarioId(Long idPermissionario) {
        Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
        PermissionarioResponseDTO permissionarioResponseDTO = new PermissionarioResponseDTO();
        if (permissionario != null){
            permissionarioResponseDTO = converterPermissionarioToPermissionarioDTO(permissionario);
        }
        return permissionarioResponseDTO;
    }

    public List<PermissionarioResponseDTO> listarTodosPermissionarioFiltros(String numeroPermissao, String nomePermissionario,
                                                                           String cpfPermissionario, String cnpjEmpresa,
                                                                           String cnhPermissionario) {
        List<Permissionario> listaPermissionario = permissionarioRepository.listarTodosPermissionariosFiltros(
                numeroPermissao,
                nomePermissionario != null ? nomePermissionario.toUpperCase() : nomePermissionario,
                cpfPermissionario,
                cnpjEmpresa,
                cnhPermissionario
        );

        List<PermissionarioResponseDTO> listaPermissionarioResponseDTO = new ArrayList<>();
        if (!listaPermissionario.isEmpty()){
            for (Permissionario permissionario : listaPermissionario) {
                PermissionarioResponseDTO permissionarioResponseDTORetornado = converterPermissionarioToPermissionarioDTO(permissionario);
                listaPermissionarioResponseDTO.add(permissionarioResponseDTORetornado);
            }
        }

        return listaPermissionarioResponseDTO;
    }

    public List<PermissionarioResponseDTO> listarPermissionariosDisponiveis(Long idPermissionario) {
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
    }

    @Transactional
    public ResponseEntity<Void> excluirPermissionario(Long idPermissionario, String usuario) {
        String msgErro = "Erro ao Excluir o Permissionário!!";
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            Permissionario permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(idPermissionario);
            Veiculo veiculo = veiculoRepository.findVeiculoByPermissionario(permissionario);
            if(Objects.nonNull(veiculo)){
                msgErro = "Existe um Veículo associado a este Permissionário";
                throw new Exception();
            }
            permissionarioRepository.deletePermissionarioByIdPermissionario(idPermissionario);

            //Auditoria
            salvarAuditoria("PERMISSIONÁRIO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
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
        permissionarioResponseDTO.setCnpjEmpresa((permissionario.getCnpjEmpresa() != null && !permissionario.getCnpjEmpresa().equals("null")) ? permissionario.getCnpjEmpresa() : "");
        permissionarioResponseDTO.setRgPermissionario(permissionario.getRgPermissionario());
        permissionarioResponseDTO.setOrgaoEmissor(permissionario.getOrgaoEmissor());
        permissionarioResponseDTO.setNaturezaPessoa(permissionario.getNaturezaPessoa().equals("1") ? "FÍSICA" : "JURÍDICA");
        permissionarioResponseDTO.setCnhPermissionario(permissionario.getCnhPermissionario());
        permissionarioResponseDTO.setCategoriaCnhPermissionario(converterIdCategoriaCnh(permissionario.getCategoriaCnhPermissionario()));
        permissionarioResponseDTO.setUfPermissionario(permissionario.getUfPermissionario());
        permissionarioResponseDTO.setBairroPermissionario(permissionario.getBairroPermissionario());
        permissionarioResponseDTO.setEnderecoPermissionario(permissionario.getEnderecoPermissionario());
        permissionarioResponseDTO.setCelularPermissionario(permissionario.getCelularPermissionario());
        permissionarioResponseDTO.setNumeroQuitacaoMilitar(permissionario.getNumeroQuitacaoMilitar());
        permissionarioResponseDTO.setNumeroQuitacaoEleitoral(permissionario.getNumeroQuitacaoEleitoral());
        permissionarioResponseDTO.setNumeroInscricaoInss(permissionario.getNumeroInscricaoInss());
        permissionarioResponseDTO.setNumeroCertificadoCondutor(permissionario.getNumeroCertificadoCondutor());
        permissionarioResponseDTO.setCertidaoNegativaCriminal(permissionario.getCertidaoNegativaCriminal());
        permissionarioResponseDTO.setCertidaoNegativaMunicipal(permissionario.getCertidaoNegativaMunicipal());
        permissionarioResponseDTO.setFoto(permissionario.getFoto());
        permissionarioResponseDTO.setDataCriacao(permissionario.getDataCriacao().toString());

        return permissionarioResponseDTO;
    }

    public Permissionario converterPermissionarioDTOToPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
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

        if(Objects.nonNull(permissionarioRequestDTO.getCnpjEmpresa()) && !permissionarioRequestDTO.getCnpjEmpresa().isEmpty() &&
                !permissionarioRequestDTO.getCnpjEmpresa().equals("null")){
            permissionarioRequestDTO.setCnpjEmpresa(
                    permissionarioRequestDTO.getCnpjEmpresa().replace(".", "").replace("-", "").replace("/", "")
            );
            permissionario.setCnpjEmpresa(permissionarioRequestDTO.getCnpjEmpresa());
        }

        permissionario.setRgPermissionario(permissionarioRequestDTO.getRgPermissionario());
        permissionario.setOrgaoEmissor(permissionarioRequestDTO.getOrgaoEmissor());
        permissionario.setNaturezaPessoa(permissionarioRequestDTO.getNaturezaPessoa().equals("FÍSICA") ? "1" : "2");
        permissionario.setUfPermissionario(permissionarioRequestDTO.getUfPermissionario());
        permissionario.setBairroPermissionario(permissionarioRequestDTO.getBairroPermissionario());
        permissionario.setEnderecoPermissionario(permissionarioRequestDTO.getEnderecoPermissionario());
        permissionario.setCelularPermissionario(permissionarioRequestDTO.getCelularPermissionario());
        permissionario.setCnhPermissionario(permissionarioRequestDTO.getCnhPermissionario());

        if(tipo == 1){
            permissionario.setCategoriaCnhPermissionario(converterIdCategoriaCnh(permissionarioRequestDTO.getCategoriaCnhPermissionario()));
        }else{
            permissionario.setCategoriaCnhPermissionario(converterNomeCategoriaCnh(permissionarioRequestDTO.getCategoriaCnhPermissionario()));
        }

        permissionario.setNumeroQuitacaoMilitar(permissionarioRequestDTO.getNumeroQuitacaoMilitar());
        permissionario.setNumeroQuitacaoEleitoral(permissionarioRequestDTO.getNumeroQuitacaoEleitoral());
        permissionario.setNumeroCertificadoCondutor(permissionarioRequestDTO.getNumeroCertificadoCondutor());
        permissionario.setNumeroInscricaoInss(permissionarioRequestDTO.getNumeroInscricaoInss());

        if(Objects.nonNull(certidaoNegativaCriminal))
            permissionario.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
        if(Objects.nonNull(certidaoNegativaMunicipal))
            permissionario.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
        if(Objects.nonNull(foto))
            permissionario.setFoto(foto.getBytes());

        if(Objects.nonNull(permissionarioRequestDTO.getDataCriacao()))
            permissionario.setDataCriacao(LocalDate.parse(permissionarioRequestDTO.getDataCriacao()));
        else
            permissionario.setDataCriacao(LocalDate.now());

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

