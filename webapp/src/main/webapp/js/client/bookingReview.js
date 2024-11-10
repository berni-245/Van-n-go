document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.starRating').forEach(starContainer => {
        const stars = starContainer.querySelectorAll('.star');
        const ratingInput = starContainer.nextElementSibling;

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
});