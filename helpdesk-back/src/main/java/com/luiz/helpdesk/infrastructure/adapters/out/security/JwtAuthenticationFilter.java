package com.luiz.helpdesk.infrastructure.adapters.out.security;

import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.exception.security.JwtAuthenticationException;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import com.luiz.helpdesk.infrastructure.adapters.out.config.JwtTokenProvider;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PersonManageUseCasePort personUseCase;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, PersonManageUseCasePort personUseCase) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.personUseCase = personUseCase;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Optional.ofNullable(getJwtFromRequest(request))
                    .filter(jwtTokenProvider::validateToken)
                    .ifPresent(jwt -> authenticateUser(jwt, request));
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(jwt);
        try {
            Person person = personUseCase.findPersonByEmail(email);
            UserDetails userDetails = createUserDetails(person);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (PersonNotFoundException e) {
            throw new JwtAuthenticationException("User not found", e);
        }
    }

    private UserDetails createUserDetails(Person person) {
        return new CustomUserDetails(
                person.getEmail(),
                person.getPassword(),
                person.getProfiles().stream()
                        .map(profile -> new SimpleGrantedAuthority(profile.getDescription()))
                        .collect(Collectors.toList()),
                person.getTheme()
        );
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .orElse(null);
    }
}