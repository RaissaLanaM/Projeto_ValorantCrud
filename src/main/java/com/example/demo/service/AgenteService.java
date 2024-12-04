package com.example.demo.service;

import com.example.demo.model.Agente;
import com.example.demo.repository.AgenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    // Criar um novo agente
    public Agente criarAgente(Agente agente) {
        return agenteRepository.save(agente);
    }

    // Listar todos os agentes
    public List<Agente> listarAgentes() {
        return agenteRepository.findAll();
    }

    // Atualizar um agente existente
    public Agente atualizarAgente(Long id, Agente agenteAtualizado) {
        Optional<Agente> agenteExistente = agenteRepository.findById(id);
        if (agenteExistente.isPresent()) {
            Agente agente = agenteExistente.get();
            agente.setNome(agenteAtualizado.getNome());
            agente.setHabilidade(agenteAtualizado.getHabilidade());
            agente.setFuncao(agenteAtualizado.getFuncao());
            return agenteRepository.save(agente);
        } else {
            throw new RuntimeException("Agente não encontrado com o ID: " + id);
        }
    }

    // Excluir um agente pelo ID
    public void excluirAgente(Long id) {
        agenteRepository.deleteById(id);
    }
}
