// API endpoints
const API_BASE_URL = 'http://localhost:8080/api';
const BOOKINGS_URL = `${API_BASE_URL}/bookings`;
const ADMIN_URL = `${API_BASE_URL}/admin`;
const AUTH_URL = `${API_BASE_URL}/auth`;

// DOM Elements
const pendingList = document.getElementById('pending-list');
const allList = document.getElementById('all-list');
const usersList = document.getElementById('users-list');

// Cache for user details
const userCache = new Map();

// Function to get user details
async function getUserDetails(userId) {
    if (userCache.has(userId)) {
        return userCache.get(userId);
    }
    
    try {
        const response = await fetch(`${AUTH_URL}/user/${userId}`, {
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to fetch user details');
        }
        
        const user = await response.json();
        userCache.set(userId, user);
        return user;
    } catch (error) {
        console.error('Error fetching user details:', error);
        return null;
    }
}

// Initialize the dashboard
document.addEventListener('DOMContentLoaded', async () => {
    // Check if user is logged in and is admin
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    // Check admin role
    const userRole = getUserRole();
    if (userRole !== 'ADMIN') {
        handleUnauthorized();
        return;
    }

    // Set admin name in header
    const adminName = localStorage.getItem('userName');
    if (adminName) {
        document.getElementById('admin-name').textContent = adminName;
    }

    // Load initial data
    showTab('pending');
});

// Tab switching
function showTab(tabName) {
    // Check admin role again before showing tabs
    if (!isAdmin()) {
        handleUnauthorized();
        return;
    }

    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Show selected tab content
    document.getElementById(tabName === 'users' ? 'users' : `${tabName}-bookings`).classList.add('active');

    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`.tab-btn[onclick="showTab('${tabName}')"]`).classList.add('active');

    // Load appropriate data
    if (tabName === 'pending') {
        loadPendingBookings();
    } else if (tabName === 'all') {
        loadAllBookings();
    } else if (tabName === 'users') {
        loadUsers();
    }
}

// Format date and time
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString();
}

// Create booking card HTML
async function createBookingCard(booking) {
    // Fetch user details
    const user = await getUserDetails(booking.userId);
    console.log('User details for booking:', user); // Debug log
    console.log('Booking object:', booking); // Debug log to see the booking structure
    
    const statusClass = `status-${booking.status.toLowerCase()}`;
    return `
        <div class="booking-card">
            <div class="booking-header">
                <h3 class="booking-title">Booking #${booking.bookingId}</h3>
                <span class="booking-status ${statusClass}">
                    <i class="fas ${getStatusIcon(booking.status)}"></i>
                    ${booking.status}
                </span>
            </div>
            <div class="booking-details">
                <div class="booking-detail">
                    <i class="fas fa-door-open"></i>
                    <span><strong>Room:</strong> ${booking.room ? booking.room.name : 'Unknown Room'}</span>
                </div>
                <div class="booking-detail">
                    <i class="fas fa-user"></i>
                    <span><strong>User:</strong> ${user ? user.name : 'Unknown User'} (${user ? user.role : 'Unknown Role'})</span>
                </div>
                <div class="booking-detail">
                    <i class="fas fa-building"></i>
                    <span><strong>Department:</strong> ${user ? user.department : 'Unknown Department'}</span>
                </div>
                <div class="booking-detail">
                    <i class="fas fa-clock"></i>
                    <span><strong>Start Time:</strong> ${formatDateTime(booking.startTime)}</span>
                </div>
                <div class="booking-detail">
                    <i class="fas fa-clock"></i>
                    <span><strong>End Time:</strong> ${formatDateTime(booking.endTime)}</span>
                </div>
                <div class="booking-detail">
                    <i class="fas fa-info-circle"></i>
                    <span><strong>Purpose:</strong> ${booking.purpose}</span>
                </div>
            </div>
            ${booking.status === 'PENDING' ? `
                <div class="booking-actions">
                    <button class="action-btn approve-btn" onclick="handleBookingAction(${booking.bookingId}, 'accept')">
                        <i class="fas fa-check"></i>
                        Accept
                    </button>
                    <button class="action-btn reject-btn" onclick="handleBookingAction(${booking.bookingId}, 'reject')">
                        <i class="fas fa-times"></i>
                        Reject
                    </button>
                </div>
            ` : ''}
        </div>
    `;
}

// Helper function to get status icon
function getStatusIcon(status) {
    switch (status.toUpperCase()) {
        case 'PENDING':
            return 'fa-clock';
        case 'APPROVED':
            return 'fa-check-circle';
        case 'REJECTED':
            return 'fa-times-circle';
        default:
            return 'fa-info-circle';
    }
}

// Helper function to add headers
function getAuthHeaders() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return {};
    }

    const headers = {
        'Content-Type': 'application/json'
    };

    // Add user ID to headers if available
    const userId = localStorage.getItem('userId');
    if (userId) {
        headers['X-User-ID'] = userId;
        console.log('Admin.js - Adding X-User-ID header:', userId);
    }

    const userRole = localStorage.getItem('userRole');
    if (userRole) {
        headers['X-User-Role'] = userRole === 'ADMIN' ? 'ROLE_ADMIN' : userRole;
    }

    return headers;
}

// Load pending bookings
async function loadPendingBookings() {
    try {
        console.log('Fetching pending bookings from:', `${BOOKINGS_URL}/all`);
        const response = await fetch(`${BOOKINGS_URL}/all`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const bookings = await response.json();
        console.log('Received bookings:', bookings);

        if (!Array.isArray(bookings)) {
            throw new Error('Invalid response format: expected an array of bookings');
        }

        const pendingBookings = bookings.filter(booking => booking.status === 'PENDING');
        console.log('Filtered pending bookings:', pendingBookings);

        if (pendingBookings.length === 0) {
            pendingList.innerHTML = '<p>No pending bookings found</p>';
        } else {
            // Create all booking cards asynchronously
            const bookingCards = await Promise.all(pendingBookings.map(createBookingCard));
            pendingList.innerHTML = bookingCards.join('');
        }
    } catch (error) {
        console.error('Error loading pending bookings:', error);
        pendingList.innerHTML = `
            <div class="error-message">
                <p>Error loading pending bookings</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}

// Load all bookings
async function loadAllBookings() {
    try {
        console.log('Fetching all bookings from:', `${BOOKINGS_URL}/all`);
        const response = await fetch(`${BOOKINGS_URL}/all`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const bookings = await response.json();
        console.log('Received all bookings:', bookings);

        if (!Array.isArray(bookings)) {
            throw new Error('Invalid response format: expected an array of bookings');
        }

        // Filter out pending bookings and sort by start time (newest first)
        const historyBookings = bookings
            .filter(booking => booking.status !== 'PENDING')
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime));

        if (historyBookings.length === 0) {
            allList.innerHTML = '<p>No booking history found</p>';
        } else {
            // Create all booking cards asynchronously
            const bookingCards = await Promise.all(historyBookings.map(createBookingCard));
            allList.innerHTML = bookingCards.join('');
        }
    } catch (error) {
        console.error('Error loading all bookings:', error);
        allList.innerHTML = `
            <div class="error-message">
                <p>Error loading bookings</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}

// Handle booking actions (accept/reject)
async function handleBookingAction(bookingId, action) {
    try {
        console.log(`Handling ${action} action for booking ${bookingId}`);
        
        const response = await fetch(`${BOOKINGS_URL}/${bookingId}/${action}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });

        // Get the response text first
        const responseText = await response.text();
        console.log(`Response from ${action}:`, responseText);

        // Try to parse as JSON
        let responseData;
        try {
            responseData = JSON.parse(responseText);
        } catch (e) {
            responseData = { message: responseText };
        }

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error(responseData.message || `Failed to ${action} booking`);
        }

       
        const currentTab = document.querySelector('.tab-content.active').id;
        if (currentTab === 'pending-bookings') {
            loadPendingBookings();
        } else {
            loadAllBookings();
        }
    } catch (error) {
        console.error(`Error ${action}ing booking:`, error);
        alert(`Error ${action}ing booking: ${error.message}`);
    }
}

// Load users
async function loadUsers() {
    try {
        console.log('Fetching users from:', `${ADMIN_URL}/users`);
        const response = await fetch(`${ADMIN_URL}/users`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const users = await response.json();
        console.log('Received users data:', users);

        if (!Array.isArray(users)) {
            throw new Error('Invalid response format: expected an array of users');
        }

        if (users.length === 0) {
            usersList.innerHTML = '<p>No users found</p>';
        } else {
            usersList.innerHTML = users.map(user => {
                console.log('Processing user:', user);

                const userName = user.name || user.username || 'Unknown';
                const userEmail = user.emailAddress || 'No email provided';
                const userDepartment = user.department || 'No department specified';
                const userRole = user.role || 'STUDENT';
                const userId = user.id || user.userId;

                // Skip admin users from the list
                if (userRole === 'ADMIN') {
                    return '';
                }

                return `
                    <div class="user-card">
                        <div class="user-header">
                            <h3 class="user-title">${userName}</h3>
                            <span class="user-role ${userRole.toLowerCase()}">
                                <i class="fas ${userRole === 'FACULTYMEMBER' ? 'fa-user-tie' : 'fa-user-graduate'}"></i>
                                ${userRole === 'FACULTYMEMBER' ? 'Faculty Member' : 'Student'}
                            </span>
                        </div>
                        <div class="user-details">
                            <div class="user-detail">
                                <i class="fas fa-envelope"></i>
                                <span><strong>Email:</strong> ${userEmail}</span>
                            </div>
                            <div class="user-detail">
                                <i class="fas fa-building"></i>
                                <span><strong>Department:</strong> ${userDepartment}</span>
                            </div>
                        </div>
                        <div class="user-actions">
                            <button class="action-btn ${userRole === 'FACULTYMEMBER' ? 'make-student-btn' : 'make-faculty-btn'}"
                                    onclick="changeUserRole(${userId}, '${userRole === 'FACULTYMEMBER' ? 'STUDENT' : 'FACULTYMEMBER'}')">
                                <i class="fas ${userRole === 'FACULTYMEMBER' ? 'fa-user-graduate' : 'fa-user-tie'}"></i>
                                Change Role to ${userRole === 'FACULTYMEMBER' ? 'Student' : 'Faculty Member'}
                            </button>
                            ${userRole !== 'ADMIN' ? `
                                <button class="action-btn delete-btn" onclick="deleteUser(${userId}, '${userName}')">
                                    <i class="fas fa-trash"></i>
                                    Delete
                                </button>
                            ` : ''}
                        </div>
                    </div>
                `;
            }).join('');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        usersList.innerHTML = `
            <div class="error-message">
                <p>Error loading users</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}

// Change user role
async function changeUserRole(userId, newRole) {
    try {
        if (!userId) {
            throw new Error('User ID is required');
        }

        // Convert role to uppercase to match enum
        newRole = newRole.toUpperCase();
        console.log('Attempting to change role for user', userId, 'to', newRole);

        const requestBody = {
            role: newRole
        };
        console.log('Request body:', requestBody);

        const response = await fetch(`${ADMIN_URL}/users/${userId}/role`, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            const errorText = await response.text();
            console.error('Error response:', errorText);
            throw new Error(`Failed to change role: ${response.status} - ${errorText}`);
        }

        // Show success message
        alert(`User role changed to ${newRole === 'FACULTYMEMBER' ? 'Faculty Member' : 'Student'} successfully!`);

        // Reload the users list to show the updated roles
        loadUsers();
    } catch (error) {
        console.error('Error changing user role:', error);
        alert('Error changing user role: ' + error.message);
    }
}

// Logout function
function logout() {
    // Clear user data
    clearUserData();
    // Redirect to login page
    window.location.href = '/login.html';
}

// Add the delete user function at the end of the file
async function deleteUser(userId, userName) {
    if (!confirm(`Are you sure you want to delete user ${userName}? This action cannot be undone.`)) {
        return;
    }

    try {
        const response = await fetch(`${ADMIN_URL}/users/${userId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return;
            }
            const errorText = await response.text();
            throw new Error(errorText || `Failed to delete user: ${response.status}`);
        }

        // Show success message
        alert('User deleted successfully');
        // Reload the users list
        loadUsers();
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('Error deleting user: ' + error.message);
    }
}