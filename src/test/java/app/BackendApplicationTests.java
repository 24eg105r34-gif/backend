package app;

import app.entity.Role;
import app.entity.User;
import app.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test 1: Verify Spring Boot context loads successfully")
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(passwordEncoder);
    }

    @Test
    @DisplayName("Test 2: Verify Developer Peddi Kalyan administrator account initialization")
    void testDeveloperAdminAccountsExist() {
        Optional<User> devUser = userRepository.findByUsername("24eg105r34@gmail.com");
        assertTrue(devUser.isPresent(), "Developer account 24eg105r34@gmail.com should be seeded");
        assertEquals(Role.ROLE_ADMIN, devUser.get().getRole());
        assertTrue(devUser.get().isEnabled());
        assertTrue(passwordEncoder.matches("admin123", devUser.get().getPassword()));

        Optional<User> anuragUser = userRepository.findByUsername("24eg105r34@anurag.edu.in");
        assertTrue(anuragUser.isPresent(), "Developer account 24eg105r34@anurag.edu.in should be seeded");
        assertEquals(Role.ROLE_ADMIN, anuragUser.get().getRole());
    }

    @Test
    @DisplayName("Test 3: BCrypt Password Encoder verification")
    void testPasswordEncoderBcrypt() {
        String raw = "CampusSecret2026";
        String encoded = passwordEncoder.encode(raw);
        assertNotEquals(raw, encoded);
        assertTrue(passwordEncoder.matches(raw, encoded));
        assertFalse(passwordEncoder.matches("WrongPassword", encoded));
    }
}

