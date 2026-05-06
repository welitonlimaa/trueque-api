# Trueque -  API de Trocas

### 🌱 Sobre a Trueque API

**Trueque API** é uma plataforma de trocas de itens em que **não há envolvimento de dinheiro**. Seu objetivo é **promover a sustentabilidade**, a **reutilização consciente de bens** e estimular uma **economia colaborativa e ecológica**.

A ideia central é **evitar o desperdício**, **prolongar a vida útil dos objetos** e **incentivar conexões sociais significativas** por meio da troca. Qualquer item pode ser oferecido ou solicitado, valorizando o que já existe e reduzindo a necessidade de consumo excessivo.

Essa abordagem contribui para:

* Redução do impacto ambiental
* Estímulo à economia circular
* Fortalecimento de laços comunitários
* Reaproveitamento criativo de recursos

---
* Frontend: [https://github.com/welitonlimaa/trueque-frontend](https://github.com/welitonlimaa/trueque-frontend)
* Backend: [https://github.com/welitonlimaa/trueque-api](https://github.com/welitonlimaa/trueque-api)
* AiService: [https://github.com/welitonlimaa/trueque-aiservice](https://github.com/welitonlimaa/trueque-aiservice)
---

## Tecnologias Utilizadas

* Java 21
* Spring Boot 3.4.4
* Spring Security (JWT)
* Spring Data JPA
* PostgreSQL
* Springdoc OpenAPI (Swagger UI)
* Docker & Docker Compose
* Maven

---

## Funcionalidades

* Login com autenticação JWT
* Gerenciamento de usuários (Cadastro, Consulta, Atualização, Exclusão)
* Lógica de trocas entre usuários (em desenvolvimento)
* Segurança com Spring Security + BCrypt
* Integração com banco PostgreSQL
* Documentação automática via Swagger UI
* Build automatizado com Maven

---

## Estrutura do Projeto

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

## Como Rodar com Docker

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

A aplicação estará disponível em:
`http://localhost:8080`

---

## 📄 Documentação da API

A documentação interativa gerada automaticamente pelo Swagger está disponível em:

 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
ou
 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Nela, você pode explorar os endpoints, schemas, parâmetros e exemplos de uso.

---

## Endpoints Úteis

| Método | Rota                  | Descrição                       |
| ------ | --------------------- | ------------------------------- |
| POST   | `/auth/login`         | Login do usuário                |
| POST   | `/user/register`      | Cadastro de novo usuário        |
| GET    | `/user/{id}`          | Consulta de dados do usuário    |
| PUT    | `/user/{id}/data`     | Atualização de dados do usuário |
| PUT    | `/user/{id}/password` | Atualização da senha do usuário |
| DELETE | `/user/{id}`          | Exclusão do usuário             |

> Os endpoints acima exigem autenticação via JWT, exceto o de login e cadastro.

---

## Requisitos

* Java 21
* Docker + Docker Compose
* Maven

---

## 📌 Notas

* As senhas são armazenadas de forma segura com **BCrypt**.
* Os tokens JWT são gerados no login e devem ser incluídos no header `Authorization` como `Bearer <token>`.
* A API está em constante evolução, com novos recursos sendo adicionados como sistema de trocas e avaliações.

---
