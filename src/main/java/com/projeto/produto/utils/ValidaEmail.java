package com.projeto.produto.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidaEmail {
    public static boolean isEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
}
