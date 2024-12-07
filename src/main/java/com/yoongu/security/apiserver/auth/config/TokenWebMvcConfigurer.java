package com.yoongu.security.apiserver.auth.config;

import com.yoongu.security.apiserver.access.interceptor.AccessLoggingInterceptor;
import com.yoongu.security.apiserver.common.SpringProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Profile({SpringProfile.DEV, SpringProfile.STAGING, SpringProfile.UAT, SpringProfile.PROD})
@Configuration
@RequiredArgsConstructor
public class TokenWebMvcConfigurer implements WebMvcConfigurer {

    private final AccessLoggingInterceptor accessLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLoggingInterceptor).addPathPatterns("/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ISO_DATE);
        registrar.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME);
        registrar.registerFormatters(registry);
    }

}
