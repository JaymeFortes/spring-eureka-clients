package com.example.clientes.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
		@NotBlank @Size(max = 14) String cpf,
		@NotBlank @Size(max = 255) String nome) {
}
