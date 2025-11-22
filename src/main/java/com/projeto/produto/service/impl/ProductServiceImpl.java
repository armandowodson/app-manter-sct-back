package com.projeto.produto.service.impl;

import com.projeto.produto.api.model.ProdutoDTO;
import com.projeto.produto.entity.Produto;
import com.projeto.produto.repository.ProductRepository;
import com.projeto.produto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProdutoDTO inserirProduto(ProdutoDTO produtoDTO) {
        if (Objects.isNull(produtoDTO.getNome()) || Objects.isNull(produtoDTO.getPreco()) ||
                Objects.isNull(produtoDTO.getSituacao()) || produtoDTO.getNome().isEmpty() ||
                produtoDTO.getPreco().equals(0D)) {
            throw new RuntimeException("Dados inválidos para o Produto!");
        }
        Produto produto = converterProdutoDTOToProduto(produtoDTO);
        produto.setDataCriacao(LocalDate.now());
        produto = productRepository.save(produto);
        return converterProdutoToProdutoDTO(produto);
    }

    @Override
    public List<ProdutoDTO> listarTodosProdutos() {
        List<Produto> listaProdutos = productRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
        return converterEntityToDTO(listaProdutos);
    }

    @Override
    public ProdutoDTO obterProdutoId(Integer idproduto) {
        Produto produto = productRepository.findById(idproduto).orElse(null);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        if (produto != null){
            produtoDTO = converterProdutoToProdutoDTO(produto);
        }
        return produtoDTO;
    }

    @Override
    public List<ProdutoDTO> listarTodosProdutosFiltros(String nome, String sinal, Double preco, Integer situacao) {
        List<Produto> listaProdutos;

        if (nome.isEmpty() && situacao == 0 && sinal.isEmpty() && preco == 0){
            listaProdutos = productRepository.findAll();
        }else{
            if (!nome.isEmpty() && situacao > 0 && !sinal.isEmpty() && preco > 0){
                listaProdutos = buscarNomeSinalPrecoSituacao(nome, sinal, preco, situacao);
            }else{
                if (!nome.isEmpty() && situacao > 0){
                    listaProdutos = productRepository.findByNomeStartsWithIgnoreCaseAndSituacao(nome, situacao);
                }else{
                    listaProdutos = buscarNomeSituacaoOrSinalPreco(nome, situacao, sinal, preco);
                }
            }
        }

        List<ProdutoDTO> listaProdutosDTO = new ArrayList<>();
        if (!listaProdutos.isEmpty()){
            for (Produto produto : listaProdutos) {
                ProdutoDTO produtoDTO = converterProdutoToProdutoDTO(produto);
                listaProdutosDTO.add(produtoDTO);
            }
        }

        return listaProdutosDTO;
    }

    @Override
    public ResponseEntity<Void> excluirProdutoId(Integer idProduto) {
        try{
            productRepository.deleteById(idProduto);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            throw new RuntimeException("Erro ao Excluir o Produto!!!");
        }
    }

    @Override
    public ProdutoDTO atualizarProduto(ProdutoDTO produtoDTO) {
        Produto produto = converterProdutoDTOToProduto(produtoDTO);
        produto.setDataAlteracao(LocalDate.now());
        return converterProdutoToProdutoDTO(productRepository.save(produto));
    }

    public List<ProdutoDTO> converterEntityToDTO(List<Produto> listaProdutos){
        List<ProdutoDTO> listaProdutoDTO = new ArrayList<>();
        for(Produto produto : listaProdutos){
            ProdutoDTO produtoDTO = converterProdutoToProdutoDTO(produto);
            listaProdutoDTO.add(produtoDTO);
        }

        return  listaProdutoDTO;
    }

    public ProdutoDTO converterProdutoToProdutoDTO(Produto produto){
        ProdutoDTO produtoDTO = new ProdutoDTO();
        if (produto.getIdproduto() != null){
            produtoDTO.setIdproduto(produto.getIdproduto());
        }
        produtoDTO.setNome(produto.getNome());
        produtoDTO.setDescricao(produto.getDescricao());
        produtoDTO.setPreco(produto.getPreco());
        produtoDTO.setSituacao(produto.getSituacao());

        return  produtoDTO;
    }

    public Produto converterProdutoDTOToProduto(ProdutoDTO produtoDTO){
        Produto produto = new Produto();
        if (produtoDTO.getIdproduto() != null && produtoDTO.getIdproduto() != 0){
            produto = productRepository.findById(produtoDTO.getIdproduto()).orElse(null);;
        }
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setSituacao(produtoDTO.getSituacao());

        return  produto;
    }

    public List<Produto> buscarNomeSinalPrecoSituacao(String nome, String sinal, Double preco, Integer situacao){
        List<Produto> listaProdutos = new ArrayList<>();
        if (sinal.equals(">=")){
            listaProdutos = productRepository.findByNomeStartsWithIgnoreCaseAndSituacaoAndPrecoGreaterThanEqual(
                    nome, situacao, preco
            );
        }
        if (sinal.equals("<=")){
            listaProdutos = productRepository.findByNomeStartsWithIgnoreCaseAndSituacaoAndPrecoLessThanEqual(
                    nome, situacao, preco
            );
        }
        return listaProdutos;
    }

    public List<Produto> buscarSinalPreco(String sinal, Double preco){
        List<Produto> listaProdutos = new ArrayList<>();
        if (sinal.equals("=")){
            listaProdutos = productRepository.findByPrecoBetween(preco, preco);
        }
        if (sinal.equals(">=")){
            listaProdutos = productRepository.findByPrecoGreaterThanEqual(preco);
        }
        if (sinal.equals("<=")){
            listaProdutos = productRepository.findByPrecoLessThanEqual(preco);
        }
        return listaProdutos;
    }

    public List<Produto> buscarNomeSituacao(String nome, Integer situacao){
        List<Produto> listaProdutos = new ArrayList<>();
        if (!nome.isEmpty()){
            listaProdutos = productRepository.findByNomeStartsWithIgnoreCase(nome);
        }else{
            if (situacao > 0){
                listaProdutos = productRepository.findBySituacao(situacao);
            }
        }
        return listaProdutos;
    }

    public List<Produto> buscarNomeSituacaoOrSinalPreco(String nome, Integer situacao, String sinal, Double preco){
        List<Produto> listaProdutos;
        if (!sinal.isEmpty() && preco > 0){
            listaProdutos = buscarSinalPreco(sinal, preco);
        }else{
            listaProdutos = buscarNomeSituacao(nome, situacao);
        }
        return listaProdutos;
    }
}
