package com.example.pecas.web;

import com.example.pecas.domain.Peca;

public record PecaResponse(Long id, String numeroIdentificacao, String nome, String descricao) {

	public static PecaResponse from(Peca p) {
		return new PecaResponse(p.getId(), p.getNumeroIdentificacao(), p.getNome(), p.getDescricao());
	}
}
