package com.example.demo.repository;

import com.example.demo.model.Agente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgenteRepository extends JpaRepository<Agente, Long> {
    // Métodos adicionais, se necessário, podem ser adicionados aqui
}
