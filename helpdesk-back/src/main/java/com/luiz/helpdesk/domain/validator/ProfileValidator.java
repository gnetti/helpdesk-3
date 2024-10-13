package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.exception.profile.InvalidProfileException;

public class ProfileValidator {

    public static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidProfileException("A descrição do perfil não pode ser nula ou vazia", new IllegalArgumentException("Descrição vazia ou nula"));
        }
    }

    public static void validateCode(Integer code) {
        if (code == null) {
            throw new InvalidProfileException("O código do perfil não pode ser nulo", new IllegalArgumentException("Código nulo"));
        }
    }
}