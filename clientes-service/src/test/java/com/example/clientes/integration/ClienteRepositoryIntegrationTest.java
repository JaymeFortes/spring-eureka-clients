package com.example.clientes.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.clientes.domain.Cliente;
import com.example.clientes.domain.ClienteRepository;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class ClienteRepositoryIntegrationTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    void findByCpfDeveRetornarClientePersistido() {
        Cliente cliente = repository.save(Cliente.builder().cpf("12345678900").nome("Maria").build());

        Optional<Cliente> result = repository.findByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals(cliente.getId(), result.get().getId());
    }

    @Test
    void findByNomeContainingIgnoreCaseDeveFiltrarPorTrecho() {
        repository.save(Cliente.builder().cpf("11111111111").nome("Ana Clara").build());
        repository.save(Cliente.builder().cpf("22222222222").nome("Bruno").build());

        List<Cliente> result = repository.findByNomeContainingIgnoreCase("clara");

        assertEquals(1, result.size());
        assertEquals("Ana Clara", result.getFirst().getNome());
    }
}
