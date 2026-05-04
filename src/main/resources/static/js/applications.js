const userId = localStorage.getItem('userId');

if (!userId) {
    window.location.href = '/index.html';
}

window.onload = function() {
    loadApplications();
}

// Load all applications
async function loadApplications() {
    try {
        const response = await fetch(`/api/applications/user/${userId}`);
        const apps = await response.json();

        const tbody = document.getElementById('applications-tbody');

        if (apps.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align:center;color:#888;padding:30px">
                        No applications yet. Add your first one!
                    </td>
                </tr>`;
            return;
        }

        tbody.innerHTML = apps.reverse().map(app => `
            <tr>
                <td><strong>${app.company}</strong></td>
                <td>${app.role}</td>
                <td><span class="badge ${app.status}">${app.status}</span></td>
                <td>${app.appliedOn}</td>
                <td>${app.notes || '—'}</td>
                <td>
                    <button class="btn-update" 
                        onclick="showUpdateForm(${app.id}, '${app.status}')">
                        Update
                    </button>
                    <button class="btn-delete" 
                        onclick="deleteApplication(${app.id})">
                        Delete
                    </button>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading applications:', error);
    }
}

// Show add form
function showAddForm() {
    document.getElementById('add-form').classList.remove('hidden');
}

// Hide add form
function hideAddForm() {
    document.getElementById('add-form').classList.add('hidden');
}

// Add new application
async function addApplication() {
    const company = document.getElementById('company').value;
    const role    = document.getElementById('role').value;
    const notes   = document.getElementById('notes').value;

    if (!company || !role) {
        document.getElementById('form-error').textContent = 'Company and Role are required!';
        document.getElementById('form-error').classList.remove('hidden');
        return;
    }

    try {
        const response = await fetch('/api/applications/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: userId.toString(),
                company,
                role,
                notes
            })
        });

        const data = await response.json();

        if (response.ok) {
            hideAddForm();
            document.getElementById('company').value = '';
            document.getElementById('role').value    = '';
            document.getElementById('notes').value   = '';
            loadApplications();
        } else {
            document.getElementById('form-error').textContent = data.error;
            document.getElementById('form-error').classList.remove('hidden');
        }

    } catch (error) {
        console.error('Error adding application:', error);
    }
}

// Show update status form
function showUpdateForm(appId, currentStatus) {
    const validTransitions = {
        'APPLIED':   ['INTERVIEW', 'REJECTED', 'GHOSTED'],
        'INTERVIEW': ['OFFER', 'REJECTED'],
        'OFFER':     [],
        'REJECTED':  [],
        'GHOSTED':   []
    };

    const options = validTransitions[currentStatus];

    if (options.length === 0) {
        alert(`Status is ${currentStatus} — this is a final state, cannot update.`);
        return;
    }

    const newStatus = prompt(
        `Current status: ${currentStatus}\nUpdate to (${options.join(' / ')}):`
    );

    if (!newStatus) return;

    if (!options.includes(newStatus.toUpperCase())) {
        alert(`Invalid status! Choose from: ${options.join(', ')}`);
        return;
    }

    updateStatus(appId, newStatus.toUpperCase());
}

// Update status API call
async function updateStatus(appId, newStatus) {
    try {
        const response = await fetch(`/api/applications/update/${appId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });

        const data = await response.json();

        if (response.ok) {
            loadApplications();
        } else {
            alert(data.error);
        }

    } catch (error) {
        console.error('Error updating status:', error);
    }
}

// Delete application
async function deleteApplication(appId) {
    if (!confirm('Are you sure you want to delete this application?')) return;

    try {
        const response = await fetch(`/api/applications/delete/${appId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadApplications();
        }

    } catch (error) {
        console.error('Error deleting application:', error);
    }
}

// Logout
function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    window.location.href = '/index.html';
}