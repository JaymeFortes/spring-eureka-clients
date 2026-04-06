package com.example.pecas.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PecaRequest(
		@NotBlank @Size(max = 64) String numeroIdentificacao,
		@NotBlank @Size(max = 255) String nome,
		@Size(max = 2000) String descricao) {
}
