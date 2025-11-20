document.addEventListener("DOMContentLoaded", function () {
    const calendarGrid = document.getElementById("calendarGrid");
    const monthLabel = document.getElementById("monthLabel");

    const modal = document.getElementById("dayModal");
    const closeModal = document.getElementById("closeModal");
    const selectedDate = document.getElementById("selectedDate");

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

        for (let day = 1; day <= daysInMonth; day++) {
            let cell = document.createElement("div");
            cell.classList.add("day");
            cell.textContent = day;

            let now = new Date();
            if (day === now.getDate() && month === now.getMonth() && year === now.getFullYear()) {
                cell.classList.add("today");
            }

            cell.onclick = () => {
                selectedDate.textContent = `${day}.${month+1}.${year}`;
                modal.style.display = "flex";
            };

            calendarGrid.appendChild(cell);
        }
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
