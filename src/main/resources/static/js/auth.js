// List of public pages that don't require authentication
const publicPages = ['home.html', 'register.html', 'login.html'];

// Function to check if current page is public
function isPublicPage() {
    const currentPage = window.location.pathname.split('/').pop();
    return publicPages.includes(currentPage);
}

// Function to check authentication
function checkAuth() {
    const token = localStorage.getItem('token');
    
    // If no token and not on a public page, redirect to login
    if (!token && !isPublicPage()) {
        window.location.href = 'login.html';
        return false;
    }
    
    // If has token and on login/register page, redirect to home
    if (token && (window.location.pathname.includes('login.html') || window.location.pathname.includes('register.html'))) {
        window.location.href = 'home.html';
        return false;
    }
    
    return true;
}

// Check authentication when page loads
document.addEventListener('DOMContentLoaded', checkAuth);

// Function to get auth headers for API calls
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

// Function to handle logout
function logout() {
    localStorage.removeItem('token');
    window.location.href = 'login.html';
} 