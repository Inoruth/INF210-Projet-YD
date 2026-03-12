// Handle login form submission
function handleLogin(event) {
    event.preventDefault();
    
    const form = document.getElementById('loginForm');
    const formData = new FormData(form);
    const messageDiv = document.getElementById('loginMessage');
    
    // Create JSON object from form data
    const credentials = {
        mail: formData.get('mail'),
        password: formData.get('password')
    };
    
    // Send login request
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            messageDiv.className = 'alert alert-success';
            messageDiv.textContent = 'Login successful! Welcome ' + data.usertype;
            messageDiv.classList.remove('d-none');
            
            // Close modal after 1 second
            setTimeout(() => {
                const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                modal.hide();
                form.reset();
                messageDiv.classList.add('d-none');
                
                // Reload page to render Thymeleaf template with session data
                location.reload();
            }, 1000);
          
        } else {
            messageDiv.className = 'alert alert-danger';
            messageDiv.textContent = data.message || 'Invalid credentials';
            messageDiv.classList.remove('d-none');
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        messageDiv.className = 'alert alert-danger';
        messageDiv.textContent = 'An error occurred during login';
        messageDiv.classList.remove('d-none');
    });
    
    return false;
}

