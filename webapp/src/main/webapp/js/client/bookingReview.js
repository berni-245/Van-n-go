document.addEventListener('DOMContentLoaded', function () {
    const stars = document.getElementById('star-rating').querySelectorAll('.star');
    const ratingInput = document.getElementById('rating-input');

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const rating = parseInt(star.getAttribute('data-value'), 10);
            ratingInput.value = rating;


            stars.forEach(s => s.classList.remove('selected'));
            for (let i = 0; i < rating; i++) {
                stars[i].classList.add('selected');
            }
        });
    });
});