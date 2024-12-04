package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para registrar um usuário
    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        try {
            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    // Endpoint para autenticar o usuário
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        if (usuarioService.autenticarUsuario(usuario.getUsername(), usuario.getPassword())) {
            return ResponseEntity.ok("Login bem-sucedido!");
        } else {
            return ResponseEntity.status(401).body("Credenciais inválidas!");
        }
    }

    // Endpoint de teste
    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("API funcionando corretamente!");
    }
}
