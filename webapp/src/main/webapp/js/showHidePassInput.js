document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.password-input-group').forEach(group => {
        const passInput = group.querySelector('input');
        const showPassBtn = group.querySelector('button');
        const showIcon = showPassBtn.children[0];
        const hideIcon = showPassBtn.children[1];
        showPassBtn.addEventListener('click', () => {
            if (passInput.type === 'password') {
                passInput.type = 'text';
                showIcon.classList.add('d-none');
                hideIcon.classList.remove('d-none');
            } else {
                passInput.type = 'password';
                showIcon.classList.remove('d-none');
                hideIcon.classList.add('d-none');
            }
        })
    })
});