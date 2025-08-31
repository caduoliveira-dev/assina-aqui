# Assina Aqui - Sistema de Assinatura Digital

Sistema completo de assinatura digital com frontend Next.js e backend Spring Boot.

## Pré-requisitos

### Para o Frontend:
- Node.js 18+ 
- npm, yarn, pnpm ou bun

### Para o Backend:
- Java 17+
- Maven 3.6+

## Como executar o projeto

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd assina-aqui
```

### 2. Executar o Backend

```bash
cd backend

# No Windows
.\mvnw.cmd spring-boot:run

# No Linux/Mac
./mvnw spring-boot:run
```

O backend será executado em: **http://localhost:8080**

### 3. Executar o Frontend

Em um novo terminal:

```bash
# Voltar para a raiz do projeto
cd ..

# Instalar dependências
npm install
# ou
yarn install
# ou
pnpm install

# Executar o servidor de desenvolvimento
npm run dev
# ou
yarn dev
# ou
pnpm dev
```

O frontend será executado em: **http://localhost:3000**

## Funcionalidades

### ✅ Autenticação
- Registro de usuários
- Login com JWT
- Geração automática de chaves RSA

### ✅ Assinatura Digital
- Assinatura de textos com SHA-256 + RSA
- Visualização do ID da assinatura
- Interface intuitiva

### ✅ Verificação
- Verificação pública de assinaturas
- Histórico de verificações
- Validação de integridade

## Estrutura do Projeto

```
assina-aqui/
├── backend/                 # Spring Boot API
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── src/                     # Next.js Frontend
│   ├── app/
│   │   ├── (private)/       # Rotas protegidas
│   │   └── (public)/        # Rotas públicas
│   └── components/
├── package.json
└── README.md
```

## Rotas da Aplicação

- `/` - Página inicial
- `/sign-up` - Registro
- `/sign-in` - Login
- `/signer` - Assinatura (protegida)
- `/verify` - Verificação pública

## API Endpoints

### Autenticação
- `POST /api/auth/register` - Registro
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Usuário atual

### Assinatura
- `POST /api/signatures/sign` - Assinar texto
- `GET /api/signatures/my-signatures` - Minhas assinaturas

### Verificação
- `GET /api/verify/{id}` - Verificar por ID

## Banco de Dados

O projeto usa **H2 Database** em memória para desenvolvimento:
- Console: http://localhost:8080/api/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (vazio)

## Configurações

### Backend (application.properties)
- Porta: 8080
- Context Path: /api
- CORS: habilitado para localhost:3000
- JWT Secret: configurado

### Frontend
- Porta: 3000
- API Base URL: http://localhost:8080

## Problemas Comuns

### Java não encontrado
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/path/to/java-17
```

### Porta em uso
- Backend: Altere `server.port` em `application.properties`
- Frontend: Use `npm run dev -- -p 3001`

### CORS Error
Verifique se o backend está rodando em localhost:8080 e o frontend em localhost:3000.

## Deploy

### Frontend
```bash
npm run build
npm start
```

### Backend
```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Tecnologias Utilizadas

### Frontend
- Next.js 15
- React 19
- TypeScript
- Tailwind CSS
- Radix UI

### Backend
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- JWT
- H2 Database
- Maven

## Segurança

- Senhas criptografadas com BCrypt
- Autenticação JWT
- Chaves RSA de 2048 bits
- Assinatura SHA-256
- CORS configurado
- Rotas protegidas

---

**Desenvolvido com Spring Boot + Next.js**
