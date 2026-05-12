package edu.icesi.emprendimientos.security;

import edu.icesi.emprendimientos.rest.security.JwtFilter;
import edu.icesi.emprendimientos.rest.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SecurityConfig(UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * JwtFilter se define como @Bean aquí (sin @Component en la clase) para que
     * Spring Boot NO lo registre automáticamente como servlet filter global.
     * Solo corre dentro de la security filter chain de la API REST.
     */
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // ── Cadena para la REST API (/api/**) ────────────────────────────────
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"No autorizado\",\"mensaje\":\"Se requiere autenticación\"}");
                        })
                );

        return http.build();
    }

    // ── Cadena para la app MVC (Thymeleaf) ───────────────────────────────
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // La MVC chain NUNCA maneja /api/** — eso lo hace la apiFilterChain
                .securityMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**")))
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/login", "/usuarios/nuevo", "/acceso-denegado").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Recursos estáticos
                        .requestMatchers("/uploads/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // Admin exclusivo
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                        .requestMatchers("/estudiantes/**").hasRole("ADMIN")
                        .requestMatchers("/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios", "/usuarios/").hasRole("ADMIN")
                        .requestMatchers("/usuarios/eliminar/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/asignar-roles/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/quitar-rol").hasRole("ADMIN")
                        // Reporte PDF: EMPRENDEDOR o ADMIN
                        .requestMatchers("/emprendimientos/reporte").hasAnyRole("EMPRENDEDOR", "ADMIN")
                        // Emprendimientos: EMPRENDEDOR o ADMIN
                        .requestMatchers("/emprendimientos/**").hasAnyRole("EMPRENDEDOR", "ADMIN")
                        // Mis emprendimientos (panel del emprendedor): EMPRENDEDOR o ADMIN
                        .requestMatchers("/mis-emprendimientos/**").hasAnyRole("EMPRENDEDOR", "ADMIN")
                        // Marketplace: cualquier autenticado puede explorar
                        .requestMatchers("/marketplace/**").authenticated()
                        // Carrito: COMPRADOR o ADMIN
                        .requestMatchers("/carrito/**").hasAnyRole("COMPRADOR", "ADMIN")
                        // Crear pedido y ver mis pedidos: COMPRADOR o ADMIN
                        .requestMatchers("/pedidos/crear").hasAnyRole("COMPRADOR", "ADMIN")
                        .requestMatchers("/pedidos/mis-pedidos/**").hasAnyRole("COMPRADOR", "ADMIN")
                        // Pedidos recibidos y cambio de estado: EMPRENDEDOR o ADMIN
                        .requestMatchers("/pedidos/recibidos/**").hasAnyRole("EMPRENDEDOR","ADMIN")
                        .requestMatchers("/pedidos/actualizar-estado/**").hasAnyRole("EMPRENDEDOR","ADMIN")
                        // Perfil propio
                        .requestMatchers("/perfil", "/perfil/foto").authenticated()
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
