package com.projeto.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionarioRequestDTO {
    private Long idPermissionario;
    private String numeroPermissao;
    private String nomePermissionario;
    private String cpfPermissionario;
    private String cnpjEmpresa;
    private String rgPermissionario;
    private String naturezaPessoa;
    private String cnhPermissionario;
    private String ufPermissionario;
    private String bairroPermissionario;
    private String enderecoPermissionario;
    private String celularPermissionario;
    private String dataCriacao;

}
