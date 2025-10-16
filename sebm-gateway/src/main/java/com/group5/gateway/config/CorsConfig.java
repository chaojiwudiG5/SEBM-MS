package com.group5.gateway.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
      CorsConfiguration config = new CorsConfiguration();

      config.addAllowedMethod("*");
      config.setAllowCredentials(true);
      config.setAllowedOriginPatterns(Arrays.asList("*"));
      config.addAllowedHeader("*");

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
      source.registerCorsConfiguration("/**", config);
      return new CorsWebFilter(source);
    }
}
