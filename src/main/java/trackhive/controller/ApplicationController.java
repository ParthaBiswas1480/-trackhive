package trackhive.controller;

import trackhive.model.Application;
import trackhive.model.Status;
import trackhive.model.User;
import trackhive.service.ApplicationService;
import trackhive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    // Get all applications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getApplications(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<Application> apps = applicationService.getAllApplications(user);
            return ResponseEntity.ok(apps);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Add new application
    @PostMapping("/add")
    public ResponseEntity<?> addApplication(@RequestBody Map<String, String> body) {
        try {
            Long userId   = Long.parseLong(body.get("userId"));
            String company = body.get("company");
            String role    = body.get("role");
            String notes   = body.get("notes");

            User user = userService.getUserById(userId);
            Application app = applicationService.addApplication(
                user, company, role, notes
            );

            return ResponseEntity.ok(Map.of(
                "message", "Application added!",
                "id", app.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update application status
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Status newStatus = Status.valueOf(body.get("status"));
            Application app = applicationService.updateStatus(id, newStatus);

            return ResponseEntity.ok(Map.of(
                "message", "Status updated!",
                "newStatus", app.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete application
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.ok(Map.of("message", "Application deleted!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get stats for a user
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getStats(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            Map<String, Long> stats = applicationService.getStats(user);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get reminders for a user
    @GetMapping("/reminders/{userId}")
    public ResponseEntity<?> getReminders(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<Application> stale = applicationService.getReminders(user);
            return ResponseEntity.ok(stale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
