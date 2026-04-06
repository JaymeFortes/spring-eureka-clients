package com.example.pecas.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pecas.domain.Peca;
import com.example.pecas.domain.PecaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {

	private final PecaRepository repository;

	public PecaController(PecaRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public ResponseEntity<PecaResponse> cadastrar(@Valid @RequestBody PecaRequest request) {
		if (repository.findByNumeroIdentificacao(request.numeroIdentificacao()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		Peca saved = repository.save(Peca.builder()
				.numeroIdentificacao(request.numeroIdentificacao().trim())
				.nome(request.nome().trim())
				.descricao(request.descricao() != null ? request.descricao().trim() : null)
				.build());
		return ResponseEntity.status(HttpStatus.CREATED).body(PecaResponse.from(saved));
	}

	@GetMapping
	public List<PecaResponse> listar() {
		return repository.findAll().stream().map(PecaResponse::from).toList();
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<PecaResponse> porId(@PathVariable Long id) {
		return repository.findById(id).map(PecaResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/numero/{numero}")
	public ResponseEntity<PecaResponse> porNumero(@PathVariable String numero) {
		return repository.findByNumeroIdentificacao(numero).map(PecaResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/buscar")
	public List<PecaResponse> porNome(@RequestParam String nome) {
		return repository.findByNomeContainingIgnoreCase(nome.trim()).stream().map(PecaResponse::from).toList();
	}
}
