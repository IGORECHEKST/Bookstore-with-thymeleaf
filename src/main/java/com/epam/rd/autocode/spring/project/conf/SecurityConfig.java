package com.epam.rd.autocode.spring.project.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationSuccessHandler successHandler;
    private final JwtLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/login", "/css/**", "/js/**", "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books", "/books/view/**").permitAll()
                        .requestMatchers("/books/add", "/books/delete/**", "/books/edit/**").hasRole("EMPLOYEE")
                        .requestMatchers("/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers("/clients").hasRole("EMPLOYEE")
                        .requestMatchers("/clients/profile/**", "/clients/edit/**", "/clients/delete/**")
                        .hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers("/orders/create").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers("/orders/client/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers("/orders/employee/**").hasRole("EMPLOYEE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}