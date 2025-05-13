const publicPages = ['home.html', 'register.html', 'login.html'];

function isPublicPage() {
    const currentPage = window.location.pathname.split('/').pop();
    return publicPages.includes(currentPage);
}

function checkAuth() {
    const userId = localStorage.getItem('userId');

    if (!userId && !isPublicPage()) {
        window.location.href = 'login.html';
        return false;
    }
 
    if (userId && (window.location.pathname.includes('login.html') || window.location.pathname.includes('register.html'))) {
        window.location.href = 'home.html';
        return false;
    }

    return true;
}

// Check authentication when page loads
document.addEventListener('DOMContentLoaded', checkAuth);

// Function to get headers for API calls
function getAuthHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };

    const userId = localStorage.getItem('userId');
    if (userId) {
        headers['X-User-ID'] = userId;
    }

    return headers;
}

function logout() {
    clearUserData();
    window.location.href = 'login.html';
}

function getCurrentUserId() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        throw new Error('No user ID found');
    }
    return userId;
}

// Function to check if user is logged in
function isLoggedIn() {
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');
    const userName = localStorage.getItem('userName');

    return !!(userId && userRole && userName);
}

function getUserRole() {
    return localStorage.getItem('userRole');
}

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

// Function to store user data
function storeUserData(userData) {
    localStorage.setItem('userId', userData.userId);
    localStorage.setItem('userRole', userData.role);
    localStorage.setItem('userName', userData.name);
    return true;
}

// Function to clear user data
function clearUserData() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userName');
    localStorage.removeItem('userDepartment');
}

// Function to handle API responses
async function handleApiResponse(response) {
    if (response.status === 401 || response.status === 403) {
        clearUserData();
        window.location.href = 'login.html';
        return null;
    }
    return await response.json();
}

