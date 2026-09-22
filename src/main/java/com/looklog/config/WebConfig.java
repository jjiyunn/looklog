package com.looklog.config;

import com.looklog.interceptor.EmailVerificationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EmailVerificationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/email-verify", "/email-verify/**",
                        "/login", "/logout",
                        "/signup", "/signup/**",
                        "/password-reset", "/password-reset/**",
                        "/css/**", "/js/**", "/images/**", "/uploads/**","/font/**"
                );
    }
}