document.addEventListener('DOMContentLoaded', () => {
    new TomSelect("#select-zones", {
        sortField: {
            field: 'text',
            direction: 'asc'
        },
        onItemAdd: function () {
            this.setTextboxValue('');
            this.refreshOptions();
        }
    });

    document.getElementById('img-input').addEventListener('change', (ev) => {
        const fileInput = ev.target;
        const file = fileInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const previewImage = document.getElementById("vehicleImagePreview");
                previewImage.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });
});