# Backend Spring Boot - Assina Aqui

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

## Como executar

### 1. Instalar Maven (se não estiver instalado)

Baixe e instale o Maven do site oficial: https://maven.apache.org/download.cgi

Ou use o Chocolatey (se disponível):
```powershell
choco install maven
```

### 2. Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação será executada em: http://localhost:8080

## Endpoints da API

### Autenticação

#### Registro de usuário
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "password": "senha123"
}
```

#### Informações do usuário atual
```
GET /api/auth/me
Authorization: Bearer {token}
```

### Assinatura Digital

#### Assinar texto
```
POST /api/signatures/sign
Content-Type: application/json
Authorization: Bearer {token}

{
  "text": "Texto a ser assinado"
}
```

#### Listar minhas assinaturas
```
GET /api/signatures/my-signatures
Authorization: Bearer {token}
```

### Verificação Pública

#### Verificar assinatura por ID
```
GET /api/verify/{id}
```

#### Verificar assinatura por texto
```
POST /api/verify/text
Content-Type: application/json

{
  "id": "1",
  "text": "Texto original",
  "signature": "Valor da assinatura"
}
```

## Banco de Dados

A aplicação usa H2 Database em memória para desenvolvimento.

Console H2: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (vazio)

## Estrutura do Projeto

```
src/main/java/com/assinaaqui/backend/
├── config/          # Configurações (Security, JWT)
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── repository/     # Repositórios JPA
├── service/        # Serviços de negócio
└── AssinaAquiBackendApplication.java
```

## Funcionalidades Implementadas

✅ Registro de usuários com geração automática de chaves RSA
✅ Autenticação JWT
✅ Assinatura digital de textos com SHA-256
✅ Verificação pública de assinaturas
✅ Logs de verificação
✅ Proteção de rotas com Spring Security
✅ CORS configurado para frontend (localhost:3000)

## Testes

Para testar a integração completa:

1. Registre um usuário
2. Faça login e obtenha o token
3. Assine um texto usando o token
4. Verifique a assinatura usando o endpoint público
5. Verifique os logs no console H2