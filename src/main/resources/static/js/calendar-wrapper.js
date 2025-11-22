document.addEventListener("DOMContentLoaded", function () {
    const calendarGrid = document.getElementById("calendarGrid");
    const monthLabel = document.getElementById("monthLabel");

    const modal = document.getElementById("dayModal");
    const closeModal = document.getElementById("closeModal");
    const modalDateTitle = document.getElementById("modalDateTitle");

    const futureView = document.getElementById("futureView");
    const readOnlyView = document.getElementById("readOnlyView");
    const editView = document.getElementById("editView");

    const roMood = document.getElementById("roMood");
    const roMoodEmoji = document.getElementById("roMoodEmoji");
    const roComment = document.getElementById("roComment");
    const roWater = document.getElementById("roWater");
    const roActivity = document.getElementById("roActivity");
    const roWeather = document.getElementById('roWeather');

    let currentDate = new Date();

    function renderCalendar() {

        calendarGrid.innerHTML = "";

        dayNames.forEach(d => {
            let el = document.createElement("div");
            el.classList.add("day-name");
            el.textContent = d;
            calendarGrid.appendChild(el);
        });

        let year = currentDate.getFullYear();
        let month = currentDate.getMonth();

        monthLabel.textContent = monthNames[month] + " " + year;

        let firstDayOfMonth = new Date(year, month, 1);
        let daysInMonth = new Date(year, month + 1, 0).getDate();

        let startDay = (firstDayOfMonth.getDay() + 6) % 7;

        for (let i = 0; i < startDay; i++) {
            let empty = document.createElement("div");
            calendarGrid.appendChild(empty);
        }

        let today = new Date();
        today.setHours(0, 0, 0, 0);

        for (let day = 1; day <= daysInMonth; day++) {
            let cell = document.createElement("div");
            cell.classList.add("day");
            cell.textContent = day;

            let cellDate = new Date(year, month, day);
            cellDate.setHours(0, 0, 0, 0);

            if (cellDate.getTime() === today.getTime()) {
                cell.classList.add("today");
            }

            cell.onclick = () => {
                const offset = cellDate.getTimezoneOffset();
                const localDate = new Date(cellDate.getTime() - (offset*60*1000));
                const dateString = localDate.toISOString().split('T')[0];

                modalDateTitle.textContent = dateString;
                futureView.style.display = "none";
                readOnlyView.style.display = "none";
                editView.style.display = "none";

                if (cellDate > today) {
                    futureView.style.display = "block";
                } else if (cellDate.getTime() === today.getTime()) {
                    editView.style.display = "block";
                } else {
                    readOnlyView.style.display = "block";
                    fetchDailyLog(dateString);
                }
                modal.style.display = "block";
            };

            calendarGrid.appendChild(cell);
        }
    }

    function getActivityEmoji(activityName) {
        const activityMap = {
            'WALKING': '🚶',
            'RUNNING': '🏃',
            'CYCLING': '🚴',
            'SWIMMING': '🏊',
            'GYM': '🏋️',
            'YOGA': '🧘',
            'BALLGAME': '⚽',
            'OTHER': '🤸'
        };
        return activityMap[activityName] || '🏆';
    }

    function getWeatherEmoji(weatherType) {
        const weatherMap = {
            'SUNNY': '☀️',
            'CLOUDY': '☁️',
            'RAINY': '🌧️',
            'SNOWY': '❄️',
            'STORMY': '⛈️',
            'WINDY': '💨',
            'FOGGY': '🌫️'
        };
        return weatherMap[weatherType] || '🌡️';
    }

    function fetchDailyLog(dateString) {
        const readOnlyView = document.getElementById('readOnlyView');
        const nodata = readOnlyView.getAttribute('nodata');
        roMood.textContent = "...";
        roMoodEmoji.textContent = "";
        roComment.textContent = "";
        roWater.textContent = "0";
        roActivity.textContent = "...";

        fetch(`/api/dashboard/log/${dateString}`)
            .then(response => {
                if (!response.ok) throw new Error(nodata);
                return response.json();
            })
            .then(data => {
                if (data.moodLevel) {
                    roMood.textContent = `${data.moodLevel}/5`;
                    const emojis = ['?', '😞', '😕', '😐', '🙂', '😄'];
                    roMoodEmoji.textContent = emojis[data.moodLevel] || '';
                    roComment.textContent = data.moodComment || '';
                } else {
                    roMood.textContent = nodata;
                }

                if (data.temperature !== null && data.weatherType) {
                    const weatherIcon = getWeatherEmoji(data.weatherType);
                    roWeather.textContent = `${data.temperature}°C  ${weatherIcon}`;
                } else {
                    roWeather.textContent = nodata;
                }

                roWater.textContent = data.hydrationMl || 0;

                if (data.activities && data.activities.length > 0) {
                    const activityIcons = data.activities.map(name => getActivityEmoji(name));
                    roActivity.textContent = activityIcons.join("  ");
                } else {
                    roActivity.textContent = nodata;
                }
            })
            .catch(err => {
                roMood.textContent = nodata;
                roWater.textContent = "0";
                roActivity.textContent = nodata;
                roWeather.textContent = nodata;
            });
    }

    document.getElementById("prevMonth").onclick = () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    };

    document.getElementById("nextMonth").onclick = () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    };

    document.getElementById("todayBtn").onclick = () => {
        currentDate = new Date();
        renderCalendar();
    };

    closeModal.onclick = () => modal.style.display = "none";

    window.onclick = (e) => {
        if (e.target === modal) modal.style.display = "none";
    };

    renderCalendar();
});
