package trackhive.service;

import trackhive.model.User;
import trackhive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register new user
    public User registerUser(String name, String email, String password) {
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // Login user
    public User loginUser(String email, String password) {
        
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = optionalUser.get();

        if (!user.getPasswordHash().equals(password)) {
            throw new RuntimeException("Wrong password!");
        }

        return user;
    }

    // Get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}