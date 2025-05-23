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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // 기본 CORS 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 무상태로 설정
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**"))
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**","/v3/api-docs/**","/swagger-ui.html",
                                "/error","/favicon.ico","/**/*.css","/**/*.js",
                                "/api/apartments/**", "/api/lwdCd/**",
                                "/**/*.png","/**/*.jpg","/**/*.svg", "/api/news/**","/api/chat/**","/api/aws/**"
                        ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users/**","/api/users/login").permitAll()
                                .requestMatchers(HttpMethod.GET,  "/api/boards/**","/api/chat/**","/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/boards/**/comments","/api/interestRegion").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/boards/**/comments/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/boards/**","/api/interestRegion/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/interestRegion/**","/api/interestHouse/**").authenticated()
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
                .userDetailsService(customUserDetailsService)
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