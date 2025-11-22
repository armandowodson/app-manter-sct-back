package com.projeto.produto.service;

import com.projeto.produto.api.model.ProdutoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<ProdutoDTO> listarTodosProdutos();
    ProdutoDTO obterProdutoId(Integer idProduto);
    List<ProdutoDTO> listarTodosProdutosFiltros(String name, String sinal, Double preco, Integer situacao);
    ProdutoDTO inserirProduto(ProdutoDTO produto);
    ProdutoDTO atualizarProduto(ProdutoDTO produto);
    ResponseEntity<Void> excluirProdutoId(Integer idProduto);

}
