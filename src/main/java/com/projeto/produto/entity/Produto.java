package com.projeto.produto.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity (name = "proj.produto")
@Table(name="produto", schema="proj")
@SequenceGenerator(name = "seq_produto", schema = "proj", sequenceName = "seq_produto", initialValue = 12, allocationSize = 1)
@Data
public class Produto {
    @Id
    @Column(name = "ID_PRODUTO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_produto")
    private Integer idproduto;
    @Column(name = "NOME_PRODUTO")
    private String nome;
    @Column(name = "DESCRICAO_PRODUTO")
    private String descricao;
    @Column(name = "VALOR_PRODUTO")
    private Double preco;
    @Column(name = "SITUACAO_PRODUTO")
    private Integer situacao;
    @Column(name = "DATA_CRIACAO_PRODUTO")
    private LocalDate dataCriacao;
    @Column(name = "DATA_ALTERACAO_PRODUTO")
    private LocalDate dataAlteracao;
}
