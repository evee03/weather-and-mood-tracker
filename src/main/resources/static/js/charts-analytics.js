document.addEventListener('DOMContentLoaded', function() {
    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    Chart.defaults.color = isDark ? '#e0e0e0' : '#666';
    Chart.defaults.borderColor = isDark ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)';

    loadChartsData();
});

function loadChartsData() {
    const statsContainer = document.getElementById('communityStats');
    if (!statsContainer) return;

    fetch('/api/dashboard/stats/community')
        .then(response => {
            if(!response.ok) throw new Error("Error fetching stats");
            return response.json();
        })
        .then(data => {
            if (!data) return;

            if(document.getElementById('scatterHydration')) {
                renderScatterChart('scatterHydration', data.moodVsHydration, 'ml', 'Mood');
            }
            if(document.getElementById('scatterPressure')) {
                renderScatterChart('scatterPressure', data.moodVsPressure, 'hPa', 'Mood');
            }
            if(document.getElementById('scatterHumidity')) {
                renderScatterChart('scatterHumidity', data.moodVsHumidity, '%', 'Mood');
            }
            if(document.getElementById('comparisonChart')) {
                renderComparisonChart(data.myMoodHistory, data.cityMoodHistory);
            }
        });
}

function renderScatterChart(canvasId, dataPoints, xLabel, yLabel) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    new Chart(ctx, {
        type: 'scatter',
        data: {
            datasets: [{
                label: 'Log',
                data: dataPoints,
                backgroundColor: 'rgba(75, 192, 192, 0.6)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    type: 'linear',
                    position: 'bottom',
                    title: { display: true, text: xLabel }
                },
                y: {
                    min: 0, max: 6, // Mood 1-5
                    title: { display: true, text: yLabel }
                }
            },
            plugins: { legend: { display: false } }
        }
    });
}

function renderComparisonChart(myData, cityData) {
    const ctx = document.getElementById('comparisonChart');
    if (!ctx) return;

    const labelMe = ctx.getAttribute('data-label-me') || 'Me';
    const labelCity = ctx.getAttribute('data-label-community') || 'City';

    const allDates = new Set([...Object.keys(myData), ...Object.keys(cityData)]);
    const sortedDates = Array.from(allDates).sort();

    const myValues = sortedDates.map(date => myData[date] || null);
    const cityValues = sortedDates.map(date => cityData[date] || null);

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: sortedDates,
            datasets: [
                {
                    label: labelMe,
                    data: myValues,
                    borderColor: '#FF6384',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    tension: 0.3,
                    spanGaps: true
                },
                {
                    label: labelCity,
                    data: cityValues,
                    borderColor: '#36A2EB',
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    tension: 0.3,
                    spanGaps: true,
                    borderDash: [5, 5]
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { min: 1, max: 5 }
            }
        }
    });
}