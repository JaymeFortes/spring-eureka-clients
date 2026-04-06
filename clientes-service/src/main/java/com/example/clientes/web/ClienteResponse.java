package com.example.clientes.web;

import com.example.clientes.domain.Cliente;

public record ClienteResponse(Long id, String cpf, String nome) {

	public static ClienteResponse from(Cliente c) {
		return new ClienteResponse(c.getId(), c.getCpf(), c.getNome());
	}
}
