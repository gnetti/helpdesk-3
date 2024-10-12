package com.luiz.helpdesk.infrastructure.adapters.out.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String theme;
    private final Integer id;
    private final String name;
    private final Integer profile;

    public CustomUserDetails(Integer id,
                             String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String theme,
                             String name,
                             Integer profile) {
        super(username, password, authorities);
        this.id = id;
        this.theme = theme;
        this.name = name;
        this.profile = profile;
    }

    public CustomUserDetails(Integer id,
                             String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String theme,
                             String name) {
        this(id, username, password, authorities, theme, name, null);
    }

    public String getTheme() {
        return theme;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getProfile() {
        return profile;
    }
}