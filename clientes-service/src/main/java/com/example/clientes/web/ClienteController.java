package com.example.clientes.web;

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

import com.example.clientes.domain.Cliente;
import com.example.clientes.domain.ClienteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteRepository repository;

	public ClienteController(ClienteRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteRequest request) {
		String cpf = CpfUtil.normalize(request.cpf());
		if (!CpfUtil.isValidLength(cpf)) {
			return ResponseEntity.badRequest().body("CPF deve conter 11 dígitos.");
		}
		if (repository.findByCpf(cpf).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado.");
		}
		Cliente saved = repository.save(Cliente.builder().cpf(cpf).nome(request.nome().trim()).build());
		return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.from(saved));
	}

	@GetMapping
	public List<ClienteResponse> listar() {
		return repository.findAll().stream().map(ClienteResponse::from).toList();
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<ClienteResponse> porId(@PathVariable Long id) {
		return repository.findById(id).map(ClienteResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/cpf/{cpf}")
	public ResponseEntity<ClienteResponse> porCpf(@PathVariable String cpf) {
		String digits = CpfUtil.normalize(cpf);
		if (!CpfUtil.isValidLength(digits)) {
			return ResponseEntity.badRequest().build();
		}
		return repository.findByCpf(digits).map(ClienteResponse::from).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/buscar")
	public List<ClienteResponse> porNome(@RequestParam String nome) {
		return repository.findByNomeContainingIgnoreCase(nome.trim()).stream().map(ClienteResponse::from).toList();
	}
}
