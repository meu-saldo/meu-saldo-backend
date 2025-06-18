# 💼 Meu Saldo - Backend

Este é o backend da aplicação **Meu Saldo**, uma plataforma de gerenciamento financeiro pessoal. Desenvolvido com **Spring Boot**, fornece uma API REST para autenticação, controle de transações, cálculo de saldo e outras funcionalidades.

---

## ⚙️ Tecnologias utilizadas

- ☕ Java 21+
- 🚀 Spring Boot
- 🔐 Spring Security
- 🗄️ Spring Data JPA
- 🐬 MySQL
- 🔧 Maven
- 🔑 JWT (para autenticação)

---

## 🏗️ Pré-requisitos

- Java 21+
- Maven instalado
- MySQL rodando localmente (ou outro banco configurado)
- IDE de sua preferência (IntelliJ, VSCode, Eclipse, etc.)

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/meu-saldo-backend.git
cd meu-saldo-backend
```

### 2. Configure as variáveis de ambiente
#### Para proteger dados sensíveis como usuário e senha do banco e a chave JWT, a aplicação utiliza variáveis de ambiente.
Crie um arquivo .env na raiz do projeto (ou configure diretamente no ambiente da sua IDE ou terminal) com as seguintes variáveis:

```
DB_URL=jdbc:mysql://localhost:3306/meusaldo
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

### 3. Configure o arquivo application.properties
O arquivo já está preparado para usar variáveis de ambiente. Confira:

```
spring.application.name=meusaldo

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### 4. Execute o projeto
#### Com Maven:
```
./mvnw spring-boot:run
```
Ou
```
mvn spring-boot:run
```

### Com sua IDE:
- Importe o projeto como Maven Project.
- Rode a classe MeuSaldoApplication.java.
