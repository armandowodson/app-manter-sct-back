package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PermissionarioRequestDTO;
import com.projeto.produto.dto.PermissionarioResponseDTO;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.repository.PermissionarioRepository;
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
        Permissionario permissionario = converterPermissionarioDTOToPermissionario(
                permissionarioRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto
        );
        permissionario.setDataCriacao(LocalDate.now());
        permissionario = permissionarioRepository.save(permissionario);
        return converterPermissionarioToPermissionarioDTO(permissionario);
    }

    @Transactional
    public PermissionarioResponseDTO atualizarPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                            MultipartFile certidaoNegativaCriminal,
                                                            MultipartFile certidaoNegativaMunicipal,
                                                            MultipartFile foto) throws IOException {
        Permissionario permissionario = converterPermissionarioDTOToPermissionario(
                permissionarioRequestDTO, certidaoNegativaCriminal, certidaoNegativaMunicipal, foto
        );
        return converterPermissionarioToPermissionarioDTO(permissionarioRepository.save(permissionario));
    }

    public List<PermissionarioResponseDTO> listarTodosPermissionarios() {
        List<Permissionario> listaPermissionario = permissionarioRepository.findAll(Sort.by(Sort.Direction.ASC, "descricaoPonto"));
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

    @Transactional
    public ResponseEntity<Void> excluirPermissionario(Long idPermissionario) {
        try{
            permissionarioRepository.deletePermissionarioByIdPermissionario(idPermissionario);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Ponto de Táxi!!!");
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
        permissionarioResponseDTO.setCnpjEmpresa(permissionario.getCnpjEmpresa());
        permissionarioResponseDTO.setRgPermissionario(permissionario.getRgPermissionario());
        permissionarioResponseDTO.setNaturezaPessoa(permissionario.getNaturezaPessoa());
        permissionarioResponseDTO.setCnhPermissionario(permissionario.getCnhPermissionario());
        permissionarioResponseDTO.setUfPermissionario(permissionario.getUfPermissionario());
        permissionarioResponseDTO.setBairroPermissionario(permissionario.getBairroPermissionario());
        permissionarioResponseDTO.setEnderecoPermissionario(permissionario.getEnderecoPermissionario());
        permissionarioResponseDTO.setCelularPermissionario(permissionario.getCelularPermissionario());
        permissionarioResponseDTO.setCertidaoNegativaCriminal(permissionario.getCertidaoNegativaCriminal());
        permissionarioResponseDTO.setCertidaoNegativaMunicipal(permissionario.getCertidaoNegativaMunicipal());
        permissionarioResponseDTO.setFoto(permissionario.getFoto());
        permissionarioResponseDTO.setDataCriacao(permissionario.getDataCriacao().toString());

        return permissionarioResponseDTO;
    }

    public Permissionario converterPermissionarioDTOToPermissionario(PermissionarioRequestDTO permissionarioRequestDTO,
                                                                     MultipartFile certidaoNegativaCriminal,
                                                                     MultipartFile certidaoNegativaMunicipal,
                                                                     MultipartFile foto) throws IOException {
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
        if(Objects.nonNull(permissionarioRequestDTO.getCnpjEmpresa()) && !permissionarioRequestDTO.getCnpjEmpresa().isEmpty()){
            permissionarioRequestDTO.setCnpjEmpresa(
                    permissionarioRequestDTO.getCnpjEmpresa().replace(".", "").replace("-", "").replace("/", "")
            );
            permissionario.setCnpjEmpresa(permissionarioRequestDTO.getCnpjEmpresa());
        }
        permissionario.setRgPermissionario(permissionarioRequestDTO.getRgPermissionario());
        permissionario.setNaturezaPessoa(permissionarioRequestDTO.getNaturezaPessoa());
        permissionario.setCnhPermissionario(permissionarioRequestDTO.getCnhPermissionario());
        permissionario.setUfPermissionario(permissionarioRequestDTO.getUfPermissionario());
        permissionario.setBairroPermissionario(permissionarioRequestDTO.getBairroPermissionario());
        permissionario.setEnderecoPermissionario(permissionarioRequestDTO.getEnderecoPermissionario());
        permissionario.setCelularPermissionario(permissionarioRequestDTO.getCelularPermissionario());
        permissionario.setCertidaoNegativaCriminal(certidaoNegativaCriminal.getBytes());
        permissionario.setCertidaoNegativaMunicipal(certidaoNegativaMunicipal.getBytes());
        permissionario.setFoto(foto.getBytes());
        if(Objects.nonNull(permissionarioRequestDTO.getDataCriacao()))
            permissionario.setDataCriacao(LocalDate.parse(permissionarioRequestDTO.getDataCriacao()));
        else
            permissionario.setDataCriacao(LocalDate.now());

        return  permissionario;
    }

}

