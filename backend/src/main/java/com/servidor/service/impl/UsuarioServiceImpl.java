package com.servidor.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.servidor.model.Usuario;
import com.servidor.repository.UsuarioRepository;
import com.servidor.security.JwtUtil;
import com.servidor.service.NegocioException;
import com.servidor.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Usuario registrar(String username, String password) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new NegocioException("El usuario ya existe");
        }
        String hash = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(username, hash);
        return usuarioRepository.save(usuario);
    }

    @Override
    public String autenticar(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NegocioException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new NegocioException("Contrasena incorrecta");
        }

        return jwtUtil.generarToken(usuario.getId(), usuario.getUsername());
    }
}
