# 💼 Meu Saldo - Backend

Este é o backend da aplicação **Meu Saldo**, uma plataforma de gerenciamento financeiro pessoal. Desenvolvido com **Spring Boot**, fornece uma API REST para autenticação, controle de transações, cálculo de saldo e outras funcionalidades.

---

## ⚙️ Tecnologias utilizadas

- Java 21+
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Maven
- JWT (para autenticação)

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/meu-saldo-backend.git
cd meu-saldo-backend
```

### 2. Configure o banco de dados
#### src/main/resources/application.properties

```
spring.datasource.url=jdbc:mysql://localhost:3306/meusaldo
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
jwt.secret=segredoSuperSecreto
```

### 3. Execute o projeto
#### Com Maven:
```
./mvnw spring-boot:run
```

#### Com Gradle:
```
./gradlew bootRun
```
