// Login function
async function login() {
    const email    = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!email || !password) {
        showError('Please fill in all fields');
        return;
    }

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Save user info in browser
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userName', data.name);

            // Go to dashboard
            window.location.href = '/dashboard.html';
        } else {
            showError(data.error);
        }

    } catch (error) {
        showError('Something went wrong. Try again.');
    }
}

// Register function
async function register() {
    const name     = document.getElementById('name').value;
    const email    = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!name || !email || !password) {
        showError('Please fill in all fields');
        return;
    }

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            showSuccess('Registered! Redirecting to login...');
            setTimeout(() => {
                window.location.href = '/index.html';
            }, 1500);
        } else {
            showError(data.error);
        }

    } catch (error) {
        showError('Something went wrong. Try again.');
    }
}

// Logout
function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    window.location.href = '/index.html';
}

// Show error message
function showError(msg) {
    const el = document.getElementById('error-msg');
    el.textContent = msg;
    el.classList.remove('hidden');
}

// Show success message
function showSuccess(msg) {
    const el = document.getElementById('success-msg');
    el.textContent = msg;
    el.classList.remove('hidden');
}