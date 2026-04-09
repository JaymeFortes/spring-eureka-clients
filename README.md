# atividade-2-spring-eureka

Projeto de microsservicos com Spring Boot, Spring Cloud, Eureka, Config Server e API Gateway.

## Arquitetura

- `eureka-server`: Service Discovery (porta 8761)
- `config-server`: configuracoes centralizadas (porta 8888)
- `api-gateway`: entrada unica da aplicacao e pagina web de testes (porta 8080)
- `pecas-service`: CRUD de pecas (porta 8081)
- `clientes-service`: CRUD de clientes (porta 8082)
- `representantes-service`: CRUD de representantes (porta 8083)
- `prometheus`: coleta de metricas via Actuator (porta 9090)
- `grafana`: visualizacao de metricas (porta 3000)

Fluxo principal:

1. Os servicos sobem e buscam configuracao no `config-server`.
2. Os servicos se registram no `eureka-server`.
3. O `api-gateway` roteia chamadas para os servicos via `lb://`.
4. A interface web em `/index.html` consome as rotas do gateway.

## Banco de dados em memoria

Os servicos `pecas-service`, `clientes-service` e `representantes-service` usam:

- Spring Data JPA + Hibernate
- H2 em memoria (`jdbc:h2:mem:...`)
- `data.sql` com dados iniciais

Arquivos de carga inicial:

- `clientes-service/src/main/resources/data.sql`
- `pecas-service/src/main/resources/data.sql`
- `representantes-service/src/main/resources/data.sql`

## Como rodar com Docker Compose

### Pre-requisitos

- Docker
- Docker Compose (plugin `docker compose`)

### Subir tudo

No diretorio raiz do projeto:

```bash
docker compose up --build
```

Isso sobe os 6 containers da arquitetura.

Com a observabilidade habilitada, o Compose tambem sobe:

- Prometheus em http://localhost:9090/
- Grafana em http://localhost:3000/

Credenciais padrao do Grafana:

- usuario: `admin`
- senha: `admin`

### Parar tudo

```bash
docker compose down
```

## Endpoints principais

### Interfaces e infraestrutura

- Gateway UI: http://localhost:8080/
- Eureka Dashboard: http://localhost:8761/
- Config Server: http://localhost:8888/
- Prometheus: http://localhost:9090/
- Grafana: http://localhost:3000/

### APIs via Gateway

- Pecas: `GET/POST http://localhost:8080/api/pecas`
- Clientes: `GET/POST http://localhost:8080/api/clientes`
- Representantes: `GET/POST http://localhost:8080/api/representantes`

## Execucao local sem Docker (opcional)

No raiz:

```bash
mvn clean package -DskipTests
```
  
Depois iniciar os jars na ordem:

1. `eureka-server`
2. `config-server`
3. `pecas-service`, `clientes-service`, `representantes-service`
4. `api-gateway`

## Observacoes

- A configuracao foi ajustada para funcionar local e Docker com variaveis de ambiente:
  - `CONFIG_SERVER_URL`
  - `EUREKA_SERVER_URL`
- No Docker Compose, o gateway usa as variaveis abaixo para rotear direto na rede interna dos containers:
  - `PECAS_SERVICE_URL`
  - `CLIENTES_SERVICE_URL`
  - `REPRESENTANTES_SERVICE_URL`
- No Docker Compose, `SPRING_CLOUD_CONFIG_ENABLED=false` e aplicado nos servicos para evitar corrida de inicializacao com o config-server.
- Em Docker Compose, essas variaveis apontam para nomes de servico da rede interna.
- Os endpoints de metricas ficam disponiveis em `/actuator/prometheus` em todos os modulos Spring Boot.
