const userId = localStorage.getItem('userId');

if (!userId) {
    window.location.href = '/index.html';
}

window.onload = function() {
    loadStats();
}

async function loadStats() {
    try {
        const response = await fetch(`/api/applications/stats/${userId}`);
        const stats = await response.json();

        // Fill stat cards
        document.getElementById('total').textContent     = stats.total;
        document.getElementById('interview').textContent = stats.interview;
        document.getElementById('offer').textContent     = stats.offer;
        document.getElementById('rejected').textContent  = stats.rejected;
        document.getElementById('ghosted').textContent   = stats.ghosted;

        // Calculate response rate
        const responded = stats.interview + stats.offer + stats.rejected;
        const rate = stats.total > 0 
            ? Math.round((responded / stats.total) * 100) 
            : 0;

        document.getElementById('response-rate').textContent = rate + '%';

        // Progress bars
        if (stats.total > 0) {
            setBar('interview', stats.interview, stats.total);
            setBar('offer',     stats.offer,     stats.total);
            setBar('rejected',  stats.rejected,  stats.total);
            setBar('ghosted',   stats.ghosted,   stats.total);
        }

    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

function setBar(status, count, total) {
    const pct = Math.round((count / total) * 100);
    document.getElementById(`${status}-pct`).textContent = pct + '%';
    document.getElementById(`${status}-bar`).style.width = pct + '%';
}

// Logout
function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    window.location.href = '/index.html';
}