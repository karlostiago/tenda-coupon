# 🎫 Tenda Coupon - Sistema de Gerenciamento de Cupons

> Sistema profissional de gerenciamento de cupons de desconto construído com **Clean Architecture**, **Domain-Driven Design** e **boas práticas de desenvolvimento**.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Test Coverage](https://img.shields.io/badge/coverage-%3E90%25-success.svg)](build/reports/tests/test/index.html)
[![Architecture](https://img.shields.io/badge/architecture-Clean%20%2B%20Hexagonal-blue.svg)]()

Um projeto showcase que demonstra a aplicação rigorosa de princípios de arquitetura limpa, separação de responsabilidades e desenvolvimento orientado a testes (TDD) em um contexto real de negócio.

```
🏗️  Clean Architecture      📚  Domain-Driven Design    🧪  90%+ Test Coverage
🎯  4 Use Cases             💎  5 Value Objects         🐳  Docker Ready
🔄  Soft Delete             📖  OpenAPI/Swagger         ⚡  Spring Boot 3.5
```

---

## 📑 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Diferenciais do Projeto](#-diferenciais-do-projeto)
- [Tecnologias](#-tecnologias)
- [Estatísticas do Projeto](#-estatísticas-do-projeto)
- [Arquitetura](#-arquitetura)
- [Regras de Negócio](#-regras-de-negócio)
- [Como Executar](#-como-executar)
- [Documentação da API](#-documentação-da-api)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Testes](#-testes)
- [Clean Architecture na Prática](#-clean-architecture-na-prática)
- [Decisões Arquiteturais](#-decisões-arquiteturais)
- [Qualidade de Código](#-qualidade-de-código)
- [Troubleshooting](#-troubleshooting)
- [Possíveis Melhorias Futuras](#-possíveis-melhorias-futuras)
- [Aprendizados e Destaques Técnicos](#-aprendizados-e-destaques-técnicos)

---

## 🎯 Sobre o Projeto

Este projeto foi desenvolvido como um desafio técnico profissional, demonstrando:

- ✅ **Arquitetura Limpa** com separação clara de responsabilidades
- ✅ **Domain-Driven Design** com regras de negócio encapsuladas no domínio
- ✅ **Casos de Uso focados** - eliminando "Services genéricos"
- ✅ **Value Objects** para garantir invariantes de negócio
- ✅ **Testes abrangentes** com cobertura > 90%
- ✅ **Integração completa** com banco H2
- ✅ **Documentação API** com Swagger/OpenAPI
- ✅ **Containerização** com Docker

## ✨ Diferenciais do Projeto

### Arquitetura & Design
- 🏛️ **Hexagonal Architecture (Ports & Adapters)** com domínio isolado
- 🎯 **Use Cases específicos** ao invés de Services genéricos
- 💎 **Rich Domain Model** com lógica de negócio encapsulada
- 🔐 **Value Objects imutáveis** com validações fail-fast
- 🔄 **Soft Delete inteligente** com controle de estado via enum

### Qualidade & Testes
- ✅ **+90% de cobertura de testes** em todas as camadas
- 🧪 **Testes organizados com Nested Classes** para melhor legibilidade
- 🔍 **AssertJ** para assertions expressivas e fluentes
- 🎭 **Testes unitários isolados** com mocks
- 🌐 **Testes de integração** com contexto Spring completo

### API & Documentação
- 📚 **OpenAPI 3.0** com documentação interativa completa
- 🎨 **Interface segregada** (CouponApi) para contrato claro da API
- 🚨 **Tratamento global de exceções** com mensagens descritivas
- 📊 **Paginação configurável** para listagens eficientes
- 💚 **Health Check** configurado via Spring Actuator

### Boas Práticas
- 🎯 **SOLID principles** rigorosamente aplicados
- 🧹 **Clean Code** com nomes expressivos e métodos coesos
- 🔒 **Imutabilidade** em Value Objects e DTOs
- ⚡ **Bean Validation** (Jakarta Validation) na camada de apresentação
- 🐳 **Docker multi-stage build** otimizado

## 🏗️ Arquitetura

### Estrutura de Camadas

```
src/
├── main/
│   └── java/br/com/tenda/coupon/
│       ├── domain/                       # Camada de Domínio (Regras de Negócio)
│       │   ├── model/
│       │   │   └── Coupon.java          # Entidade de domínio
│       │   ├── exception/               # Exceções de domínio
│       │   │   ├── InvalidCouponException.java
│       │   │   ├── CouponAlreadyDeletedException.java
│       │   │   ├── CouponNotFoundException.java
│       │   │   └── CouponStatusException.java
│       │   ├── repository/              # Port (interface)
│       │   │   └── CouponRepository.java
│       │   └── vo/                      # Value Objects
│       │       ├── CouponCode.java
│       │       ├── CouponDescription.java
│       │       ├── CouponDiscount.java
│       │       ├── CouponExpirationDate.java
│       │       └── CouponStatus.java
│       │
│       ├── application/                  # Camada de Aplicação (Casos de Uso)
│       │   └── usecase/
│       │       ├── CreateCouponUseCase.java
│       │       ├── DeleteCouponUseCase.java
│       │       ├── GetCouponByIdUseCase.java
│       │       └── GetAllCouponsUseCase.java
│       │
│       ├── presentation/                 # Camada de Apresentação (API)
│       │   ├── controller/
│       │   │   ├── CouponApi.java       # Interface OpenAPI
│       │   │   └── CouponController.java
│       │   ├── dto/
│       │   │   ├── CreateCouponRequest.java
│       │   │   ├── CouponResponse.java
│       │   │   └── ErrorResponse.java
│       │   └── mapper/
│       │       └── CouponMapper.java
│       │
│       └── infrastructure/               # Camada de Infraestrutura (Adapters)
│           ├── config/
│           │   └── OpenApiConfig.java
│           ├── exception/
│           │   └── GlobalExceptionHandler.java
│           └── persistence/             # JPA, Repositories
│               ├── CouponH2DatabaseAdapter.java
│               ├── SpringDataCouponRepository.java
│               └── entity/
│                   └── CouponEntity.java
```

### Princípios Aplicados

- **Single Responsibility Principle**: Cada classe tem uma única responsabilidade
- **Dependency Inversion**: Domínio não depende de infraestrutura
- **Open/Closed Principle**: Aberto para extensão, fechado para modificação
- **Use Cases focados**: Cada caso de uso representa uma intenção clara do usuário

## 📋 Regras de Negócio

### Criação de Cupom (Create)

- ✅ Campos obrigatórios: `code`, `description`, `discountValue`, `expirationDate`
- ✅ Código deve ter **exatamente 6 caracteres alfanuméricos**
- ✅ Caracteres especiais são **removidos automaticamente** (ex: "ABC-123" vira "ABC123")
- ✅ Código é convertido automaticamente para **maiúsculas**
- ✅ Valor de desconto mínimo: **0.5** (sem máximo)
- ✅ Data de expiração **não pode ser no passado**
- ✅ Cupom pode ser criado como **já publicado** (campo `published`)
- ✅ Cupom pode ser criado como **resgatado** (campo `redeemed`)
- ✅ Cupom criado com status **ACTIVE** por padrão
- ✅ **Não permite criar cupom com código duplicado**

### Consulta de Cupons

- ✅ Busca por **UUID** (identificador único)
- ✅ Listagem **paginada** de todos os cupons
- ✅ Paginação configurável (padrão: 20 itens por página)
- ✅ Retorna todos os dados incluindo status (ACTIVE, INACTIVE, DELETED)

### Deleção de Cupom (Delete)

- ✅ **Soft delete** - mantém os dados no banco
- ✅ Altera o status para **DELETED**
- ✅ **Não permite deletar cupom já deletado** (regra crítica!)
- ✅ Utiliza UUID para identificação

### Status do Cupom

O sistema trabalha com 3 estados:
- **ACTIVE**: Cupom ativo e disponível para uso
- **INACTIVE**: Cupom inativo (não implementado na versão atual)
- **DELETED**: Cupom deletado (soft delete)

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.10**
- **Spring Data JPA**
- **Spring Boot Validation**
- **Spring Boot Actuator** (Health Check & Monitoring)
- **H2 Database** (in-memory)
- **Lombok**
- **JUnit 5 + Mockito + AssertJ**
- **SpringDoc OpenAPI 2.7.0** (Swagger UI)
- **Docker & Docker Compose**

## 📊 Estatísticas do Projeto

```
📁 Arquivos Java:           39 classes
📝 Linhas de Código:        3.083 linhas
   ├─ Código Principal:     783 linhas
   └─ Código de Testes:     2.300 linhas
   
📊 Proporção:              2.94:1 (teste/código)
🎯 Cobertura:              >90%
🧪 Casos de Teste:         45+ cenários
```

**Distribuição por Camada:**
- **Domain**: 5 VOs + 1 Entidade + 4 Exceções + 1 Enum + 1 Interface
- **Application**: 4 Use Cases
- **Presentation**: 2 Controllers + 3 DTOs + 1 Mapper
- **Infrastructure**: 3 Adapters + 1 Entity + 2 Configs + 1 Exception Handler

## 🏗️ Arquitetura

### Pré-requisitos

- Java 17+
- Docker (opcional)

### Opção 1: Executar localmente

```bash
# Clone o repositório
git clone <repository-url>
cd tenda-coupon

# Execute com Gradle (Windows)
.\gradlew bootRun

# Execute com Gradle (Linux/Mac)
./gradlew bootRun
```

### Opção 2: Executar com Docker

```bash
# Build e execute com Docker Compose
docker-compose up --build

# Parar containers
docker-compose down
```

### 📌 Comandos Úteis

```bash
# Executar testes
./gradlew test             # Linux/Mac
.\gradlew test             # Windows

# Build do projeto
./gradlew build

# Executar sem testes
./gradlew build -x test

# Limpar build
./gradlew clean

# Ver relatório de testes (HTML gerado em build/reports/tests/test/index.html)
./gradlew test --info

# Ver logs do container Docker
docker-compose logs -f coupon-api

# Rebuild completo do Docker
docker-compose up --build --force-recreate
```

## 📖 Documentação da API

```bash
# Build
.\gradlew build

# Execute
java -jar build/libs/coupon-0.0.1-SNAPSHOT.jar
```

## 📖 Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:9090/swagger-ui.html
- **OpenAPI JSON**: http://localhost:9090/api-docs
- **H2 Console**: http://localhost:9090/h2-console
  - JDBC URL: `jdbc:h2:mem:coupondb`
  - Username: `sa`
  - Password: (vazio)
- **Health Check**: http://localhost:9090/actuator/health

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/coupons` | Criar novo cupom |
| GET | `/api/coupons/{id}` | Buscar cupom por ID |
| GET | `/api/coupons?page=0&size=20` | Listar todos os cupons (paginado) |
| DELETE | `/api/coupons/{id}` | Deletar cupom (soft delete) |

## 🧪 Exemplos de Uso

### Criar um Cupom

```bash
POST /api/coupons
Content-Type: application/json

{
  "code": "ABC-123",
  "description": "Summer sale discount",
  "discountValue": 10.50,
  "expirationDate": "2026-12-31T23:59:59",
  "published": true,
  "redeemed": false
}
```

**Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "ABC123",
  "description": "Summer sale discount",
  "discountValue": 10.50,
  "expirationDate": "2026-12-31T23:59:59",
  "published": true,
  "redeemed": false,
  "status": "ACTIVE"
}
```

### Buscar Cupom por ID

```bash
GET /api/coupons/{id}
```

**Exemplo:**
```bash
GET /api/coupons/550e8400-e29b-41d4-a716-446655440000
```

### Listar Todos os Cupons (Paginado)

```bash
GET /api/coupons?page=0&size=20
```

**Resposta:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "ABC123",
      "description": "Summer sale discount",
      "discountValue": 10.50,
      "expirationDate": "2026-12-31T23:59:59",
      "published": true,
      "redeemed": false,
      "status": "ACTIVE"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Deletar Cupom

```bash
DELETE /api/coupons/{id}
```

**Exemplo:**
```bash
DELETE /api/coupons/550e8400-e29b-41d4-a716-446655440000
```

**Resposta:** 204 No Content

### ⚠️ Exemplos de Respostas de Erro

#### Erro 400 - Validação de Dados

```bash
POST /api/coupons
{
  "code": "ABC",  # Código muito curto
  "description": "",
  "discountValue": 0.3,
  "expirationDate": "2025-01-01T00:00:00"
}
```

**Resposta:**
```json
{
  "timestamp": "2026-02-16T20:32:17",
  "status": 400,
  "error": "Bad Request",
  "message": "Coupon code must have exactly 6 alphanumeric characters (after removing special characters)",
  "path": "/api/coupons"
}
```

#### Erro 404 - Cupom Não Encontrado

```bash
GET /api/coupons/00000000-0000-0000-0000-000000000000
```

**Resposta:**
```json
{
  "timestamp": "2026-02-16T20:32:17",
  "status": 404,
  "error": "Not Found",
  "message": "Coupon not found with id: 00000000-0000-0000-0000-000000000000",
  "path": "/api/coupons/00000000-0000-0000-0000-000000000000"
}
```

#### Erro 409 - Cupom Duplicado

```bash
POST /api/coupons
{
  "code": "ABC123",  # Código já existe
  "description": "Test",
  "discountValue": 10.5,
  "expirationDate": "2026-12-31T23:59:59"
}
```

**Resposta:**
```json
{
  "timestamp": "2026-02-16T20:32:17",
  "status": 400,
  "error": "Bad Request",
  "message": "A coupon with this code already exists",
  "path": "/api/coupons"
}
```

#### Erro 409 - Cupom Já Deletado

```bash
DELETE /api/coupons/550e8400-e29b-41d4-a716-446655440000
```

**Resposta (se já deletado):**
```json
{
  "timestamp": "2026-02-16T20:32:17",
  "status": 409,
  "error": "Conflict",
  "message": "Coupon with id 550e8400-e29b-41d4-a716-446655440000 is already deleted",
  "path": "/api/coupons/550e8400-e29b-41d4-a716-446655440000"
}
```

## 🧪 Testes

O projeto possui **testes abrangentes** em todas as camadas:

### Executar todos os testes

```bash
.\gradlew test
```

### Executar com relatório de cobertura

```bash
.\gradlew test jacocoTestReport
```

### Estrutura de Testes

- **Testes de Domínio**: Validam todas as regras de negócio
  - `CouponTest` - Testes da entidade Coupon
  - `CouponCodeTest` - Validação de código
  - `CouponDescriptionTest` - Validação de descrição
  - `CouponDiscountTest` - Validação de desconto
  - `CouponExpirationDateTest` - Validação de data de expiração

- **Testes de Casos de Uso**: Validam comportamento dos use cases
  - `CreateCouponUseCaseTest` - Criação de cupons
  - `DeleteCouponUseCaseTest` - Deleção de cupons
  - `GetCouponByIdUseCaseTest` - Busca por ID
  - `GetAllCouponsUseCaseTest` - Listagem paginada

- **Testes de Integração**: Validam integração com banco H2
  - `CouponControllerIntegrationTest` - Testes end-to-end da API

- **Testes de Infraestrutura**: Validam adapters
  - `CouponRepositoryAdapterTest` - Adapter do repositório

### Cobertura de Testes

O projeto alcança **cobertura superior a 90%**, testando:

- ✅ Todas as regras de negócio
- ✅ Cenários de sucesso e falha
- ✅ Validações de entrada
- ✅ Comportamento do banco de dados
- ✅ Integração end-to-end

**Destaques dos Testes:**
- Testes de domínio com **Nested Classes** para melhor organização
- Uso de **AssertJ** para assertions fluentes e legíveis
- Testes parametrizados para múltiplos cenários
- Testes de integração com contexto Spring completo
- Validação de exceções e mensagens de erro

## 🎨 Destaques da Implementação

### 1. Eliminação de "Services Genéricos"

❌ **Evitado:**
```java
@Service
class CouponService {
    void create() {}
    void update() {}
    void delete() {}
    void sendEmail() {}
    void generateReport() {}
    // 20 métodos diferentes...
}
```

✅ **Implementado:**
```java
@Service
class CreateCouponUseCase {
    Coupon execute(String code, String description, 
                   BigDecimal discountValue, 
                   LocalDateTime expirationDate, 
                   boolean published) { ... }
}

@Service
class DeleteCouponUseCase {
    void execute(UUID id) { ... }
}

@Service
class GetCouponByIdUseCase {
    Coupon execute(UUID id) { ... }
}

@Service
class GetAllCouponsUseCase {
    Page<Coupon> execute(Pageable pageable) { ... }
}
```

### 2. Value Objects com Validação

Os Value Objects encapsulam as regras de validação de cada campo:

**CouponCode**: Remove caracteres especiais, valida 6 caracteres, converte para maiúsculas
```java
public class CouponCode {
    private final String value;
    
    public static CouponCode from(String rawCode) {
        // Remove caracteres especiais: "ABC-123" -> "ABC123"
        // Valida exatamente 6 caracteres alfanuméricos
        // Converte para maiúsculas
        return new CouponCode(cleanedCode.toUpperCase());
    }
}
```

**CouponDiscount**: Valida valor mínimo de 0.5
```java
public class CouponDiscount {
    public CouponDiscount(BigDecimal value) {
        if (value.compareTo(new BigDecimal("0.5")) < 0) {
            throw new InvalidCouponException("Discount value must be at least 0.5");
        }
        this.value = value;
    }
}
```

**CouponExpirationDate**: Valida que a data não está no passado
```java
public class CouponExpirationDate {
    public CouponExpirationDate(LocalDateTime value) {
        if (value.isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past");
        }
        this.value = value;
    }
}
```

### 3. Domínio Rico

```java
public class Coupon {
    private String status;
    
    public void delete() {
        if (CouponStatus.DELETED.name().equals(this.status)) {
            throw new CouponAlreadyDeletedException("Coupon with id " + this.id + " is already deleted");
        }
        this.status = CouponStatus.DELETED.name();
    }
    
    public static Coupon create(String code, String description, BigDecimal discountValue, 
                                LocalDateTime expirationDate, boolean published, boolean redeemed) {
        return new Coupon(UUID.randomUUID(), CouponCode.from(code), description, 
                         discountValue, expirationDate, published, redeemed, CouponStatus.ACTIVE);
    }
}
```

### 4. Ports and Adapters (Hexagonal Architecture)

**Port (Interface no domínio):**
```java
public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
    boolean existsByCode(String code);
    Page<Coupon> findAll(Pageable pageable);
}
```

**Adapter (Implementação na infraestrutura):**
```java
@Component
@RequiredArgsConstructor
public class CouponH2DatabaseAdapter implements CouponRepository {
    private final SpringDataCouponRepository springRepository;
    private final CouponEntityMapper mapper;
    
    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = mapper.toEntity(coupon);
        CouponEntity savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    // ...outras implementações
}
```

### 5. Tratamento de Exceções Centralizado

O sistema possui um `GlobalExceptionHandler` que converte exceções de domínio em respostas HTTP apropriadas:

| Exceção de Domínio | Status HTTP | Descrição |
|-------------------|-------------|-----------|
| `InvalidCouponException` | 400 Bad Request | Dados inválidos ou violação de regra |
| `CouponNotFoundException` | 404 Not Found | Cupom não encontrado |
| `CouponAlreadyDeletedException` | 409 Conflict | Tentativa de deletar cupom já deletado |
| `CouponStatusException` | 400 Bad Request | Status inválido |

**Formato de resposta de erro:**
```json
{
  "timestamp": "2026-02-16T20:32:17",
  "status": 400,
  "error": "Bad Request",
  "message": "Coupon code must have exactly 6 alphanumeric characters",
  "path": "/api/coupons"
}
```

### 6. Documentação OpenAPI Completa

A interface `CouponApi` utiliza anotações do SpringDoc para gerar documentação detalhada:

```java
@Tag(name = "Coupons", description = "API para gerenciamento de cupons de desconto")
public interface CouponApi {
    
    @Operation(summary = "Criar novo cupom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Cupom duplicado")
    })
    ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request);
}
```

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│              Presentation Layer (Controllers & DTOs)            │
│  - CouponApi (Interface)  - CouponController                    │
│  - CreateCouponRequest  - CouponResponse  - ErrorResponse       │
├─────────────────────────────────────────────────────────────────┤
│              Application Layer (Use Cases)                      │
│  - CreateCouponUseCase  - DeleteCouponUseCase                   │
│  - GetCouponByIdUseCase  - GetAllCouponsUseCase                 │
├─────────────────────────────────────────────────────────────────┤
│                  Domain Layer (Core)                            │
│  - Coupon (Entity)                                              │
│  - CouponCode, CouponDescription, CouponDiscount,               │
│    CouponExpirationDate, CouponStatus (Value Objects)           │
│  - CouponRepository (Port)                                      │
│  - Domain Exceptions                                            │
├─────────────────────────────────────────────────────────────────┤
│            Infrastructure Layer (Adapters)                      │
│  - CouponH2DatabaseAdapter (Adapter)                            │
│  - SpringDataCouponRepository (JPA)                             │
│  - CouponEntity  - OpenApiConfig  - GlobalExceptionHandler      │
└─────────────────────────────────────────────────────────────────┘
```

## 🎯 Decisões Arquiteturais

### Separação de Camadas

- **Domain Layer**: Totalmente independente de frameworks. Contém apenas lógica de negócio pura
- **Application Layer**: Orquestra casos de uso sem conhecer detalhes de infraestrutura
- **Presentation Layer**: Controllers e DTOs separados do domínio
- **Infrastructure Layer**: Implementações específicas (JPA, H2, Configs)

### Identificação por UUID

Optou-se por usar UUID como identificador ao invés do código do cupom por:
- **Segurança**: UUIDs são imprevisíveis e não sequenciais
- **Escalabilidade**: Geração distribuída sem conflitos
- **Separação de Conceitos**: O código é uma propriedade de negócio, não um identificador técnico
- **Flexibilidade**: Permite mudanças no código sem impactar referências

### Soft Delete com Status

Implementação via enum `CouponStatus` ao invés de campos booleanos:
- **Extensibilidade**: Fácil adicionar novos status (EXPIRED, SUSPENDED, etc.)
- **Auditoria**: Histórico completo do ciclo de vida
- **Regras de Negócio**: Validações específicas por status
- **Consultas**: Filtros mais expressivos e performáticos

### Value Objects Imutáveis

Todos os Value Objects são imutáveis (`final` fields, sem setters):
- **Thread-Safety**: Seguros para uso concorrente
- **Previsibilidade**: Estado não pode ser corrompido após criação
- **Hash Codes Estáveis**: Seguros para uso em coleções
- **Validação no Construtor**: Garantia de invariantes desde a criação

## 🔒 Qualidade de Código

- **Clean Code**: Código legível e expressivo com nomes significativos
- **SOLID Principles**: Todos os princípios rigorosamente aplicados
- **DRY**: Sem duplicação de código ou lógica
- **Separation of Concerns**: Cada camada com responsabilidade bem definida
- **Testabilidade**: Código 100% testável com baixo acoplamento
- **Immutability**: Value Objects e DTOs imutáveis
- **Fail-Fast**: Validações no momento da criação dos objetos

## 🔧 Troubleshooting

### Problema: Porta 9090 já está em uso

```bash
# Windows - Encontrar processo na porta 9090
netstat -ano | findstr :9090

# Windows - Matar processo (substitua <PID> pelo ID do processo)
taskkill /PID <PID> /F

# Linux/Mac - Encontrar e matar processo
lsof -ti:9090 | xargs kill -9
```

### Problema: Gradle build falha

```bash
# Limpar cache do Gradle
.\gradlew clean --refresh-dependencies

# Excluir pasta .gradle e rebuildar
rm -rf .gradle
.\gradlew build
```

### Problema: Testes falhando com erro de data

Os testes validam datas futuras. Se estiver executando testes muito antigos, algumas datas podem ter "expirado". Os testes usam datas relativas (`LocalDateTime.now().plusDays(30)`), então isso normalmente não deve ocorrer.

### Problema: Docker build falha

```bash
# Limpar imagens e containers antigos
docker system prune -a

# Rebuild forçado
docker-compose build --no-cache
docker-compose up
```

### Problema: H2 Console não abre

Verifique se:
1. A aplicação está rodando (`http://localhost:9090/actuator/health` deve retornar `UP`)
2. URL do H2 está correta: `http://localhost:9090/h2-console`
3. JDBC URL no H2 Console: `jdbc:h2:mem:coupondb`
4. Username: `sa` (senha em branco)

## 🚀 Possíveis Melhorias Futuras

### Funcionalidades
- [ ] **Update de Cupom**: Adicionar caso de uso para atualizar cupons existentes
- [ ] **Busca por Código**: Endpoint para buscar cupom pelo código
- [ ] **Filtros Avançados**: Busca por status, data de expiração, valor de desconto
- [ ] **Ativação/Inativação**: Implementar transições ACTIVE ↔ INACTIVE
- [ ] **Expiração Automática**: Job scheduled para marcar cupons expirados
- [ ] **Histórico de Alterações**: Audit trail completo com Event Sourcing

### Infraestrutura
- [ ] **PostgreSQL**: Migrar de H2 para banco de produção
- [ ] **Redis Cache**: Cache distribuído para consultas frequentes
- [ ] **Observabilidade**: Integração com Prometheus + Grafana
- [ ] **Distributed Tracing**: OpenTelemetry para rastreamento
- [ ] **CI/CD Pipeline**: GitHub Actions ou Jenkins
- [ ] **Kubernetes**: Deployment manifests para orquestração

### Segurança
- [ ] **Spring Security**: Autenticação e autorização
- [ ] **JWT Tokens**: Autenticação stateless
- [ ] **Rate Limiting**: Proteção contra abuso de API
- [ ] **CORS Configuration**: Configuração adequada para produção

### Performance
- [ ] **Índices de Banco**: Otimização de consultas
- [ ] **Lazy Loading**: Otimização de carregamento de entidades
- [ ] **Query Optimization**: Análise e otimização de N+1 queries
- [ ] **Connection Pool Tuning**: Ajuste fino do HikariCP

## 🎓 Aprendizados e Destaques Técnicos

### O que este projeto demonstra:

✅ **Arquitetura Escalável**: Estrutura que suporta crescimento sem refatoração massiva  
✅ **Código Testável**: Baixo acoplamento permite testes unitários isolados  
✅ **Manutenibilidade**: Código limpo e organizado facilita manutenção  
✅ **Separação de Conceitos**: Cada camada com responsabilidade única e clara  
✅ **Domínio Protegido**: Regras de negócio isoladas de frameworks  
✅ **Validações Robustas**: Fail-fast com Value Objects imutáveis  
✅ **API Profissional**: Documentação completa e padrões REST  
✅ **Qualidade de Código**: SOLID, Clean Code e DRY aplicados consistentemente  

### Padrões e Práticas Implementados:

- 🏛️ **Hexagonal Architecture** (Ports & Adapters)
- 📚 **Domain-Driven Design** (Entities, Value Objects, Repositories)
- 🎯 **Use Case Pattern** (Application Services)
- 🏭 **Factory Pattern** (Criação de entidades)
- 🔄 **Repository Pattern** (Abstração de persistência)
- 🗺️ **Mapper Pattern** (Conversão entre camadas)
- ⚡ **DTO Pattern** (Transferência de dados)
- 🚨 **Exception Handler Pattern** (Tratamento centralizado)

### Qualidades do Projeto:

| Aspecto | Implementação |
|---------|---------------|
| **Arquitetura** | Clean Architecture + Hexagonal |
| **Cobertura de Testes** | >90% com testes unitários + integração |
| **Documentação** | OpenAPI 3.0 completa e interativa |
| **Padrões de Código** | SOLID, Clean Code, DRY |
| **Validações** | Fail-fast com Value Objects |
| **Exceções** | Tratamento centralizado e tipado |
| **API** | RESTful com paginação |
| **Containerização** | Docker multi-stage otimizado |

## 📝 Licença

Este projeto é de código aberto e está disponível para fins educacionais.

## 👨‍💻 Autor

Desenvolvido como parte de um desafio técnico profissional.

---

**Nota**: Este projeto demonstra a aplicação de **arquitetura limpa**, **DDD**, e **boas práticas de orientação a objetos** em um contexto real de negócio.


