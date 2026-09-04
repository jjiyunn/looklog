package com.looklog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // 일단 전부 다 허용 (개발 단계용)
                )
                .csrf(csrf -> csrf.disable())   // 일단 CSRF 보호도 꺼둠 (개발 단계용)
                .formLogin(form -> form.disable())   // 기본 로그인 폼 끄기
                .httpBasic(basic -> basic.disable());  // 기본 로그인 팝업도 끄기

        return http.build();
    }
}