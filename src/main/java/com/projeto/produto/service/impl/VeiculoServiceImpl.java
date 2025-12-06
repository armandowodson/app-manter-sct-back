package com.projeto.produto.service.impl;

import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.Veiculo;
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
public class VeiculoServiceImpl {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public VeiculoResponseDTO inserirVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                             MultipartFile crlv,
                                             MultipartFile comprovanteVistoria) throws IOException {
        if (Objects.isNull(veiculoRequestDTO.getMarca()) || Objects.isNull(veiculoRequestDTO.getModelo()) ||
                Objects.isNull(veiculoRequestDTO.getModelo()) || Objects.isNull(veiculoRequestDTO.getAnoModelo()) ||
                Objects.isNull(veiculoRequestDTO.getCor()) || Objects.isNull(veiculoRequestDTO.getPlaca()) ||
                Objects.isNull(veiculoRequestDTO.getChassi()) || Objects.isNull(veiculoRequestDTO.getRenavam()) ||
                Objects.isNull(crlv) || Objects.isNull(comprovanteVistoria) ||
                Objects.isNull(veiculoRequestDTO.getIdPermissionario()) || Objects.isNull(veiculoRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Veículo!");
        }
        Veiculo veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria);
        veiculo.setDataCriacao(LocalDate.now());
        veiculo = veiculoRepository.save(veiculo);
        return converterVeiculoToVeiculoDTO(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizarVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                               MultipartFile crlv,
                                               MultipartFile comprovanteVistoria) throws IOException {
        if (Objects.isNull(veiculoRequestDTO.getMarca()) || Objects.isNull(veiculoRequestDTO.getModelo()) ||
                Objects.isNull(veiculoRequestDTO.getModelo()) || Objects.isNull(veiculoRequestDTO.getAnoModelo()) ||
                Objects.isNull(veiculoRequestDTO.getCor()) || Objects.isNull(veiculoRequestDTO.getPlaca()) ||
                Objects.isNull(veiculoRequestDTO.getChassi()) || Objects.isNull(veiculoRequestDTO.getRenavam()) ||
                Objects.isNull(crlv) || Objects.isNull(comprovanteVistoria) ||
                Objects.isNull(veiculoRequestDTO.getIdPermissionario()) || Objects.isNull(veiculoRequestDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Veículo!");
        }
        Veiculo veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria);
        return converterVeiculoToVeiculoDTO(veiculoRepository.save(veiculo));
    }

    public List<VeiculoResponseDTO> listarTodosVeiculos() {
        List<Veiculo> listaVeiculos = veiculoRepository.findAll(Sort.by(Sort.Direction.ASC, "placa"));
        return converterEntityToDTO(listaVeiculos);
    }

    public VeiculoResponseDTO buscarVeiculoId(Long idVeiculo) {
        Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
        VeiculoResponseDTO veiculoResponseDTO = new VeiculoResponseDTO();
        if (veiculo != null){
            veiculoResponseDTO = converterVeiculoToVeiculoDTO(veiculo);
        }
        return veiculoResponseDTO;
    }

    public List<VeiculoResponseDTO> listarTodosVeiculosFiltros(String numeroPermissao, String placa,
                                                               String renavam, String numeroTaximetro,
                                                               String anoFabricacao) {
        List<Veiculo> listaVeiculos = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao
        );

        List<VeiculoResponseDTO> listaVeiculoResponseDTO = new ArrayList<>();
        if (!listaVeiculos.isEmpty()){
            for (Veiculo veiculo : listaVeiculos) {
                VeiculoResponseDTO veiculoResponseDTORetornado = converterVeiculoToVeiculoDTO(veiculo);
                listaVeiculoResponseDTO.add(veiculoResponseDTORetornado);
            }
        }

        return listaVeiculoResponseDTO;
    }

    @Transactional
    public ResponseEntity<Void> excluirVeiculo(Long idVeiculo) {
        try{
            veiculoRepository.deleteVeiculoByIdVeiculo(idVeiculo);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Veículo!!!");
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

        veiculoResponseDTO.setIdPermissionario(veiculo.getIdPermissionario());
        veiculoResponseDTO.setNumeroPermissao(veiculo.getNumeroPermissao());
        veiculoResponseDTO.setPlaca(veiculo.getPlaca());
        veiculoResponseDTO.setRenavam(veiculo.getRenavam());
        veiculoResponseDTO.setChassi(veiculo.getChassi());
        veiculoResponseDTO.setAnoFabricacao(veiculo.getAnoFabricacao());
        veiculoResponseDTO.setMarca(veiculo.getMarca());
        veiculoResponseDTO.setModelo(veiculo.getModelo());
        veiculoResponseDTO.setAnoModelo(veiculo.getAnoModelo());
        veiculoResponseDTO.setCor(veiculo.getCor());
        veiculoResponseDTO.setCombustivel(veiculo.getCombustivel());
        veiculoResponseDTO.setCrlv(veiculo.getCrlv());
        veiculoResponseDTO.setNumeroTaximetro(veiculo.getNumeroTaximetro());
        veiculoResponseDTO.setAnoRenovacao(veiculo.getAnoRenovacao());
        veiculoResponseDTO.setDataVistoria(veiculo.getDataVistoria().toString());
        veiculoResponseDTO.setDataRetorno(veiculo.getDataRetorno().toString());
        veiculoResponseDTO.setComprovanteVistoria(veiculo.getComprovanteVistoria());
        veiculoResponseDTO.setSituacaoVeiculo(veiculo.getSituacaoVeiculo());
        veiculoResponseDTO.setDataMidiaTaxi(veiculo.getDataMidiaTaxi().toString());
        veiculoResponseDTO.setEmpresaMidiaTaxi(veiculo.getEmpresaMidiaTaxi());
        veiculoResponseDTO.setDataCriacao(veiculo.getDataCriacao().toString());

        return veiculoResponseDTO;
    }

    public Veiculo converterVeiculoDTOToVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                                MultipartFile crlv,
                                                MultipartFile comprovanteVistoria) throws IOException {
        Veiculo veiculo = new Veiculo();
        if (veiculoRequestDTO.getIdVeiculo() != null && veiculoRequestDTO.getIdVeiculo() != 0){
            veiculo = veiculoRepository.findVeiculoByIdVeiculo(veiculoRequestDTO.getIdVeiculo());
        }

        veiculo.setIdPermissionario(veiculoRequestDTO.getIdPermissionario());
        veiculo.setNumeroPermissao(veiculoRequestDTO.getNumeroPermissao());
        veiculo.setPlaca(veiculoRequestDTO.getPlaca());
        veiculo.setRenavam(veiculoRequestDTO.getRenavam());
        veiculo.setChassi(veiculoRequestDTO.getChassi());
        veiculo.setAnoFabricacao(veiculoRequestDTO.getAnoFabricacao());
        veiculo.setMarca(veiculoRequestDTO.getMarca());
        veiculo.setModelo(veiculoRequestDTO.getModelo());
        veiculo.setAnoModelo(veiculoRequestDTO.getAnoModelo());
        veiculo.setCor(veiculoRequestDTO.getCor());
        veiculo.setCombustivel(veiculoRequestDTO.getCombustivel());
        veiculo.setCrlv(crlv.getBytes());
        veiculo.setNumeroTaximetro(veiculoRequestDTO.getNumeroTaximetro());
        veiculo.setAnoRenovacao(veiculoRequestDTO.getAnoRenovacao());
        veiculo.setDataVistoria(LocalDate.parse(veiculoRequestDTO.getDataVistoria()));
        veiculo.setDataRetorno(LocalDate.parse(veiculoRequestDTO.getDataRetorno()));
        veiculo.setComprovanteVistoria(comprovanteVistoria.getBytes());
        veiculo.setSituacaoVeiculo(veiculoRequestDTO.getSituacaoVeiculo());
        veiculo.setDataMidiaTaxi(LocalDate.parse(veiculoRequestDTO.getDataMidiaTaxi()));
        veiculo.setEmpresaMidiaTaxi(veiculoRequestDTO.getEmpresaMidiaTaxi());
        if(Objects.nonNull(veiculoRequestDTO.getDataCriacao()))
            veiculo.setDataCriacao(LocalDate.parse(veiculoRequestDTO.getDataCriacao()));
        else
            veiculo.setDataCriacao(LocalDate.now());

        return  veiculo;
    }

}
