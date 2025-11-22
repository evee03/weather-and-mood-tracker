document.addEventListener('DOMContentLoaded', function() {
    const mapElement = document.getElementById('map');

    if (!mapElement) {
        return;
    }

    const city = mapElement.getAttribute('data-city');
    const isCustomCity = mapElement.getAttribute('data-city-custom') === 'true';

    const labelUserCity = mapElement.getAttribute('data-label-user-city');
    const labelDefaultCity = mapElement.getAttribute('data-label-default-city');
    const labelErrorLoading = mapElement.getAttribute('data-label-error-loading');

    const locationLabel = isCustomCity ? labelUserCity : labelDefaultCity;

    fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(city)}&format=json&limit=1`)
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                const lat = data[0].lat;
                const lon = data[0].lon;

                const map = L.map('map').setView([lat, lon], 12);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                }).addTo(map);

                L.marker([lat, lon]).addTo(map)
                    .bindPopup(`${locationLabel}: <b>${city}</b>`)
                    .openPopup();
            } else {
                console.error('Nie udało się znaleźć współrzędnych dla miasta:', city);
                mapElement.innerHTML = labelErrorLoading;
            }
        })
        .catch(error => {
            console.error('Błąd ładowania mapy:', error);
            mapElement.innerHTML = labelErrorLoading;
        });
});

function toggleCityEdit() {
    const display = document.getElementById('cityDisplay');
    const edit = document.getElementById('cityEdit');

    if (display.style.display === 'none') {
        display.style.display = 'inline';
        edit.style.display = 'none';
    } else {
        display.style.display = 'none';
        edit.style.display = 'inline';
    }
}

function saveCity() {
    const newCity = document.getElementById('newCityInput').value;

    fetch('/api/settings/city', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ city: newCity })
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert("Nie znaleziono takiego miasta!");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Wystąpił błąd.");
        });
}