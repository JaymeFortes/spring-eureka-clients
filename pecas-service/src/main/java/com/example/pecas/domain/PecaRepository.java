package com.example.pecas.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PecaRepository extends JpaRepository<Peca, Long> {

	Optional<Peca> findByNumeroIdentificacao(String numeroIdentificacao);

	List<Peca> findByNomeContainingIgnoreCase(String nome);
}
