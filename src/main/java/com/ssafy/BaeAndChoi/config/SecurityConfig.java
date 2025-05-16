package com.ssafy.BaeAndChoi.config;

import com.ssafy.BaeAndChoi.config.filter.TokenAuthenticationFilter;
import com.ssafy.BaeAndChoi.config.util.JwtUtil;
import com.ssafy.BaeAndChoi.user.application.CustomUserDetailsService;
import com.ssafy.BaeAndChoi.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(cs -> cs.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // swagger, static
                        .requestMatchers(
                                "/swagger-ui/**","/v3/api-docs/**","/swagger-ui.html",
                                "/error","/favicon.ico","/**/*.css","/**/*.js",
                                "/**/*.png","/**/*.jpg","/**/*.svg", "/api/news/**"
                        ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users/login", "/api/users").permitAll()
                                .requestMatchers(HttpMethod.GET,  "/api/boards/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/boards/**/comments").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/boards/**/comments/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/boards/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)   // 여기서 customUserDetailsService 사용
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(jwtUtil, customUserDetailsService);
    }
}