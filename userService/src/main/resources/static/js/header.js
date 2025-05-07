// Function to load the header
async function loadHeader() {
    try {
        const response = await fetch('components/header.html');
        const headerHtml = await response.text();
        
        // Create a temporary container to parse the HTML
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = headerHtml;
        
        // Extract the header and styles
        const header = tempContainer.querySelector('header');
        const styles = tempContainer.querySelector('style');
        const script = tempContainer.querySelector('script');
        
        // Add the header to the page
        document.body.insertBefore(header, document.body.firstChild);
        
        // Add the styles to the head
        if (styles) {
            document.head.appendChild(styles);
        }

        // Execute the script
        if (script) {
            eval(script.textContent);
            updateAuthButton();
        }
    } catch (error) {
        console.error('Error loading header:', error);
    }
}

// Load the header when the page loads
document.addEventListener('DOMContentLoaded', loadHeader); 