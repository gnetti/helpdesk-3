package com.luiz.helpdesk.infrastructure.adapters.out.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String theme;

    public CustomUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String theme) {
        super(username, password, authorities);
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }
}