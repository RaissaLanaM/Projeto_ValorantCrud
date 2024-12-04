package com.example.demo.controller;

import com.example.demo.model.Agente;
import com.example.demo.service.AgenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agentes")
public class AgenteController {

    @Autowired
    private AgenteService agenteService;

    @PostMapping
    public Agente criarAgente(@RequestBody Agente agente) {
        return agenteService.criarAgente(agente);
    }

    @GetMapping
    public List<Agente> listarAgentes() {
        return agenteService.listarAgentes();
    }

    @PutMapping("/{id}")
    public Agente atualizarAgente(@PathVariable Long id, @RequestBody Agente agenteAtualizado) {
        return agenteService.atualizarAgente(id, agenteAtualizado);
    }

    @DeleteMapping("/{id}")
    public void excluirAgente(@PathVariable Long id) {
        agenteService.excluirAgente(id);
    }
}
