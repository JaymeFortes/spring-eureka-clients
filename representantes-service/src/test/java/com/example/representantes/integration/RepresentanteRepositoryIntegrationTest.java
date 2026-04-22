package com.example.representantes.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.representantes.domain.Representante;
import com.example.representantes.domain.RepresentanteRepository;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class RepresentanteRepositoryIntegrationTest {

    @Autowired
    private RepresentanteRepository repository;

    @Test
    void findByCpfDeveRetornarRepresentantePersistido() {
        Representante representante = repository.save(Representante.builder().cpf("12345678900").nome("Carlos").build());

        Optional<Representante> result = repository.findByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals(representante.getId(), result.get().getId());
    }

    @Test
    void findByNomeContainingIgnoreCaseDeveFiltrarPorTrecho() {
        repository.save(Representante.builder().cpf("11111111111").nome("Carlos Silva").build());
        repository.save(Representante.builder().cpf("22222222222").nome("Joana").build());

        List<Representante> result = repository.findByNomeContainingIgnoreCase("silva");

        assertEquals(1, result.size());
        assertEquals("Carlos Silva", result.getFirst().getNome());
    }
}
