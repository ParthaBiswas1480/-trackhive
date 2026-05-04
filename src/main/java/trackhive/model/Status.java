package trackhive.model;

public enum Status {
    APPLIED,    // Just sent resume
    INTERVIEW,  // Got interview call
    OFFER,      // Got offer letter
    REJECTED,   // Got rejection
    GHOSTED     // No response in 7+ days
}