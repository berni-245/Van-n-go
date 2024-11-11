document.addEventListener('DOMContentLoaded', () => {
    const toasts = document.querySelectorAll('.toast-container > .toast');
    toasts.forEach(toast => (new bootstrap.Toast(toast)).show());
})
