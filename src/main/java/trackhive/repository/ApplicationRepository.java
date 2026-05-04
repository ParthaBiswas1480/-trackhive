package trackhive.repository;

import trackhive.model.Application;
import trackhive.model.Status;
import trackhive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Get all applications for a specific user
    List<Application> findByUser(User user);

    // Get applications by status for a specific user
    List<Application> findByUserAndStatus(User user, Status status);

    // Count applications by status for a specific user
    long countByUserAndStatus(User user, Status status);

    // Get stale applications — no update in 7 days
    @Query("SELECT a FROM Application a WHERE a.user = :user " +
           "AND a.status = 'APPLIED' " +
           "AND a.appliedOn <= :sevenDaysAgo")
    List<Application> findStaleApplications(User user, LocalDate sevenDaysAgo);
}