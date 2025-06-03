
# 🔐 Projeto Spring Boot JWT + Spring Security + H2 + Thymeleaf

Este é um sistema completo de autenticação e autorização utilizando Spring Boot 3.4.5, Spring Security, JWT (com Refresh Token), banco de dados em memória H2, e páginas HTML com Thymeleaf. Ideal para fins didáticos, com arquitetura limpa e bem separada entre API e rotas web.

---

## 🧰 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.4.5
- Spring Security 6
- Spring Data JPA
- H2 Database (em memória)
- JWT com JJWT
- Thymeleaf
- Lombok (opcional)

---

## 🚀 Como executar

1. Clone o projeto e abra na sua IDE (recomendado: IntelliJ ou NetBeans 26+).
2. Compile e execute a aplicação (`mvn spring-boot:run` ou botão de play da IDE).
3. Acesse os seguintes recursos no navegador:

### 🌐 Rotas Web (com Thymeleaf)

| Rota           | Acesso        | Descrição                         |
|----------------|---------------|-----------------------------------|
| `/login`       | Público       | Tela de login                     |
| `/home`        | Qualquer user | Página inicial após login         |
| `/admin`       | Somente admin | Página restrita a administradores |
| `/logout`      | Autenticados  | Efetua logout e limpa o cookie    |

---

### 🧪 Acessar H2 Console

- URL: [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuário: `sa`
- Senha: (deixe em branco)

---

## 👥 Usuários iniciais

Estes usuários são cadastrados automaticamente ao iniciar a aplicação:

| Username | Senha   | Permissões         |
|----------|---------|--------------------|
| `admin`  | 123456  | `ROLE_ADMIN`, `ROLE_USER` |
| `user`   | 123456  | `ROLE_USER`        |

---

## 📡 Testar API com Postman (JWT)

### 🔐 Login via API

- **POST** `http://localhost:8080/api/auth/login`
- Body (JSON):
```json
{
  "username": "admin",
  "password": "123456"
}
```
- ✅ Retorna o `accessToken` (no corpo) e `refresh_token` (via cookie)

---

### 🔄 Refresh Token

- **POST** `http://localhost:8080/api/auth/refresh`
- ✅ Envia o cookie `refresh_token` automaticamente

---

### 🔓 Logout

- **POST** `http://localhost:8080/api/auth/logout`
- ✅ Remove o token do banco e limpa o cookie

---

### 🔒 Acessar rota protegida com token

- Exemplo:
  - **GET** `http://localhost:8080/api/secure-data`
  - Headers:
    ```
    Authorization: Bearer <accessToken>
    ```

---

## 🧠 Conceitos abordados

- Autenticação via formulário e via API
- Geração de token JWT com `subject`, `roles`, `exp` e `jti`
- Armazenamento de refresh tokens em banco
- Cookies HttpOnly + Secure
- Validação de token JWT manual e via filtro
- Separação entre rotas `API` e rotas `Web`
- Uso de `SecurityFilterChain` para múltiplas estratégias

---

## 📁 Estrutura de Pacotes

- `config`: configurações de segurança e banco
- `controller`: rotas públicas e protegidas (web + API)
- `dto`: objetos de requisição/resposta
- `entity`: entidades JPA (User, RefreshToken)
- `repository`: interfaces JPA
- `security`: filtros e serviços de segurança
- `service`: geração e validação de tokens

---

## ✅ Status

Projeto funcionando com:
- Login, logout, refresh
- Expiração segura de tokens
- Cookies protegidos
- Validação e autorização baseada em roles

---

**💡 Ideal para aprender autenticação moderna com Spring Security + JWT.**
