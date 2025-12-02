package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.dto.VeiculoDTO;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.PontosTaxiRepository;
import com.projeto.produto.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class VeiculoServiceImpl {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public VeiculoDTO inserirVeiculo(VeiculoDTO veiculoDTO) {
        if (Objects.isNull(veiculoDTO.getMarca()) || Objects.isNull(veiculoDTO.getModelo()) ||
                Objects.isNull(veiculoDTO.getModelo()) || Objects.isNull(veiculoDTO.getAnoModelo()) ||
                Objects.isNull(veiculoDTO.getCor()) || Objects.isNull(veiculoDTO.getPlaca()) ||
                Objects.isNull(veiculoDTO.getChassi()) || Objects.isNull(veiculoDTO.getRenavam()) ||
                Objects.isNull(veiculoDTO.getCrlv()) || Objects.isNull(veiculoDTO.getComprovanteVistoria()) ||
                Objects.isNull(veiculoDTO.getIdPermissionario()) || Objects.isNull(veiculoDTO.getNumeroPermissao())) {
            throw new RuntimeException("Dados inválidos para o Veículo!");
        }
        Veiculo veiculo = converterVeiculoDTOToVeiculo(veiculoDTO);
        veiculo.setDataCriacao(LocalDate.now());
        veiculo = veiculoRepository.save(veiculo);
        return converterVeiculoToVeiculoDTO(veiculo);
    }

    @Transactional
    public VeiculoDTO atualizarVeiculo(VeiculoDTO veiculoDTO) {
        Veiculo veiculo = converterVeiculoDTOToVeiculo(veiculoDTO);
        return converterVeiculoToVeiculoDTO(veiculoRepository.save(veiculo));
    }

    public List<VeiculoDTO> listarTodosVeiculos() {
        List<Veiculo> listaVeiculos = veiculoRepository.findAll(Sort.by(Sort.Direction.ASC, "placa"));
        return converterEntityToDTO(listaVeiculos);
    }

    public VeiculoDTO buscarVeiculoId(Long idVeiculo) {
        Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
        VeiculoDTO veiculoDTO = new VeiculoDTO();
        if (veiculo != null){
            veiculoDTO = converterVeiculoToVeiculoDTO(veiculo);
        }
        return veiculoDTO;
    }

    public List<VeiculoDTO> listarTodosVeiculosFiltros(String numeroPermissao, String placa,
                                                       String renavam, String numeroTaximetro,
                                                       String anoFabricacao) {
        List<Veiculo> listaVeiculos = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao
        );

        List<VeiculoDTO> listaVeiculoDTO = new ArrayList<>();
        if (!listaVeiculos.isEmpty()){
            for (Veiculo veiculo : listaVeiculos) {
                VeiculoDTO veiculoDTORetornado = converterVeiculoToVeiculoDTO(veiculo);
                listaVeiculoDTO.add(veiculoDTORetornado);
            }
        }

        return listaVeiculoDTO;
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

    public List<VeiculoDTO> converterEntityToDTO(List<Veiculo> listaVeiculos){
        List<VeiculoDTO> listaVeiculosDTO = new ArrayList<>();
        for(Veiculo veiculo : listaVeiculos){
            VeiculoDTO veiculoDTO = converterVeiculoToVeiculoDTO(veiculo);
            listaVeiculosDTO.add(veiculoDTO);
        }

        return  listaVeiculosDTO;
    }

    public VeiculoDTO converterVeiculoToVeiculoDTO(Veiculo veiculo){
        VeiculoDTO veiculoDTO = new VeiculoDTO();
        if (veiculo.getIdVeiculo() != null){
            veiculoDTO.setIdVeiculo(veiculo.getIdVeiculo());
        }

        veiculoDTO.setIdPermissionario(veiculo.getIdPermissionario());
        veiculoDTO.setNumeroPermissao(veiculo.getNumeroPermissao());
        veiculoDTO.setPlaca(veiculo.getPlaca());
        veiculoDTO.setRenavam(veiculo.getRenavam());
        veiculoDTO.setChassi(veiculo.getChassi());
        veiculoDTO.setAnoFabricacao(veiculo.getAnoFabricacao());
        veiculoDTO.setMarca(veiculo.getMarca());
        veiculoDTO.setModelo(veiculo.getModelo());
        veiculoDTO.setAnoModelo(veiculo.getAnoModelo());
        veiculoDTO.setCor(veiculo.getCor());
        veiculoDTO.setCombustivel(veiculo.getCombustivel());
        veiculoDTO.setCrlv(veiculo.getCrlv());
        veiculoDTO.setNumeroTaximetro(veiculo.getNumeroTaximetro());
        veiculoDTO.setAnoRenovacao(veiculo.getAnoRenovacao());
        veiculoDTO.setDataVistoria(veiculo.getDataVistoria().toString());
        veiculoDTO.setDataRetorno(veiculo.getDataRetorno().toString());
        veiculoDTO.setComprovanteVistoria(veiculo.getComprovanteVistoria());
        veiculoDTO.setSituacaoVeiculo(veiculo.getSituacaoVeiculo());
        veiculoDTO.setDataMidiaTaxi(veiculo.getDataMidiaTaxi().toString());
        veiculoDTO.setEmpresaMidiaTaxi(veiculo.getEmpresaMidiaTaxi());
        veiculoDTO.setDataCriacao(veiculo.getDataCriacao().toString());

        return  veiculoDTO;
    }

    public Veiculo converterVeiculoDTOToVeiculo(VeiculoDTO veiculoDTO){
        Veiculo veiculo = new Veiculo();
        if (veiculoDTO.getIdVeiculo() != null && veiculoDTO.getIdVeiculo() != 0){
            veiculo = veiculoRepository.findVeiculoByIdVeiculo(veiculoDTO.getIdVeiculo());
        }

        veiculo.setNumeroPermissao(veiculoDTO.getNumeroPermissao());
        veiculo.setPlaca(veiculoDTO.getPlaca());
        veiculo.setRenavam(veiculoDTO.getRenavam());
        veiculo.setChassi(veiculoDTO.getChassi());
        veiculo.setAnoFabricacao(veiculoDTO.getAnoFabricacao());
        veiculo.setMarca(veiculoDTO.getMarca());
        veiculo.setModelo(veiculoDTO.getModelo());
        veiculo.setAnoModelo(veiculoDTO.getAnoModelo());
        veiculo.setCor(veiculoDTO.getCor());
        veiculo.setCombustivel(veiculoDTO.getCombustivel());
        veiculo.setCrlv(veiculoDTO.getCrlv());
        veiculo.setNumeroTaximetro(veiculoDTO.getNumeroTaximetro());
        veiculo.setAnoRenovacao(veiculoDTO.getAnoRenovacao());
        veiculo.setDataVistoria(LocalDate.parse(veiculoDTO.getDataVistoria()));
        veiculo.setDataRetorno(LocalDate.parse(veiculoDTO.getDataRetorno()));
        veiculo.setComprovanteVistoria(veiculoDTO.getComprovanteVistoria());
        veiculo.setSituacaoVeiculo(veiculoDTO.getSituacaoVeiculo());
        veiculo.setDataMidiaTaxi(LocalDate.parse(veiculoDTO.getDataMidiaTaxi()));
        veiculo.setEmpresaMidiaTaxi(veiculoDTO.getEmpresaMidiaTaxi());
        veiculo.setDataCriacao(LocalDate.parse(veiculoDTO.getDataCriacao()));

        return  veiculo;
    }

}
