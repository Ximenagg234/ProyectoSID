package edu.icesi.emprendimientos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/login", "/usuarios/nuevo").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Recursos estáticos
                        .requestMatchers("/uploads/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // Admin exclusivo
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                        .requestMatchers("/estudiantes/**").hasRole("ADMIN")
                        .requestMatchers("/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/eliminar/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/asignar-roles/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/quitar-rol").hasRole("ADMIN")
                        // Reporte PDF: EMPRENDEDOR o ADMIN
                        .requestMatchers("/emprendimientos/reporte").hasAnyRole("EMPRENDEDOR", "ADMIN")
                        // Emprendimientos: EMPRENDEDOR o ADMIN
                        .requestMatchers("/emprendimientos/**").hasAnyRole("EMPRENDEDOR", "ADMIN")
                        // Marketplace: cualquier autenticado puede explorar
                        .requestMatchers("/marketplace/**").authenticated()
                        // Carrito: solo COMPRADOR
                        .requestMatchers("/carrito/**").hasRole("COMPRADOR")
                        // Crear pedido y ver mis pedidos: COMPRADOR
                        .requestMatchers("/pedidos/crear").hasRole("COMPRADOR")
                        .requestMatchers("/pedidos/mis-pedidos/**").hasRole("COMPRADOR")
                        // Pedidos recibidos y cambio de estado: EMPRENDEDOR o ADMIN
                        .requestMatchers("/pedidos/recibidos/**").hasAnyRole("EMPRENDEDOR","ADMIN")
                        .requestMatchers("/pedidos/actualizar-estado/**").hasAnyRole("EMPRENDEDOR","ADMIN")
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/acceso-denegado")
                        )
                );

        return http.build();
    }
}
