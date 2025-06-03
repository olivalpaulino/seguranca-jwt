package dobackaofront.seguranca_jwt.config;

import dobackaofront.seguranca_jwt.entity.User;
import dobackaofront.seguranca_jwt.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            // Cria admin
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(encoder.encode("123456"))
                        .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                        .build();
                userRepo.save(admin);
                System.out.println("✅ Usuário admin criado com sucesso.");
            } else {
                System.out.println("ℹ️ Usuário admin já existe.");
            }

            // Cria usuário comum
            if (userRepo.findByUsername("user").isEmpty()) {
                User user = User.builder()
                        .username("user")
                        .password(encoder.encode("123456"))
                        .roles(List.of("ROLE_USER"))
                        .build();
                userRepo.save(user);
                System.out.println("✅ Usuário comum criado com sucesso.");
            } else {
                System.out.println("ℹ️ Usuário comum já existe.");
            }
        };
    }
}
