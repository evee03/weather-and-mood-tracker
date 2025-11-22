document.addEventListener('DOMContentLoaded', function() {

    function handleFormSubmit(formId, alertId) {
        const form = document.getElementById(formId);
        const alertBox = document.getElementById(alertId);

        if (!form) return;

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(form);

            fetch(form.action, {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    alertBox.style.display = 'block';

                    if (data.error) {
                        alertBox.className = 'alert alert-error';
                        alertBox.textContent = data.error;
                    } else {
                        alertBox.className = 'alert alert-success';
                        alertBox.textContent = data.message;
                    }

                    setTimeout(() => {
                        alertBox.style.display = 'none';
                    }, 3000);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alertBox.style.display = 'block';
                    alertBox.className = 'alert alert-error';
                });
        });
    }

    handleFormSubmit('moodForm', 'moodAlert');
    handleFormSubmit('hydrationForm', 'hydrationAlert');
    handleFormSubmit('activityForm', 'activityAlert');
});