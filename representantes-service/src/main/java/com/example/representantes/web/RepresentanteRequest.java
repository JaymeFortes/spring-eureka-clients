package com.example.representantes.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RepresentanteRequest(
		@NotBlank @Size(max = 14) String cpf,
		@NotBlank @Size(max = 255) String nome) {
}
