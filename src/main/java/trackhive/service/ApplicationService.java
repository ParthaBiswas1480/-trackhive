package trackhive.service;

import trackhive.model.Application;
import trackhive.model.Status;
import trackhive.model.User;
import trackhive.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    // Add new application
    public Application addApplication(User user, String company, 
                                      String role, String notes) {
        Application app = new Application();
        app.setUser(user);
        app.setCompany(company);
        app.setRole(role);
        app.setStatus(Status.APPLIED);
        app.setAppliedOn(LocalDate.now());
        app.setNotes(notes);

        return applicationRepository.save(app);
    }

    // Get all applications for a user
    public List<Application> getAllApplications(User user) {
        return applicationRepository.findByUser(user);
    }

    // Update status — State Machine logic
    public Application updateStatus(Long applicationId, Status newStatus) {
        
        Application app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found!"));

        // State Machine — valid transitions only
        Status current = app.getStatus();
        if (!isValidTransition(current, newStatus)) {
            throw new RuntimeException(
                "Invalid transition from " + current + " to " + newStatus
            );
        }

        app.setStatus(newStatus);
        app.setUpdatedOn(LocalDate.now());

        return applicationRepository.save(app);
    }

    // State Machine — DSA concept
    private boolean isValidTransition(Status current, Status next) {
        switch (current) {
            case APPLIED:
                return next == Status.INTERVIEW 
                    || next == Status.REJECTED 
                    || next == Status.GHOSTED;
            case INTERVIEW:
                return next == Status.OFFER 
                    || next == Status.REJECTED;
            case OFFER:
            case REJECTED:
            case GHOSTED:
                return false; // Final states
            default:
                return false;
        }
    }

    // Get statistics — HashMap DSA concept
    public Map<String, Long> getStats(User user) {
        Map<String, Long> stats = new HashMap<>();

        stats.put("total", applicationRepository.countByUserAndStatus(user, Status.APPLIED)
                         + applicationRepository.countByUserAndStatus(user, Status.INTERVIEW)
                         + applicationRepository.countByUserAndStatus(user, Status.OFFER)
                         + applicationRepository.countByUserAndStatus(user, Status.REJECTED)
                         + applicationRepository.countByUserAndStatus(user, Status.GHOSTED));

        stats.put("applied",    applicationRepository.countByUserAndStatus(user, Status.APPLIED));
        stats.put("interview",  applicationRepository.countByUserAndStatus(user, Status.INTERVIEW));
        stats.put("offer",      applicationRepository.countByUserAndStatus(user, Status.OFFER));
        stats.put("rejected",   applicationRepository.countByUserAndStatus(user, Status.REJECTED));
        stats.put("ghosted",    applicationRepository.countByUserAndStatus(user, Status.GHOSTED));

        return stats;
    }

    // Get stale applications reminder
    public List<Application> getReminders(User user) {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return applicationRepository.findStaleApplications(user, sevenDaysAgo);
    }

    // Delete application
    public void deleteApplication(Long applicationId) {
        applicationRepository.deleteById(applicationId);
    }
}
