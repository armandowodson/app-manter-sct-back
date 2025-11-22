package com.projeto.produto.controller;

import com.projeto.produto.api.model.ProdutoDTO;
import com.projeto.produto.service.ProductService;
import io.swagger.api.ProdutoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProdutoController implements ProdutoApi {
    @Autowired
    public ProductService service;

    @Override
    public ResponseEntity<List<ProdutoDTO>> listarTodosProdutos() {
        return ResponseEntity.ok(service.listarTodosProdutos());
    }

    @Override
    public ResponseEntity<ProdutoDTO> obterProdutoId(Integer idProduto) {
        return ResponseEntity.ok(service.obterProdutoId(idProduto));
    }

    @Override
    public ResponseEntity<List<ProdutoDTO>> listarTodosProdutosFiltros(String name, String sinal, Double preco, Integer situacao) {
        return ResponseEntity.ok(service.listarTodosProdutosFiltros(name, sinal, preco, situacao));
    }

    @Override
    public ResponseEntity<ProdutoDTO> inserirProduto(ProdutoDTO produto) {
        return ResponseEntity.ok(service.inserirProduto(produto));
    }

    @Override
    public ResponseEntity<ProdutoDTO> atualizarProduto(ProdutoDTO produto) {
        return ResponseEntity.ok(service.atualizarProduto(produto));
    }

    @Override
    public ResponseEntity<Void> excluirProdutoId(Integer idProduto) {
        try {
            return service.excluirProdutoId(idProduto);
        }catch (RuntimeException e){
            throw e;
        }
    }
}
