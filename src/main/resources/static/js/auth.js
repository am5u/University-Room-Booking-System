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

// Function to check if user is logged in
function isLoggedIn() {
    const token = localStorage.getItem('token');
    if (!token) return false;
    
    try {
        const tokenParts = token.split('.');
        if (tokenParts.length !== 3) return false;
        
        const payload = JSON.parse(atob(tokenParts[1]));
        const expiration = new Date(payload.exp * 1000);
        
        if (expiration < new Date()) {
            clearUserData();
            return false;
        }
        
        // Ensure all required user data is present
        if (!payload.role || !payload.userId || !payload.name || !payload.department) {
            clearUserData();
            return false;
        }
        
        return true;
    } catch (e) {
        console.error('Error checking login status:', e);
        clearUserData();
        return false;
    }
}

// Function to get user role
function getUserRole() {
    try {
        const token = localStorage.getItem('token');
        if (!token) return null;
        
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('Token payload:', payload);
        console.log('Raw role from token:', payload.role);
        
        const role = payload.role;
        if (!role) return null;
        
        // Remove ROLE_ prefix if present
        const cleanRole = role.replace('ROLE_', '');
        console.log('Cleaned role:', cleanRole);
        return cleanRole;
    } catch (error) {
        console.error('Error getting user role:', error);
        return null;
    }
}

// Function to check if user is admin
function isAdmin() {
    const role = getUserRole();
    console.log('Current user role:', role);
    console.log('Is admin check:', role === 'ADMIN');
    return role === 'ADMIN';
}

// Function to check if user is student or faculty
function isUser() {
    const role = getUserRole();
    return role === 'STUDENT' || role === 'FACULTYMEMBER';
}

// Function to handle unauthorized access
function handleUnauthorized() {
    alert('You are not authorized to access this page');
    window.location.href = 'home.html';
}

// Function to check authorization for student pages
function checkUserAuthorization() {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }
    if (!isUser()) {
        handleUnauthorized();
    }
}

// Function to check authorization for admin pages
function checkAdminAuthorization() {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }
    if (!isAdmin()) {
        handleUnauthorized();
    }
}

// Function to decode JWT token
function decodeToken(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Error decoding token:', e);
        return null;
    }
}

// Function to store user data from token
function storeUserData(token) {
    try {
        const tokenParts = token.split('.');
        if (tokenParts.length === 3) {
            const payload = JSON.parse(atob(tokenParts[1]));
            
            // Verify all required data is present
            if (!payload.role || !payload.userId || !payload.name || !payload.department) {
                console.error('Missing required user data in token');
                return false;
            }
            
            localStorage.setItem('userRole', payload.role);
            localStorage.setItem('userId', payload.userId);
            localStorage.setItem('userName', payload.name);
            localStorage.setItem('userDepartment', payload.department);
            return true;
        }
    } catch (e) {
        console.error('Error storing user data:', e);
    }
    return false;
}

// Function to clear user data
function clearUserData() {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userDepartment');
}

// Function to add authorization header to fetch requests
function addAuthHeader(headers = {}) {
    const token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

// Function to handle API responses
async function handleApiResponse(response) {
    if (response.status === 401) {
        clearUserData();
        window.location.href = 'login.html';
        return null;
    }
    if (response.status === 403) {
        handleUnauthorized();
        return null;
    }
    return await response.json();
}

// Function to make authenticated API calls
async function authenticatedFetch(url, options = {}) {
    options.headers = addAuthHeader(options.headers || {});
    const response = await fetch(url, options);
    return handleApiResponse(response);
} 