package com.example.pecas.controllers;

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

import com.example.pecas.domain.Peca;
import com.example.pecas.domain.PecaRepository;
import com.example.pecas.web.PecaController;
import com.example.pecas.web.PecaRequest;
import com.example.pecas.web.PecaResponse;

@ExtendWith(MockitoExtension.class)
class PecaControllerTest {

    @Mock
    private PecaRepository repository;

    @InjectMocks
    private PecaController controller;

    @Test
    void cadastrarDeveCriarPecaQuandoNumeroNovo() {
        PecaRequest request = new PecaRequest(" PEC-001 ", "  Peca A  ", "  Desc  ");

        when(repository.findByNumeroIdentificacao(" PEC-001 ")).thenReturn(Optional.empty());
        when(repository.save(any(Peca.class))).thenAnswer(invocation -> {
            Peca peca = invocation.getArgument(0);
            peca.setId(1L);
            return peca;
        });

        ResponseEntity<PecaResponse> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(PecaResponse.class, response.getBody());
        assertNotNull(response.getBody());
        assertEquals("PEC-001", response.getBody().numeroIdentificacao());
        assertEquals("Peca A", response.getBody().nome());
        assertEquals("Desc", response.getBody().descricao());
        verify(repository).save(any(Peca.class));
    }

    @Test
    void cadastrarDeveRetornarConflictQuandoNumeroJaExiste() {
        PecaRequest request = new PecaRequest("PEC-001", "Peca", null);
        when(repository.findByNumeroIdentificacao("PEC-001")).thenReturn(Optional.of(new Peca()));

        ResponseEntity<PecaResponse> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void porNomeDeveBuscarComTrim() {
        when(repository.findByNomeContainingIgnoreCase("filtro")).thenReturn(List.of(
                Peca.builder().id(1L).numeroIdentificacao("PEC-01").nome("Filtro").descricao("x").build()));

        List<PecaResponse> response = controller.porNome("  filtro  ");

        assertEquals(1, response.size());
        assertEquals("Filtro", response.getFirst().nome());
    }
}
