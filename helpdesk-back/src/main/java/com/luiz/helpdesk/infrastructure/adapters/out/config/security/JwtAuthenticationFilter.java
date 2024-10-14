package com.luiz.helpdesk.infrastructure.adapters.out.config.security;

import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import com.luiz.helpdesk.infrastructure.adapters.out.config.JwtTokenProvider;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                authenticateUser(jwt, request);
            }
        } catch (Exception ex) {
            throw new ServletException("Failed to process authentication request", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) throws ServletException {
        try {
            String email = jwtTokenProvider.getEmailFromToken(jwt);
            Person person = personUseCase.findPersonByEmail(email);
            UserDetails userDetails = createUserDetails(person);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (PersonNotFoundException e) {
            throw new ServletException("User not found", e);
        } catch (Exception e) {
            throw new ServletException("Error during authentication", e);
        }
    }

    private UserDetails createUserDetails(Person person) {
        Profile profile = Profile.fromCode(person.getProfile());
        return new CustomUserDetails(
                person.getId(),
                person.getEmail(),
                person.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(profile.getDescription())),
                person.getTheme().toString(),
                person.getName(),
                person.getProfile()
        );
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}