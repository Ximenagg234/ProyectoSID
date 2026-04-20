package edu.icesi.emprendimientos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/login", "/usuarios/nuevo").permitAll()
                        // Admin
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                        .requestMatchers("/estudiantes/**").hasRole("ADMIN")
                        // Marketplace: cualquier autenticado puede explorar
                        .requestMatchers("/marketplace/**").authenticated()
                        // Carrito: solo COMPRADOR
                        .requestMatchers("/carrito/**").hasRole("COMPRADOR")
                        // Mis pedidos: COMPRADOR
                        .requestMatchers("/pedidos/mis-pedidos/**").hasRole("COMPRADOR")
                        // Pedidos recibidos: EMPRENDEDOR
                        .requestMatchers("/pedidos/recibidos/**").hasRole("EMPRENDEDOR")
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/usuarios", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}
