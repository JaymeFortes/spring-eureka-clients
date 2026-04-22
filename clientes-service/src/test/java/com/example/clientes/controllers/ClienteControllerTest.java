package com.example.clientes.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.clientes.domain.Cliente;
import com.example.clientes.domain.ClienteRepository;
import com.example.clientes.web.ClienteController;
import com.example.clientes.web.ClienteRequest;
import com.example.clientes.web.ClienteResponse;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteController controller;

    @Test
    void cadastrarDeveCriarClienteQuandoCpfNovo() {
        ClienteRequest request = new ClienteRequest("123.456.789-00", "  Maria Silva  ");

        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(ClienteResponse.class, response.getBody());
        ClienteResponse body = (ClienteResponse) response.getBody();
        assertNotNull(body);
        assertEquals("12345678900", body.cpf());
        assertEquals("Maria Silva", body.nome());
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void cadastrarDeveRetornarBadRequestQuandoCpfInvalido() {
        ClienteRequest request = new ClienteRequest("123", "Joao");

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cadastrarDeveRetornarConflictQuandoCpfJaExiste() {
        ClienteRequest request = new ClienteRequest("12345678900", "Joao");
        when(repository.findByCpf("12345678900")).thenReturn(Optional.of(new Cliente()));

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void porNomeDeveBuscarComTrim() {
        when(repository.findByNomeContainingIgnoreCase("maria")).thenReturn(List.of(
                Cliente.builder().id(1L).cpf("12345678900").nome("Maria").build()));

        List<ClienteResponse> response = controller.porNome("  maria  ");

        assertEquals(1, response.size());
        assertEquals("Maria", response.getFirst().nome());
    }
}
