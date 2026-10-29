package com.servidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend PixelVault.
 * Proyecto: Avance de Proyecto Final 1 (APF1) - Marcos de Desarrollo Web - UTP
 * Arquitectura: Java 17 + Spring Boot (REST API) + PostgreSQL
 */
@SpringBootApplication
public class PixelVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(PixelVaultApplication.class, args);
        System.out.println("PixelVault backend corriendo en http://localhost:8080/api");
    }
}
