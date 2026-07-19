package com.MHM.MultiHotelManagement.security;

import com.MHM.MultiHotelManagement.serviceimplement.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 *
 * This filter runs once for every incoming HTTP request.
 * It checks whether a valid JWT token exists in the
 * Authorization header and, if valid, authenticates the user.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    /**
     * Utility class used for JWT operations
     * such as validation and extracting claims.
     */
    private final JwtUtil jwtUtil;

    /**
     * Custom UserDetailsService implementation
     * used to load user information from the database.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * This method executes for every request.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (request.getParameter("token") != null) {
            token = request.getParameter("token");
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JWT Filter - Method: {}, URI: {}, Token length: {}",
                request.getMethod(), request.getRequestURI(), token.length());

        if (jwtUtil.isValid(token)) {
            String email = jwtUtil.extractEmail(token);
            log.debug("JWT Filter - Token valid, email: {}", email);

            if (
                    email != null &&
                            SecurityContextHolder.getContext()
                                    .getAuthentication() == null
            ) {
                try {
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    log.debug("JWT Filter - Authenticated user: {}, roles: {}",
                            email, userDetails.getAuthorities());
                } catch (Exception e) {
                    log.error("JWT Filter - Failed to load user for email: {}", email, e);
                }
            }
        } else {
            log.warn("JWT Filter - Invalid token for URI: {}, token starts with: {}",
                    request.getRequestURI(),
                    token.length() > 10 ? token.substring(0, 10) + "..." : token);
        }

        filterChain.doFilter(request, response);
    }
}
