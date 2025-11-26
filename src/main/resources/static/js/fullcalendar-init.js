document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('calendar');
    let currentModalDate = null;

    const calendar = new FullCalendar.Calendar(calendarEl, {
        themeSystem: 'bootstrap5',
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next',
            center: 'title',
            right: 'today'
        },
        locale: document.body.getAttribute('data-language') || 'pl',
        buttonText: document.body.getAttribute('data-language') === 'pl' ? { today: 'Dzisiaj' } : undefined,
        height: 'auto',
        contentHeight: 450,
        selectable: true,

        events: {
            url: '/api/dashboard/calendar/events',
            failure: function() {
                console.error('Błąd pobierania zdarzeń kalendarza');
            }
        },

        dateClick: function(info) {
            handleDayClick(info.date);
        }
    });

    calendar.render();

    window.handleDayClick = function(date) {
        const modal = new bootstrap.Modal(document.getElementById('dayModal'));
        const offset = date.getTimezoneOffset() * 60000;
        const localDate = new Date(date.getTime() - offset);
        currentModalDate = localDate.toISOString().split('T')[0];

        document.getElementById('modalDateTitle').textContent = currentModalDate;

        const today = new Date(); today.setHours(0,0,0,0);
        const clickedDate = new Date(date); clickedDate.setHours(0,0,0,0);

        document.getElementById('futureView').style.display = 'none';
        document.getElementById('readOnlyView').style.display = 'none';
        document.getElementById('editView').style.display = 'none';

        if (clickedDate > today) {
            document.getElementById('futureView').style.display = 'block';
        } else if (clickedDate.getTime() === today.getTime()) {
            checkIfDataExistsAndOpen(currentModalDate, true);
        } else {
            checkIfDataExistsAndOpen(currentModalDate, false);
        }
        modal.show();
    }

    function checkIfDataExistsAndOpen(dateString, isToday) {
        fetch(`/api/dashboard/log/${dateString}`)
            .then(res => res.json())
            .then(data => {
                if (data.moodLevel || data.hydrationMl > 0 || (data.activities && data.activities.length > 0)) {
                    document.getElementById('readOnlyView').style.display = 'block';
                    fillReadOnlyView(data);
                } else {
                    if (isToday) {
                        document.getElementById('editView').style.display = 'block';
                        resetForm();
                    } else {
                        document.getElementById('readOnlyView').style.display = 'block';
                        fillReadOnlyView(data);
                    }
                }
            })
            .catch(() => {
                if (isToday) {
                    document.getElementById('editView').style.display = 'block';
                    resetForm();
                }
            });
    }

    function fillReadOnlyView(data) {
        const nodata = document.getElementById('readOnlyView').getAttribute('data-nodata-text');

        document.getElementById("roMood").textContent = data.moodLevel ? `${data.moodLevel}/5` : '-';
        const emojis = [
            '?',
            '<i class="bi bi-emoji-frown text-danger"></i>',
            '<i class="bi bi-emoji-expressionless text-warning"></i>',
            '<i class="bi bi-emoji-neutral text-secondary"></i>',
            '<i class="bi bi-emoji-smile text-info"></i>',
            '<i class="bi bi-emoji-laughing text-success"></i>'
        ];
        document.getElementById("roMoodEmoji").innerHTML = data.moodLevel ? emojis[data.moodLevel] : '';
        document.getElementById("roComment").textContent = data.moodComment || '';

        if (data.temperature !== null) {
            const weatherEmoji = getWeatherEmoji(data.weatherType);
            document.getElementById("roWeather").textContent = `${data.temperature}°C ${weatherEmoji}`;
            document.getElementById("roHumidity").textContent = data.humidity || '-';
            document.getElementById("roPressure").textContent = data.pressure || '-';
            document.getElementById("roCity").textContent = data.city || 'Nieznane';
        } else {
            document.getElementById("roWeather").textContent = nodata;
            document.getElementById("roHumidity").textContent = '-';
            document.getElementById("roPressure").textContent = '-';
            document.getElementById("roCity").textContent = '-';
        }

        document.getElementById("roWater").textContent = data.hydrationMl || 0;

        if (data.activities && data.activities.length > 0) {
            const icons = data.activities.map(a => getActivityEmoji(a)).join(" ");
            document.getElementById("roActivity").textContent = icons;
        } else {
            document.getElementById("roActivity").textContent = nodata;
        }

        prefillForm(data);
    }

    window.enableEditMode = function() {
        document.getElementById('readOnlyView').style.display = 'none';
        document.getElementById('editView').style.display = 'block';
    }

    const commentInput = document.getElementById('commentInput');
    const commentError = document.getElementById('commentError');
    const maxLength = 200;
    const tooLongMsgTemplate = commentError.dataset.msgTooLong;

    function validateComment() {
        if (commentInput.value.length > maxLength) {
            const msg = tooLongMsgTemplate.replace('{0}', maxLength);
            commentError.textContent = msg;
            commentError.style.display = 'block';
            return false;
        } else {
            commentError.textContent = '';
            commentError.style.display = 'none';
            return true;
        }
    }

    function prefillForm(data) {
        resetForm();
        if (data.moodLevel) {
            const radio = document.querySelector(`input[name="moodLevel"][value="${data.moodLevel}"]`);
            if(radio) radio.checked = true;
        }

        commentInput.value = data.moodComment || '';
        validateComment();
        commentInput.addEventListener('input', validateComment);

        if (window.setHydration) window.setHydration(data.hydrationMl || 0);

        if (data.activities && data.activities.length > 0) {
            const act = data.activities[0];
            const radio = document.getElementById('act-' + act);
            if(radio) radio.checked = true;
        }
    }

    function resetForm() {
        document.getElementById('dailyLogForm').reset();
        if (window.setHydration) window.setHydration(0);
        document.querySelectorAll('.activity-radio').forEach(cb => cb.checked = false);
    }

    window.saveDailyLog = function() {
        if (!validateComment()) return;

        const moodRadio = document.querySelector('input[name="moodLevel"]:checked');

        const activityRadio = document.querySelectorAll('input[name="activity"]:checked');
        const activities = Array.from(activityRadio).map(cb => cb.value);


        const payload = {
            date: currentModalDate,
            moodLevel: moodRadio ? parseInt(moodRadio.value) : null,
            moodComment: document.getElementById('commentInput').value,
            hydrationMl: parseInt(document.getElementById('hydrationInput').value) || 0,
            activities: activities
        };

        fetch('/api/dashboard/log', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        }).then(response => {
            if (response.ok) {
                bootstrap.Modal.getInstance(document.getElementById('dayModal')).hide();
                calendar.refetchEvents();
                location.reload();
            } else {
                alert("Błąd zapisu!");
            }
        });
    }

    window.deleteDay = function() {
        if(!confirm("Czy usunąć wpis?")) return;
        fetch(`/api/dashboard/log/${currentModalDate}`, { method: 'DELETE' })
            .then(response => {
                if (response.ok) {
                    bootstrap.Modal.getInstance(document.getElementById('dayModal')).hide();
                    calendar.refetchEvents();
                    location.reload();
                } else { alert("Błąd!"); }
            });
    }

    function getWeatherEmoji(type) {
        const map = { 'SUNNY': '☀️', 'CLOUDY': '☁️', 'RAINY': '🌧️', 'SNOWY': '❄️', 'STORMY': '⛈️', 'WINDY': '💨', 'FOGGY': '🌫️' };
        return map[type] || '';
    }

    function getActivityEmoji(type) {
        const map = { 'WALKING': '🚶', 'RUNNING': '🏃', 'CYCLING': '🚴', 'SWIMMING': '🏊', 'GYM': '🏋️', 'YOGA': '🧘', 'BALLGAME': '⚽', 'OTHER': '🤸' };
        return map[type] || '🏆';
    }

});