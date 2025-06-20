# 💼 Meu Saldo - Backend

![Java CI](https://github.com/meu-saldo/meu-saldo-backend/actions/workflows/ci.yml/badge.svg?branch=dev)

Backend da aplicação **Meu Saldo**, uma plataforma de gerenciamento financeiro pessoal.  
Desenvolvido com **Spring Boot**, oferece uma API REST para:

- 🧾 Gerenciamento de transações
- 💰 Cálculo de saldo
- 👤 Gerenciamento de usuários
- 🏦 Gerenciamento de contas bancárias
- 🔐 Autenticação e segurança (em breve)

---

## 🧰 Tecnologias utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot**
- 🔐 **Spring Security** (configurado, mas autenticação não implementada ainda)
- 🗄️ **Spring Data JPA**
- 🐬 **MySQL**
- 🔧 **Maven**
- 🔑 **JWT** (para autenticação futura)

---

## 🏗️ Pré-requisitos

- ✅ Java **21** ou superior
- ✅ Maven instalado
- ✅ MySQL rodando localmente ou na nuvem
- ✅ IDE de sua preferência (IntelliJ, VSCode, Eclipse, etc.)

---

## 🚀 Como rodar localmente

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/seu-usuario/meu-saldo-backend.git
cd meu-saldo-backend
```

### 2️⃣ Configure as variáveis de ambiente
#### Crie um arquivo .env na raiz do projeto (ou configure diretamente na IDE/terminal) com:
```
DB_URL=jdbc:mysql://localhost:3306/meusaldo
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```
> 🔒 Isso mantém senhas e dados sensíveis fora do código.

### 3️⃣ Configure o arquivo application.properties
#### O arquivo já está preparado para ler variáveis de ambiente:
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

### 4️⃣ Execute o projeto
#### ✅ Usando Maven:
```
./mvnw spring-boot:run
```
Ou
```
mvn spring-boot:run
```

#### ✅ Usando sua IDE:
- Importe o projeto como Maven Project.
- Rode a classe MeuSaldoApplication.java.

---

## 📂 Estrutura do projeto (resumida)

src\
├── main\
│   ├── java\
│   │   └── com.seuusuario.meusaldo\
│   │       ├── controller\
│   │       ├── model\
│   │       ├── repository\
│   │       ├── service\
│   │       └── MeuSaldoApplication.java\
│   └── resources\
│       ├── application.properties\
│       └── ...\
└── test

---

## 🚧 Funcionalidades futuras

- 🔐 Autenticação e autorização com JWT
- 📊 Dashboard com estatísticas financeiras
- 📥 Importação de dados bancários (CSV, OFX, etc.)
- 🪪 Cadastro de categorias personalizadas
- 💳 Integração com mais bancos
- 📱 API pública para o frontend em React

---

## 🛠️ Contribuindo

Contribuições são bem-vindas! 🚀\
Siga os passos:
  1. Fork este repositório
  2. Crie uma branch: ```git checkout -b feature/sua-feature```
  3. Commit suas mudanças: ```git commit -m 'feat: sua descrição'```
  4. Push para a branch: ```git push origin feature/sua-feature```
  5. Abra um Pull Request ✔️

---

## 📝 Licença

Este projeto está sob a licença MIT. Consulte o arquivo LICENSE para mais informações.

---

## ✨ Contato

Desenvolvido por Nathan Nolacio.\
📧 [nathannolacio04@gmail.com](nathannolacio04@gmail.com)\
🔗 [LinkedIn](https://www.linkedin.com/in/nathannolacio)
