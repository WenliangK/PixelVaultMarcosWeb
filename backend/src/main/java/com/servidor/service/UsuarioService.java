package com.servidor.service;

import com.servidor.model.Usuario;

public interface UsuarioService {
    Usuario registrar(String username, String password);
    String autenticar(String username, String password);
}
