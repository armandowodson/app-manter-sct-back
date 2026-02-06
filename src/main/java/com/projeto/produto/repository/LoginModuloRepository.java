package com.projeto.produto.repository;

import com.projeto.produto.entity.LoginModulo;
import com.projeto.produto.entity.PontoTaxi;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginModuloRepository extends JpaRepository<LoginModulo,Integer> {

    LoginModulo findLoginModuloByIdLoginAndNumeroModulo (Long idLogin, Integer modulo);

    @Query(
            value = "SELECT * " +
                    "FROM proj.login_modulo " +
                    "WHERE ID_LOGIN = :idLogin " ,
            nativeQuery = true
    )
    List<LoginModulo> buscarModulosLogin(Long idLogin);
}

