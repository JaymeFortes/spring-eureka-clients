package com.example.representantes.controllers;

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

import com.example.representantes.domain.Representante;
import com.example.representantes.domain.RepresentanteRepository;
import com.example.representantes.web.RepresentanteController;
import com.example.representantes.web.RepresentanteRequest;
import com.example.representantes.web.RepresentanteResponse;

@ExtendWith(MockitoExtension.class)
class RepresentanteControllerTest {

    @Mock
    private RepresentanteRepository repository;

    @InjectMocks
    private RepresentanteController controller;

    @Test
    void cadastrarDeveCriarRepresentanteQuandoCpfNovo() {
        RepresentanteRequest request = new RepresentanteRequest("123.456.789-00", "  Carlos  ");

        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(repository.save(any(Representante.class))).thenAnswer(invocation -> {
            Representante representante = invocation.getArgument(0);
            representante.setId(1L);
            return representante;
        });

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(RepresentanteResponse.class, response.getBody());
        RepresentanteResponse body = (RepresentanteResponse) response.getBody();
        assertNotNull(body);
        assertEquals("12345678900", body.cpf());
        assertEquals("Carlos", body.nome());
        verify(repository).save(any(Representante.class));
    }

    @Test
    void cadastrarDeveRetornarBadRequestQuandoCpfInvalido() {
        RepresentanteRequest request = new RepresentanteRequest("123", "Carlos");

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cadastrarDeveRetornarConflictQuandoCpfJaExiste() {
        RepresentanteRequest request = new RepresentanteRequest("12345678900", "Carlos");
        when(repository.findByCpf("12345678900")).thenReturn(Optional.of(new Representante()));

        ResponseEntity<?> response = controller.cadastrar(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void porNomeDeveBuscarComTrim() {
        when(repository.findByNomeContainingIgnoreCase("carlos")).thenReturn(List.of(
                Representante.builder().id(1L).cpf("12345678900").nome("Carlos").build()));

        List<RepresentanteResponse> response = controller.porNome("  carlos  ");

        assertEquals(1, response.size());
        assertEquals("Carlos", response.getFirst().nome());
    }

    @Test
    void porCpfDeveRetornarRepresentanteQuandoCpfExistir() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.of(
                Representante.builder().id(1L).cpf("12345678900").nome("Carlos").build()));

        ResponseEntity<RepresentanteResponse> response = controller.porCpf("123.456.789-00");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("12345678900", response.getBody().cpf());
        assertEquals("Carlos", response.getBody().nome());
    }

    @Test
    void porCpfDeveRetornarBadRequestQuandoCpfForInvalido() {
        ResponseEntity<RepresentanteResponse> response = controller.porCpf("123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void porCpfDeveRetornarNotFoundQuandoCpfNaoExistir() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());

        ResponseEntity<RepresentanteResponse> response = controller.porCpf("12345678900");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void porIdDeveRetornarRepresentanteQuandoIdExistir() {
        when(repository.findById(1L)).thenReturn(Optional.of(
                Representante.builder().id(1L).cpf("12345678900").nome("Carlos").build()));

        ResponseEntity<RepresentanteResponse> response = controller.porId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Carlos", response.getBody().nome());
    }

    @Test
    void porIdDeveRetornarNotFoundQuandoIdNaoExistir() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<RepresentanteResponse> response = controller.porId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
