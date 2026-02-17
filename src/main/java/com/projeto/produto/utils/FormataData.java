package com.projeto.produto.utils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormataData {
    public static String formatarDataLocalDate(String dataOperacao){
        LocalDate localDate = LocalDate.now();
        Integer indexChar = dataOperacao.indexOf('(');
        String dataFormatada = "";
        if(indexChar > 0){
            dataOperacao = dataOperacao.substring(0, indexChar);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dataOperacao.trim(), formatter);
            localDate = zonedDateTime.toLocalDate();

            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataFormatada = localDate.format(formatter);
        }

        return dataFormatada;
    }
}
