package com.projeto.produto.utils;

import com.projeto.produto.dto.VeiculoRequestDTO;
import com.projeto.produto.dto.VeiculoResponseDTO;
import com.projeto.produto.entity.Auditoria;
import com.projeto.produto.entity.Defensor;
import com.projeto.produto.entity.Permissionario;
import com.projeto.produto.entity.Veiculo;
import com.projeto.produto.repository.*;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ImprimirAnexosServiceImpl {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PermissionarioRepository permissionarioRepository;

    @Autowired
    private DefensorRepository defensorRepository;

    private static final Logger logger = LogManager.getLogger(ImprimirAnexosServiceImpl.class);

    public byte[] imprimirAnexo(String idAplicacao, String aplicacao, String anexo, String modulo) {
        logger.info("Início Impressão do Anexo Busca dos Dados");
        try{
            Veiculo veiculo = new Veiculo();
            if(aplicacao.equals("veiculo")){
                veiculo = veiculoRepository.findVeiculoByIdVeiculo(Long.valueOf(idAplicacao));
                if(Objects.isNull(veiculo))
                    throw new RuntimeException("400");
                byte[] bytes = imprimirAnexoVeiculoJasper(veiculo, anexo, modulo);
                return bytes;
            }

            Permissionario permissionario = new Permissionario();
            if(aplicacao.equals("permissionario")){
                permissionario = permissionarioRepository.findPermissionarioByIdPermissionario(Long.valueOf(idAplicacao));
                if(Objects.isNull(veiculo))
                    throw new RuntimeException("400");
                byte[] bytes = imprimirAnexoAutorizatarioJasper(permissionario, anexo, modulo);
                return bytes;
            }

            Defensor defensor = new Defensor();
            if(aplicacao.equals("defensor")){
                defensor = defensorRepository.findDefensorByIdDefensor(Long.valueOf(idAplicacao));
                if(Objects.isNull(veiculo))
                    throw new RuntimeException("400");
                byte[] bytes = imprimirAnexoDefensorJasper(defensor, anexo, modulo);
                return bytes;
            }

            return null;
        } catch (Exception e){
            logger.error("imprimirAnexo: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] imprimirAnexoVeiculoJasper(Veiculo veiculo, String anexo, String modulo) {
        logger.info("Início Imprimir Anexo CRLV Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/anexoDocumento.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            InputStream fotoStream = new ByteArrayInputStream(veiculo.getCrlv());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("imagemAnexo", fotoStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirAnexoCrlv: " + e.getMessage());
            throw new RuntimeException("500");
        }
    }

    public byte[] imprimirAnexoAutorizatarioJasper(Permissionario permissionario, String anexo, String modulo) {
        logger.info("Início Imprimir Anexo Autorizatário Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/anexoDocumento.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            Map<String, Object> parameters = new HashMap<>();
            InputStream imagemStream = null;
            if(anexo.equals("rg")){
                if(Objects.isNull(permissionario.getAnexoRg()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getAnexoRg());
            }
            if(anexo.equals("cpf")){
                if(Objects.isNull(permissionario.getAnexoCpf()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getAnexoCpf());
            }
            if(anexo.equals("cnh")){
                if(Objects.isNull(permissionario.getAnexoCnh()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getAnexoCnh());
            }
            if(anexo.equals("residencia")){
                if(Objects.isNull(permissionario.getComprovanteResidencia()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getComprovanteResidencia());
            }
            if(anexo.equals("multas")){
                if(Objects.isNull(permissionario.getCertidaoNegativaMunicipal()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getCertidaoNegativaMunicipal());
            }
            if(anexo.equals("criminal")){
                if(Objects.isNull(permissionario.getCertidaoNegativaCriminal()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getCertidaoNegativaCriminal());
            }
            if(anexo.equals("propriedade")){
                if(Objects.isNull(permissionario.getCertificadoPropriedade()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getCertificadoPropriedade());
            }
            if(anexo.equals("certificado")){
                if(Objects.isNull(permissionario.getCertificadoCondutor()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getCertificadoCondutor());
            }
            if(anexo.equals("vida")){
                if(Objects.isNull(permissionario.getApoliceSeguroVida()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getApoliceSeguroVida());
            }
            if(anexo.equals("motocicleta")){
                if(Objects.isNull(permissionario.getApoliceSeguroMotocicleta()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getApoliceSeguroMotocicleta());
            }
            if(anexo.equals("foto")){
                if(Objects.isNull(permissionario.getFoto()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(permissionario.getFoto());
            }
            parameters.put("imagemAnexo", imagemStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirAnexoAutorizatario: " + e.getMessage());
            if(e.getMessage().equals("400"))
                throw new RuntimeException(e.getMessage());
            else
                throw new RuntimeException("500");
        }
    }

    public byte[] imprimirAnexoDefensorJasper(Defensor defensor, String anexo, String modulo) {
        logger.info("Início Imprimir Anexo Autorizatário Jasper");
        try{
            ClassPathResource resource = new ClassPathResource("reports/anexoDocumento.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            Map<String, Object> parameters = new HashMap<>();
            InputStream imagemStream = null;
            if(anexo.equals("rg")){
                if(Objects.isNull(defensor.getAnexoRg()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getAnexoRg());
            }
            if(anexo.equals("cpf")){
                if(Objects.isNull(defensor.getAnexoCpf()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getAnexoCpf());
            }
            if(anexo.equals("cnh")){
                if(Objects.isNull(defensor.getAnexoCnh()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getAnexoCnh());
            }
            if(anexo.equals("residencia")){
                if(Objects.isNull(defensor.getComprovanteResidencia()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getComprovanteResidencia());
            }
            if(anexo.equals("multas")){
                if(Objects.isNull(defensor.getCertidaoNegativaMunicipal()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getCertidaoNegativaMunicipal());
            }
            if(anexo.equals("criminal")){
                if(Objects.isNull(defensor.getCertidaoNegativaCriminal()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getCertidaoNegativaCriminal());
            }
            if(anexo.equals("propriedade")){
                if(Objects.isNull(defensor.getCertificadoPropriedade()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getCertificadoPropriedade());
            }
            if(anexo.equals("certificado")){
                if(Objects.isNull(defensor.getCertificadoCondutor()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getCertificadoCondutor());
            }
            if(anexo.equals("vida")){
                if(Objects.isNull(defensor.getApoliceSeguroVida()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getApoliceSeguroVida());
            }
            if(anexo.equals("motocicleta")){
                if(Objects.isNull(defensor.getApoliceSeguroMotocicleta()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getApoliceSeguroMotocicleta());
            }
            if(anexo.equals("foto")){
                if(Objects.isNull(defensor.getFoto()))
                    throw new RuntimeException("400");
                imagemStream = new ByteArrayInputStream(defensor.getFoto());
            }
            parameters.put("imagemAnexo", imagemStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (Exception e){
            logger.error("imprimirAnexoDefensor: " + e.getMessage());
            if(e.getMessage().equals("400"))
                throw new RuntimeException(e.getMessage());
            else
                throw new RuntimeException("500");
        }
    }

}
