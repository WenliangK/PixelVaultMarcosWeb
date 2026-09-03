package com.servidor.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> RUTAS_PROTEGIDAS = List.of("/api/compras");

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean requiereAuth = RUTAS_PROTEGIDAS.stream().anyMatch(path::startsWith);

        if (!requiereAuth) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            enviarError(response, "Token no proporcionado. Inicia sesion nuevamente.");
            return;
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.validarYObtenerClaims(token);
            // Se castea via toString()->parseLong para evitar problemas de tipo
            // (JJWT puede deserializar numeros JSON como Integer o Long segun el valor).
            Long usuarioId = Long.parseLong(claims.get("id").toString());
            request.setAttribute("usuarioId", usuarioId);
            request.setAttribute("username", claims.getSubject());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            enviarError(response, "Token invalido o expirado.");
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, String> body = new HashMap<>();
        body.put("error", mensaje);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
