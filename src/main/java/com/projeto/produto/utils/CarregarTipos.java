package com.projeto.produto.utils;

public class CarregarTipos {
    public static String carregarTipoCombustivelVeiculo(String tipo) {
        String strTipo = "";
        switch (tipo) {
            case "1":
                strTipo = "Gasolina";
                break;
            case "2":
                strTipo = "Álcool/Etanol";
                break;
            case "3":
                strTipo = "Diesel";
                break;
            case "4":
                strTipo = "Gás Natural";
                break;
            case "5":
                strTipo = "Eletricidade";
                break;
        }
        return strTipo;
    }

    public static String carregarStatusVistoriaVeiculo(String status) {
        String strStatus = "";
        switch (status) {
            case "1":
                strStatus = "Aprovado";
                break;
            case "2":
                strStatus = "Ressalvas";
                break;
            case "3":
                strStatus = "Reprovado";
                break;
        }
        return strStatus;
    }

    public static String carregarCategoriaVeiculo(String tipo) {
        String strTipo = "";
        switch (tipo) {
            case "1":
                strTipo = "Convencional";
                break;
            case "2":
                strTipo = "Executivo";
                break;
            case "3":
                strTipo = "Especial";
                break;
        }
        return strTipo;
    }

    public static String carregarStatusPermissao(String status) {
        String strStatus = "";
        switch (status) {
            case "1":
                strStatus = "Gerada";
                break;
            case "2":
                strStatus = "Em Uso";
                break;
            case "3":
                strStatus = "Suspensa";
                break;
            case "4":
                strStatus = "Renunciada";
                break;
            case "5":
                strStatus = "Reservada";
                break;
            case "6":
                strStatus = "Substituída";
                break;
            case "7":
                strStatus = "Revogada";
                break;
            case "8":
                strStatus = "Expirada";
                break;
            case "9":
                strStatus = "Abandonada";
                break;
        }
        return strStatus;
    }

    public static String carregarEstadoCivilPermissionario(String estado) {
        String strEstado = "";
        switch (estado) {
            case "1":
                strEstado = "Solteiro";
                break;
            case "2":
                strEstado = "Casado";
                break;
            case "3":
                strEstado = "Separado";
                break;
            case "4":
                strEstado = "Divorciado";
                break;
            case "5":
                strEstado = "Viúvo";
                break;
        }
        return strEstado;
    }

    public static String carregarCategoriaCnh(String categoria) {
        String strCategoria = "";
        switch (categoria) {
            case "1":
                strCategoria = "B";
                break;
            case "2":
                strCategoria = "C";
                break;
            case "3":
                strCategoria = "D";
                break;
            case "4":
                strCategoria = "E";
                break;
        }
        return strCategoria;
    }
}
