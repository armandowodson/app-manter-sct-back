package com.projeto.produto.service.impl;

import com.projeto.produto.dto.PontoTaxiDTO;
import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.PontoTaxi;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.AuditoriaRepository;
import com.projeto.produto.repository.PermissionarioRepository;
import com.projeto.produto.repository.PontosTaxiRepository;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class VeiculoServiceImpl {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private PontosTaxiRepository pontosTaxiRepository;

    @Transactional
    public VeiculoResponseDTO inserirVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                             MultipartFile crlv,
                                             MultipartFile comprovanteVistoria) {
        if (Objects.isNull(veiculoRequestDTO.getMarca()) || Objects.isNull(veiculoRequestDTO.getModelo()) ||
                Objects.isNull(veiculoRequestDTO.getModelo()) || Objects.isNull(veiculoRequestDTO.getAnoModelo()) ||
                Objects.isNull(veiculoRequestDTO.getCor()) || Objects.isNull(veiculoRequestDTO.getPlaca()) ||
                Objects.isNull(veiculoRequestDTO.getChassi()) || Objects.isNull(veiculoRequestDTO.getRenavam()) ||
                Objects.isNull(crlv) || Objects.isNull(comprovanteVistoria) || Objects.isNull(veiculoRequestDTO.getIdPermissionario()) ||
                Objects.isNull(veiculoRequestDTO.getNumeroPermissao()) || Objects.isNull(veiculoRequestDTO.getIdPontoTaxi())) {
            throw new RuntimeException("Dados inválidos para o Veículo!");
        }
        if(Objects.isNull(veiculoRequestDTO.getUsuario()) || veiculoRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Veiculo veiculo = new Veiculo();
        try{
            veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria);
            veiculo.setDataCriacao(LocalDate.now());
            veiculo = veiculoRepository.save(veiculo);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "INCLUSÃO", veiculoRequestDTO.getUsuario());
        } catch (Exception e){
            throw new RuntimeException("Não foi possível inserir os dados do Veículo!");
        }

        return converterVeiculoToVeiculoDTO(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizarVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                               MultipartFile crlv,
                                               MultipartFile comprovanteVistoria) {
        if (Objects.isNull(veiculoRequestDTO.getMarca()) || Objects.isNull(veiculoRequestDTO.getModelo()) ||
                Objects.isNull(veiculoRequestDTO.getModelo()) || Objects.isNull(veiculoRequestDTO.getAnoModelo()) ||
                Objects.isNull(veiculoRequestDTO.getCor()) || Objects.isNull(veiculoRequestDTO.getPlaca()) ||
                Objects.isNull(veiculoRequestDTO.getChassi()) || Objects.isNull(veiculoRequestDTO.getRenavam()) ||
                Objects.isNull(crlv) || Objects.isNull(comprovanteVistoria) || Objects.isNull(veiculoRequestDTO.getIdPermissionario()) ||
                Objects.isNull(veiculoRequestDTO.getNumeroPermissao()) || Objects.isNull(veiculoRequestDTO.getIdPontoTaxi())) {
            throw new RuntimeException("Dados inválidos para o Veículo!");
        }

        if(Objects.isNull(veiculoRequestDTO.getUsuario()) || veiculoRequestDTO.getUsuario().isEmpty())
            throw new RuntimeException("Usuário vazio ou não identificado!");

        Veiculo veiculo = new Veiculo();
        try{
            veiculo = converterVeiculoDTOToVeiculo(veiculoRequestDTO, crlv, comprovanteVistoria);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "ALTERAÇÃO", veiculoRequestDTO.getUsuario());
        }catch (Exception e){
            throw new RuntimeException("Não foi possível atualizar os dados do Veículo!");
        }

        return converterVeiculoToVeiculoDTO(veiculoRepository.save(veiculo));
    }

    public Page<VeiculoResponseDTO> listarTodosVeiculos(PageRequest pageRequest) {
        List<Veiculo> veiculoList = veiculoRepository.buscarTodos(pageRequest);
        Integer countLista = veiculoRepository.buscarTodos(null).size();
        List<VeiculoResponseDTO> veiculoResponseDTOList = converterEntityToDTO(veiculoList);
        return new PageImpl<>(veiculoResponseDTOList, pageRequest, countLista);
    }

    public VeiculoResponseDTO buscarVeiculoId(Long idVeiculo) {
        Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
        VeiculoResponseDTO veiculoResponseDTO = new VeiculoResponseDTO();
        if (veiculo != null){
            veiculoResponseDTO = converterVeiculoToVeiculoDTO(veiculo);
        }
        return veiculoResponseDTO;
    }

    public Page<VeiculoResponseDTO> listarTodosVeiculosFiltros(String numeroPermissao, String placa,
                                                               String renavam, String numeroTaximetro,
                                                               String anoFabricacao, PageRequest pageRequest) {
        List<Veiculo> listaVeiculos = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao, pageRequest
        );

        Integer countRegistros = veiculoRepository.listarTodosVeiculosFiltros(
                numeroPermissao,  placa, renavam, numeroTaximetro, anoFabricacao, null
        ).size();

        List<VeiculoResponseDTO> listaVeiculoResponseDTO = new ArrayList<>();
        if (!listaVeiculos.isEmpty()){
            for (Veiculo veiculo : listaVeiculos) {
                VeiculoResponseDTO veiculoResponseDTORetornado = converterVeiculoToVeiculoDTO(veiculo);
                listaVeiculoResponseDTO.add(veiculoResponseDTORetornado);
            }
        }

        return new PageImpl<>(listaVeiculoResponseDTO, pageRequest, countRegistros);
    }

    @Transactional
    public ResponseEntity<Void> excluirVeiculo(Long idVeiculo, String usuario) {
        try{
            if(Objects.isNull(usuario) || usuario.isEmpty())
                throw new RuntimeException("Usuário vazio ou não identificado!");

            Veiculo veiculo = veiculoRepository.findVeiculoByIdVeiculo(idVeiculo);
            veiculo.setStatus("INATVIO");
            veiculoRepository.save(veiculo);

            //Auditoria
            salvarAuditoria("VEÍCULO TÁXI", "EXCLUSÃO", usuario);

            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Não foi possível excluir o Veículo!!!");
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

        veiculoResponseDTO.setIdPermissionario(veiculo.getPermissionario().getIdPermissionario());
        veiculoResponseDTO.setNumeroPermissao(veiculo.getNumeroPermissao());
        veiculoResponseDTO.setIdPontoTaxi(veiculo.getPontoTaxi().getIdPontoTaxi());
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
        veiculoResponseDTO.setDataVistoria(veiculo.getDataVistoria().plusDays(1).toString());
        veiculoResponseDTO.setDataRetorno(veiculo.getDataRetorno().plusDays(1).toString());
        veiculoResponseDTO.setComprovanteVistoria(veiculo.getComprovanteVistoria());
        veiculoResponseDTO.setSituacaoVeiculo(veiculo.getSituacaoVeiculo());
        veiculoResponseDTO.setTipoVeiculo(veiculo.getTipoVeiculo());
        veiculoResponseDTO.setNumeroCrlv(veiculo.getNumeroCrlv());
        veiculoResponseDTO.setAnoCrlv(veiculo.getAnoCrlv());
        veiculoResponseDTO.setCertificadoAfericao(veiculo.getCertificadoAfericao());
        veiculoResponseDTO.setObservacao(veiculo.getObservacao());
        veiculoResponseDTO.setDataCriacao(veiculo.getDataCriacao().toString());
        veiculoResponseDTO.setStatus(veiculo.getStatus());

        return veiculoResponseDTO;
    }

    public Veiculo converterVeiculoDTOToVeiculo(VeiculoRequestDTO veiculoRequestDTO,
                                                MultipartFile crlv,
                                                MultipartFile comprovanteVistoria) throws IOException {
        Veiculo veiculo = new Veiculo();
        if (veiculoRequestDTO.getIdVeiculo() != null && veiculoRequestDTO.getIdVeiculo() != 0){
            veiculo = veiculoRepository.findVeiculoByIdVeiculo(veiculoRequestDTO.getIdVeiculo());
        }

        veiculo.setPermissionario(permissionarioRepository.findPermissionarioByIdPermissionario(veiculoRequestDTO.getIdPermissionario()));
        veiculo.setPontoTaxi(pontosTaxiRepository.findByIdPontoTaxi(veiculoRequestDTO.getIdPontoTaxi()));
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
        if(Objects.nonNull(crlv))
            veiculo.setCrlv(crlv.getBytes());
        veiculo.setNumeroTaximetro(veiculoRequestDTO.getNumeroTaximetro());
        veiculo.setAnoRenovacao(veiculoRequestDTO.getAnoRenovacao());

        if(Objects.nonNull(veiculoRequestDTO.getDataVistoria())) {
            String data = veiculoRequestDTO.getDataVistoria();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateVistoria = zonedDateTime.toLocalDate();
                veiculo.setDataVistoria(localDateVistoria);
            }
        }

        if(Objects.nonNull(veiculoRequestDTO.getDataRetorno())) {
            String data = veiculoRequestDTO.getDataRetorno();
            Integer indexChar = data.indexOf('(');
            if(indexChar > 0){
                data = data.substring(0, indexChar);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.trim(), formatter);
                LocalDate localDateRetorno = zonedDateTime.toLocalDate();
                veiculo.setDataRetorno(localDateRetorno);
            }
        }

        if(Objects.nonNull(comprovanteVistoria))
            veiculo.setComprovanteVistoria(comprovanteVistoria.getBytes());
        veiculo.setSituacaoVeiculo(veiculoRequestDTO.getSituacaoVeiculo());
        veiculo.setTipoVeiculo(veiculoRequestDTO.getTipoVeiculo());

        veiculo.setNumeroCrlv(veiculoRequestDTO.getNumeroCrlv());
        veiculo.setAnoCrlv(veiculoRequestDTO.getAnoCrlv());
        veiculo.setCertificadoAfericao(veiculoRequestDTO.getCertificadoAfericao());
        veiculo.setObservacao(veiculoRequestDTO.getObservacao());

        if(Objects.nonNull(veiculoRequestDTO.getDataCriacao()) && !veiculoRequestDTO.getDataCriacao().isEmpty())
            veiculo.setDataCriacao(LocalDate.parse(veiculoRequestDTO.getDataCriacao()));
        else
            veiculo.setDataCriacao(LocalDate.now());

        veiculo.setStatus("ATIVO");

        return  veiculo;
    }

    public void salvarAuditoria(String modulo, String operacao, String usuario){
        Auditoria auditoria = new Auditoria();
        auditoria.setNomeModulo(modulo);
        auditoria.setOperacao(operacao);
        auditoria.setUsuarioOperacao(usuario);
        auditoria.setDataOperacao(LocalDate.now());
        auditoriaRepository.save(auditoria);
    }
}
