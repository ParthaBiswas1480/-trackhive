package trackhive.controller;

import trackhive.model.User;
import trackhive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String name     = body.get("name");
            String email    = body.get("email");
            String password = body.get("password");

            User user = userService.registerUser(name, email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful!");
            response.put("userId", user.getId());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email    = body.get("email");
            String password = body.get("password");

            User user = userService.loginUser(email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("userId", user.getId());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}