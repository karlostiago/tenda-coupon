# 🎫 Tenda Coupon Management System

Sistema de gerenciamento de cupons desenvolvido com **arquitetura limpa**, seguindo as melhores práticas de **Domain-Driven Design (DDD)** e **orientação a objetos**.

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

## 🏗️ Arquitetura

### Estrutura de Camadas

```
src/
├── main/
│   └── java/br/com/tenda/coupon/
│       ├── domain/                    # Camada de Domínio (Regras de Negócio)
│       │   ├── Coupon.java           # Entidade de domínio
│       │   ├── exception/            # Exceções de domínio
│       │   ├── repository/           # Port (interface)
│       │   └── valueobject/          # Value Objects
│       │       ├── CouponCode.java
│       │       └── DiscountValue.java
│       │
│       ├── application/               # Camada de Aplicação (Casos de Uso)
│       │   └── usecase/
│       │       ├── CreateCoupon.java
│       │       ├── DeleteCoupon.java
│       │       └── FindCouponByCode.java
│       │
│       └── infrastructure/            # Camada de Infraestrutura (Adapters)
│           ├── api/                   # Controllers, DTOs
│           │   ├── controller/
│           │   ├── dto/
│           │   ├── mapper/
│           │   └── exception/
│           └── persistence/           # JPA, Repositories
│               ├── adapter/
│               ├── entity/
│               └── mapper/
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
- ✅ Caracteres especiais são **removidos automaticamente**
- ✅ Valor de desconto mínimo: **0.5** (sem máximo)
- ✅ Data de expiração **não pode ser no passado**
- ✅ Cupom pode ser criado como **já publicado**

### Deleção de Cupom (Delete)

- ✅ **Soft delete** - mantém os dados no banco
- ✅ Registra data de deleção
- ✅ **Não permite deletar cupom já deletado** (regra crítica!)

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.10**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok**
- **JUnit 5 + Mockito**
- **Swagger/OpenAPI**
- **Docker & Docker Compose**

## 🔧 Como Executar

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

### Opção 3: Build manual do JAR

```bash
# Build
.\gradlew build

# Execute
java -jar build/libs/coupon-0.0.1-SNAPSHOT.jar
```

## 📖 Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:coupondb`
  - Username: `sa`
  - Password: (vazio)
- **Health Check**: http://localhost:8080/actuator/health

## 🧪 Exemplos de Uso

### Criar um Cupom

```bash
POST /api/coupons
Content-Type: application/json

{
  "code": "SUM-MER",
  "description": "Summer sale discount",
  "discountValue": 10.50,
  "expirationDate": "2026-12-31",
  "published": true
}
```

**Resposta:**
```json
{
  "code": "SUMMER",
  "description": "Summer sale discount",
  "discountValue": 10.50,
  "expirationDate": "2026-12-31",
  "published": true,
  "deleted": false,
  "deletedAt": null
}
```

### Buscar Cupom

```bash
GET /api/coupons/SUMMER
```

### Deletar Cupom

```bash
DELETE /api/coupons/SUMMER
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
  - `CouponTest`
  - `CouponCodeTest`
  - `DiscountValueTest`

- **Testes de Casos de Uso**: Validam comportamento dos use cases
  - `CreateCouponTest`
  - `DeleteCouponTest`
  - `FindCouponByCodeTest`

- **Testes de Integração**: Validam integração com banco H2
  - `CouponControllerIntegrationTest`
  - `CouponRepositoryIntegrationTest`

- **Testes de Infraestrutura**: Validam adapters e mappers
  - `CouponRepositoryAdapterTest`
  - `CouponMapperTest`

### Cobertura de Testes

O projeto alcança **mais de 90% de cobertura**, testando:

- ✅ Todas as regras de negócio
- ✅ Cenários de sucesso e falha
- ✅ Validações de entrada
- ✅ Comportamento do banco de dados
- ✅ Integração end-to-end

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
@Component
class CreateCoupon {
    Coupon execute(Input input) { ... }
}

@Component
class DeleteCoupon {
    void execute(String code) { ... }
}
```

### 2. Value Objects com Validação

```java
public class CouponCode {
    private final String value;
    
    public CouponCode(String rawCode) {
        // Remove caracteres especiais
        // Valida 6 caracteres
        this.value = sanitizeAndValidate(rawCode);
    }
}
```

### 3. Domínio Rico

```java
public class Coupon {
    public void delete() {
        if (this.deleted) {
            throw new CouponAlreadyDeletedException(code);
        }
        this.deleted = true;
        this.deletedAt = LocalDate.now();
    }
}
```

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                    API Layer (Controllers)               │
├─────────────────────────────────────────────────────────┤
│              Application Layer (Use Cases)               │
│  - CreateCoupon  - DeleteCoupon  - FindCouponByCode     │
├─────────────────────────────────────────────────────────┤
│                  Domain Layer (Core)                     │
│  - Coupon  - CouponCode  - DiscountValue                │
├─────────────────────────────────────────────────────────┤
│            Infrastructure Layer (Adapters)               │
│  - JPA Repository  - Entity Mappers  - Database         │
└─────────────────────────────────────────────────────────┘
```

## 🔒 Qualidade de Código

- **Clean Code**: Código legível e expressivo
- **SOLID Principles**: Todos os princípios aplicados
- **DRY**: Sem duplicação de código
- **Separation of Concerns**: Cada camada com sua responsabilidade
- **Testabilidade**: Código 100% testável

## 📝 Licença

Este projeto é de código aberto e está disponível para fins educacionais.

## 👨‍💻 Autor

Desenvolvido como parte de um desafio técnico profissional.

---

**Nota**: Este projeto demonstra a aplicação de **arquitetura limpa**, **DDD**, e **boas práticas de orientação a objetos** em um contexto real de negócio.


