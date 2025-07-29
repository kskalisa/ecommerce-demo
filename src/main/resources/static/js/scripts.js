document.addEventListener('DOMContentLoaded', function() {
    // Legacy support for old add-cart buttons
    document.querySelectorAll('.add-cart').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            showToast('Product added to cart!');
        });
    });
    
    // Support for new Buy Now buttons
    document.querySelectorAll('.btn-primary').forEach(function(btn) {
        if (btn.textContent.includes('Buy') || btn.textContent.includes('Shop')) {
            btn.addEventListener('click', function(e) {
                if (!btn.getAttribute('href') || btn.getAttribute('href') === '#') {
                    e.preventDefault();
                    showToast('Proceeding to checkout...');
                }
            });
        }
    });
    
    // Initialize category hover effects
    document.querySelectorAll('.category-card-modern').forEach(function(card) {
        card.addEventListener('mouseenter', function() {
            const icon = this.querySelector('.category-icon-wrapper i');
            if (icon) {
                icon.classList.add('fa-bounce');
                setTimeout(() => {
                    icon.classList.remove('fa-bounce');
                }, 1000);
            }
        });
    });
    
    // Newsletter subscription
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const emailInput = this.querySelector('input[type="email"]');
            if (emailInput && emailInput.value) {
                showToast('Thank you for subscribing to our newsletter!');
                emailInput.value = '';
            }
        });
    }
});

// Helper function to show toast notifications
function showToast(message) {
    // Create toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        toastContainer.style.zIndex = '11';
        document.body.appendChild(toastContainer);
    }
    
    // Create toast
    const toastId = 'toast-' + Date.now();
    const toast = document.createElement('div');
    toast.className = 'toast show';
    toast.id = toastId;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="toast-header">
            <strong class="me-auto">Notification</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
        <div class="toast-body">
            ${message}
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    // Add event listener to close button
    const closeBtn = toast.querySelector('.btn-close');
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            toast.remove();
        });
    }
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.remove();
    }, 3000);
}