# Music API - Processo Seletivo SEPLAG MT

Este projeto é uma implementação de uma API REST para gerenciamento de artistas e álbuns musicais, desenvolvida como parte do Processo Seletivo Simplificado para o cargo de **Analista de Tecnologia da Informação - Engenheiro da Computação/SÊNIOR**.

## 🚀 Tecnologias Utilizadas

- **Java 11** (OpenJDK)
- **Spring Boot 2.7.18**
- **Spring Security** (JWT Authentication & Domain Filtering)
- **Spring Data JPA** (PostgreSQL)
- **Flyway** (Database Migrations)
- **MinIO** (Armazenamento de imagens compatível com S3)
- **WebSocket** (Notificações em tempo real)
- **SpringDoc OpenAPI** (Swagger UI)
- **Docker & Docker Compose**

## 🏗️ Arquitetura e Decisões de Projeto

A aplicação segue os princípios de **Clean Code** e **SOLID**, organizada em camadas:
- **Controller**: Endpoints versionados (`/api/v1/...`) com validação de entrada.
- **Service**: Lógica de negócio, integrações externas e notificações.
- **Repository**: Abstração de acesso a dados via Spring Data JPA.
- **DTO**: Objetos de transferência de dados para desacoplar a API do modelo de dados.
- **Security**: Filtros customizados para JWT, Rate Limiting e bloqueio por domínio.

### Estrutura de Dados
- **Artist**: Armazena nome e tipo (SINGER/BAND).
- **Album**: Armazena o título.
- **Artist_Album**: Tabela de junção para o relacionamento **N:N**.
- **Album_Image**: Metadados das imagens armazenadas no MinIO.
- **Regional**: Armazena dados sincronizados da API externa com controle de estado (ativo/inativo).

### Sincronização de Regionais
A lógica de sincronização implementada no `RegionalSyncService` garante:
1. **Inserção**: Novos registros da API externa são criados como ativos.
2. **Inativação**: Registros locais ativos que não constam mais na API externa são marcados como inativos.
3. **Atualização**: Se um atributo (nome) mudar, o registro antigo é inativado e um novo é criado, mantendo o histórico.

## 🔒 Segurança e Performance

- **JWT**: Autenticação com tokens de acesso (5 min) e refresh tokens.
- **Rate Limit**: Limitado a 10 requisições por minuto por usuário autenticado.
- **Domain Filter**: Bloqueio de requisições cujos headers `Origin` ou `Host` não correspondam ao domínio configurado.
- **Health Checks**: Endpoints `/actuator/health` (liveness/readiness) disponíveis.

## 🛠️ Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados.

### Passo a Passo
1. Clone o repositório.
2. Na raiz do projeto (onde está o `docker-compose.yml`), execute:
   ```bash
   docker-compose up -d
   ```
   *Nota: Se encontrar erro de conflito de nomes de containers, execute `docker-compose down` para limpar execuções anteriores.*
3. A API estará disponível em `http://localhost:8080`.
4. O Swagger UI pode ser acessado em `http://localhost:8080/swagger-ui.html`.

### Credenciais de Teste
- **Usuário**: `admin`
- **Senha**: `admin`

## 🧪 Testes

Para executar os testes unitários:
```bash
./mvnw test
```

## 📝 Documentação da API

A documentação completa dos endpoints (POST, PUT, GET, Upload, Sync) está disponível via Swagger.

### Principais Endpoints:
- `POST /api/v1/auth/login`: Obter token JWT.
- `GET /api/v1/artists`: Listar artistas com ordenação.
- `GET /api/v1/albums`: Listar álbuns com paginação.
- `POST /api/v1/albums/{id}/images`: Upload de capas.
- `POST /api/v1/regional-sync`: Sincronizar regionais.

---
**Candidato**: Luiz Henrique Martins Fanti

**Vaga**: Analista de TI - Engenheiro da Computação/SÊNIOR

**Data**: 05/02/2026
