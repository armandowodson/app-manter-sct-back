package com.projeto.produto;

import com.projeto.produto.api.model.ProdutoDTO;
import com.projeto.produto.entity.Produto;
import com.projeto.produto.repository.ProductRepository;
import com.projeto.produto.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProdutoControllerTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    public void testConsultarTodosProdutos() {
        Produto produto = criarMockProduto();
        List<Produto> listaProdutos = new ArrayList<Produto>();
        listaProdutos.add(produto);

        when(productRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))).thenReturn(listaProdutos);
        assertTrue(productServiceImpl.listarTodosProdutos().size() > 0);
    }

    @Test
    public void testConsultarProdutosPorFiltros() {
        Produto produto = criarMockProduto();
        List<Produto> listaProdutos = new ArrayList<Produto>();
        listaProdutos.add(produto);

        when(productRepository.findBySituacao(1)).thenReturn(listaProdutos);
        assertTrue(productServiceImpl.listarTodosProdutosFiltros("", "", (double) 0, 1) .size() > 0);
    }

    @Test
    public void testSalvarProdutoComSucesso() {
        Produto produto = criarMockProduto();

        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setIdproduto(produto.getIdproduto());
        produtoDTO.setNome(produto.getNome());
        produtoDTO.setPreco(produto.getPreco());
        produtoDTO.setSituacao(produto.getSituacao());

        when(productRepository.save(produto)).thenReturn(produto);
        assertNotNull(productServiceImpl.inserirProduto(produtoDTO).getIdproduto());
    }

    @Test
    public void testExcluirProdutoComSucesso() throws Exception {
        Produto produto = criarMockProduto();

        productServiceImpl.excluirProdutoId(1000);
        verify(productRepository,times(1)).deleteById(produto.getIdproduto());
    }

    public Produto criarMockProduto(){
        Produto produto = new Produto();
        produto.setIdproduto(1000);
        produto.setNome("TESTE PRODUTO");
        produto.setPreco(100.50);
        produto.setSituacao(1);
        return produto;
    }
}
