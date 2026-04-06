package com.example.representantes.web;

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

import com.example.representantes.domain.Representante;
import com.example.representantes.domain.RepresentanteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/representantes")
public class RepresentanteController {

	private final RepresentanteRepository repository;

	public RepresentanteController(RepresentanteRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public ResponseEntity<?> cadastrar(@Valid @RequestBody RepresentanteRequest request) {
		String cpf = CpfUtil.normalize(request.cpf());
		if (!CpfUtil.isValidLength(cpf)) {
			return ResponseEntity.badRequest().body("CPF deve conter 11 dígitos.");
		}
		if (repository.findByCpf(cpf).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado.");
		}
		Representante saved = repository
				.save(Representante.builder().cpf(cpf).nome(request.nome().trim()).build());
		return ResponseEntity.status(HttpStatus.CREATED).body(RepresentanteResponse.from(saved));
	}

	@GetMapping
	public List<RepresentanteResponse> listar() {
		return repository.findAll().stream().map(RepresentanteResponse::from).toList();
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<RepresentanteResponse> porId(@PathVariable Long id) {
		return repository.findById(id).map(RepresentanteResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/cpf/{cpf}")
	public ResponseEntity<RepresentanteResponse> porCpf(@PathVariable String cpf) {
		String digits = CpfUtil.normalize(cpf);
		if (!CpfUtil.isValidLength(digits)) {
			return ResponseEntity.badRequest().build();
		}
		return repository.findByCpf(digits).map(RepresentanteResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/buscar")
	public List<RepresentanteResponse> porNome(@RequestParam String nome) {
		return repository.findByNomeContainingIgnoreCase(nome.trim()).stream().map(RepresentanteResponse::from).toList();
	}
}
