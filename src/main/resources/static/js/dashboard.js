// Get user from browser storage
const userId   = localStorage.getItem('userId');
const userName = localStorage.getItem('userName');

// If not logged in → go to login page
if (!userId) {
    window.location.href = '/index.html';
}

// Show username
document.getElementById('user-name').textContent = userName;

// Load everything when page opens
window.onload = function() {
    loadStats();
    loadReminders();
    loadRecentApplications();
}

// Load stats
async function loadStats() {
    try {
        const response = await fetch(`/api/applications/stats/${userId}`);
        const stats = await response.json();

        document.getElementById('total').textContent     = stats.total;
        document.getElementById('interview').textContent = stats.interview;
        document.getElementById('offer').textContent     = stats.offer;
        document.getElementById('rejected').textContent  = stats.rejected;
        document.getElementById('ghosted').textContent   = stats.ghosted;

    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Load reminders
async function loadReminders() {
    try {
        const response = await fetch(`/api/applications/reminders/${userId}`);
        const reminders = await response.json();

        const container = document.getElementById('reminders-list');

        if (reminders.length === 0) {
            container.innerHTML = '<p style="color:#888">No reminders. You are on top of things! 🎉</p>';
            return;
        }

        container.innerHTML = reminders.map(app => `
            <div class="reminder-item">
                ⚠️ <strong>${app.company}</strong> — ${app.role} 
                — Applied on ${app.appliedOn} — No update yet!
            </div>
        `).join('');

    } catch (error) {
        console.error('Error loading reminders:', error);
    }
}

// Load recent applications (last 5)
async function loadRecentApplications() {
    try {
        const response = await fetch(`/api/applications/user/${userId}`);
        const apps = await response.json();

        const container = document.getElementById('recent-list');

        if (apps.length === 0) {
            container.innerHTML = '<p style="color:#888">No applications yet. Add your first one!</p>';
            return;
        }

        // Show last 5 only
        const recent = apps.slice(-5).reverse();

        container.innerHTML = recent.map(app => `
            <div class="recent-item">
                <div>
                    <strong>${app.company}</strong> — ${app.role}
                </div>
                <div>
                    <span class="badge ${app.status}">${app.status}</span>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error loading applications:', error);
    }
}

// Logout
function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    window.location.href = '/index.html';
}