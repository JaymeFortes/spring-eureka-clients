package com.example.pecas.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pecas.domain.Peca;
import com.example.pecas.domain.PecaRepository;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class PecaRepositoryIntegrationTest {

    @Autowired
    private PecaRepository repository;

    @Test
    void findByNumeroIdentificacaoDeveRetornarPecaPersistida() {
        Peca peca = repository.save(Peca.builder()
                .numeroIdentificacao("PEC-001")
                .nome("Parafuso")
                .descricao("aco")
                .build());

        Optional<Peca> result = repository.findByNumeroIdentificacao("PEC-001");

        assertTrue(result.isPresent());
        assertEquals(peca.getId(), result.get().getId());
    }

    @Test
    void findByNomeContainingIgnoreCaseDeveFiltrarPorTrecho() {
        repository.save(Peca.builder().numeroIdentificacao("PEC-002").nome("Parafuso Sextavado").descricao(null).build());
        repository.save(Peca.builder().numeroIdentificacao("PEC-003").nome("Arruela").descricao(null).build());

        List<Peca> result = repository.findByNomeContainingIgnoreCase("sexta");

        assertEquals(1, result.size());
        assertEquals("Parafuso Sextavado", result.getFirst().getNome());
    }
}
