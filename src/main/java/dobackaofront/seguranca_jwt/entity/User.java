package dobackaofront.seguranca_jwt.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ou IDENTITY se quiser long/incremental
    private String id;
    private String username;
    private String password;
    private List<String> roles;

    public User() {
    }

    // Construtor privado para impedir a criação de instâncias diretamente
    private User(UserBuilder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.roles = builder.roles;
    }

    // Getters e setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    // Método builder estático
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Classe interna estática UserBuilder
    public static class UserBuilder {
        private String username;
        private String password;
        private List<String> roles;

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
