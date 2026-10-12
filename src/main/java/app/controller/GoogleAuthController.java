package app.controller;

import app.entity.Role;
import app.entity.User;
import app.repository.UserRepository;
import app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

    private final UserRepository userRepository;
    private final UserService userService;

    public GoogleAuthController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        String name = payload.get("name");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required."));
        }

        email = email.trim().toLowerCase();
        log.info("Processing Google Authentication for: {}", email);

        // Find or create user
        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) {
            Role assignedRole = Role.ROLE_STUDENT;
            if (email.contains("24eg105r34") || email.contains("admin") || email.endsWith("@anurag.edu.in")) {
                assignedRole = Role.ROLE_ADMIN;
            } else if (email.contains("prof") || email.contains("faculty")) {
                assignedRole = Role.ROLE_FACULTY;
            }

            String displayName = (name != null && !name.trim().isEmpty()) ? name.trim() : email.split("@")[0];
            log.info("Auto-provisioning user from Google Sign-In: {} with role {}", email, assignedRole);
            user = userService.createUser(email, "google_oauth_" + System.currentTimeMillis(), assignedRole, displayName);
        }

        // Establish Spring Security Context
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        // Determine destination dashboard
        String redirectUrl = switch (user.getRole()) {
            case ROLE_ADMIN -> "/admin/dashboard";
            case ROLE_FACULTY -> "/faculty/dashboard";
            case ROLE_STUDENT -> "/student/dashboard";
        };

        log.info("Google authentication successful for {}. Redirecting to {}", email, redirectUrl);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "email", user.getUsername(),
                "role", user.getRole().name(),
                "redirectUrl", redirectUrl
        ));
    }
}
