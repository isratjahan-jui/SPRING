package com.MHM.MultiHotelManagement.security;


import com.MHM.MultiHotelManagement.serviceimplement.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize on controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints (no token needed) ────────────
                        .requestMatchers(
                                //  auth_api
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/verify",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        // Hotel Search APIs (public - regex for path variables)
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/city/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/location/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/approved").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/owner/**").permitAll()

                        // Location APIs (public)
                        .requestMatchers(HttpMethod.GET, "/api/locations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/locations/*").permitAll()

                        // Review APIs (public read)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        // Media APIs (image serving paths from WebConfig)
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/hotel/**").permitAll()
                        .requestMatchers("/room/**").permitAll()
                        .requestMatchers("/food/**").permitAll()
                        .requestMatchers("/owners/**").permitAll()
                        .requestMatchers("/gallery/**").permitAll()
                        .requestMatchers("/customer/**").permitAll()
                        .requestMatchers("/checkin-id/**").permitAll()
                        .requestMatchers("/location/**").permitAll()

                        // ── Admin only endpoints ──────────────────────────
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")

                        // ── Public read for hotel sub-resources ─────────
                        .requestMatchers(HttpMethod.GET, "/api/hotel-details/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/facilities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/food-items/**").permitAll()

                        // ── Admin hotel management (approve/reject) ──────
                        .requestMatchers(HttpMethod.PUT, "/api/hotels/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/hotels/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/hotels/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/hotels/pending").hasRole("ADMIN")

                        // ── SSLCommerz callbacks (public - no auth) ─────
                        .requestMatchers("/api/payments/sslcommerz/success").permitAll()
                        .requestMatchers("/api/payments/sslcommerz/fail").permitAll()
                        .requestMatchers("/api/payments/sslcommerz/cancel").permitAll()
                        .requestMatchers("/api/payments/sslcommerz/ipn").permitAll()

                        // ── Hotel Owner endpoints ─────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/hotels").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/hotels/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/hotels/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/hotel-owners").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotel-owners").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/hotel-owners/**").hasAnyRole("HOTEL_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/hotel-details/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/hotel-details/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/hotel-details/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/facilities/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/facilities/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/gallery/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/gallery/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/gallery/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/food-items/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/food-items/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/food-items/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/deals/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/deals/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/deals/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/deals/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/coupons/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/coupons/**").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/coupons/**").hasRole("HOTEL_OWNER")
                        .requestMatchers("/api/reports/**").hasRole("HOTEL_OWNER")

                        // ── Customer endpoints ────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .requestMatchers("/api/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/booking-rooms/**").hasAnyRole("CUSTOMER", "ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/payments/**").hasAnyRole("CUSTOMER", "ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/invoices/**").hasAnyRole("CUSTOMER", "ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/wishlists/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/hotel-extra-services/**").permitAll()
                        .requestMatchers("/api/hotel-extra-services/**").hasRole("HOTEL_OWNER")
                        .requestMatchers("/api/extra-services/**").hasAnyRole("CUSTOMER", "ADMIN", "HOTEL_OWNER")

                        // ── Admin + Owner endpoints ───────────────────────
                        .requestMatchers("/api/commissions/**").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers("/api/support/**").hasAnyRole("ADMIN", "CUSTOMER", "HOTEL_OWNER")
                        .requestMatchers("/api/support-replies/**").hasAnyRole("ADMIN", "CUSTOMER", "HOTEL_OWNER")
                        .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "CUSTOMER", "HOTEL_OWNER")

                        // ── All other requests need authentication ────────
                        .anyRequest().authenticated()

                ).authenticationProvider(authenticationProvider())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://192.168.88.250:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT","PATCH",  "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}