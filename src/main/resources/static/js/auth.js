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
    console.log('CheckAuth - Token exists:', !!token);
    console.log('CheckAuth - Current page:', window.location.pathname);
    console.log('CheckAuth - Is public page:', isPublicPage());
    
    // If no token and not on a public page, redirect to login
    if (!token && !isPublicPage()) {
        console.log('No token and not public page - redirecting to login');
        window.location.href = 'login.html';
        return false;
    }
    
    // If has token and on login/register page, redirect to home
    if (token && (window.location.pathname.includes('login.html') || window.location.pathname.includes('register.html'))) {
        console.log('Has token and on login/register - redirecting to home');
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

// Function to get current user ID from JWT token
function getCurrentUserId() {
    try {
        const token = localStorage.getItem('token');
        console.log('Token from localStorage:', token ? 'Token exists' : 'No token');
        
        if (!token) {
            throw new Error('No token found');
        }
        
        const tokenParts = token.split('.');
        if (tokenParts.length !== 3) {
            throw new Error('Invalid token format');
        }
        
        const payload = JSON.parse(atob(tokenParts[1]));
        console.log('Token payload:', JSON.stringify(payload));
        const userId = payload.userId;
        console.log('Extracted userId:', userId);
        
        if (!userId) {
            throw new Error('User ID not found in token');
        }
        
        return userId;
    } catch (error) {
        console.error('Error getting current user ID:', error);
        throw error;
    }
} 