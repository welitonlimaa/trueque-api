# Trueque API

**Trueque API** é uma aplicação backend desenvolvida em **Java 21** com **Spring Boot**, utilizando **JWT** para autenticação, persistência de dados com **PostgreSQL**, e conteinerização com **Docker** para facilitar o ambiente de desenvolvimento.

---

## 📦 Tecnologias Utilizadas

- Java 21
- Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- Maven

---

## ⚙️ Funcionalidades

- 🔐 Login com autenticação JWT
- 👤 Gerenciamento de usuários (Cadastro, Login)
- 🛡️ Segurança com Spring Security + BCrypt
- 🐘 Integração com banco PostgreSQL
- 📦 Build automatizado com Maven

---

## 📁 Estrutura do Projeto

```
trueque-api/
│
├── src/
│   ├── main/
│   │   ├── java/com/trueque_api/staff/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── config/
│   │   │   ├── security/
│   │   │   └── dto/
│   │   └── resources/
│   │       └── application.properties
├── Dockerfile
├── docker-compose.yml
├── .env
└── README.md
```

---

## 🐳 Como Rodar com Docker

### 1. Crie um arquivo `.env` com as variáveis de ambiente:
```env
DB_USER=postgres
DB_PASSWORD=postgres
SEC_USER=admin
SEC_PASSWORD=admin
```

### 2. Suba a aplicação com Docker Compose:
```bash
docker-compose up --build
```

### 3. Acesse a API
A aplicação estará rodando em:  
👉 `http://localhost:8080`

---

## 🧪 Endpoints Úteis

| Método | Rota             | Descrição              |
|--------|------------------|------------------------|
| POST   | `/auth/login`    | Login do usuário       |

---

## ✅ Requisitos

- Java 21
- Docker + Docker Compose
- Maven

---

---

## 📌 Notas

- As senhas são armazenadas de forma segura com **BCrypt**.
- Os tokens JWT são gerados dinamicamente no login e devem ser usados para autenticação nas rotas protegidas.