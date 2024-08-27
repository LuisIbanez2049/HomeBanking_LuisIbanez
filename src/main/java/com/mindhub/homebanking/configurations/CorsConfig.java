package com.mindhub.homebanking.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing) para permitir solicitudes desde dominios específicos.
     *
     * @return Una instancia de CorsConfigurationSource que define las reglas de CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Especifica los orígenes permitidos para las solicitudes CORS.
        // Aquí, se permiten solicitudes desde http://localhost:8080 y http://localhost:5173.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:5173"));

        // Especifica los métodos HTTP permitidos para las solicitudes CORS.
        // En este caso, se permiten los métodos GET, POST, PUT y DELETE.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        // Especifica los encabezados permitidos para las solicitudes CORS.
        // Se permiten todos los encabezados con "*".
        configuration.setAllowedHeaders(List.of("*"));

        // Configura la fuente de configuración de CORS con las reglas especificadas.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        // Retorna la fuente de configuración de CORS.
        return source;
    }
}
