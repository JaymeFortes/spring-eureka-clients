package com.example.representantes.web;

import com.example.representantes.domain.Representante;

public record RepresentanteResponse(Long id, String cpf, String nome) {

	public static RepresentanteResponse from(Representante r) {
		return new RepresentanteResponse(r.getId(), r.getCpf(), r.getNome());
	}
}
