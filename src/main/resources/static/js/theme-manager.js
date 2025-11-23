class ThemeManager {
    init() {
        const serverTheme = document.documentElement.getAttribute('data-bs-theme');
        const savedTheme = localStorage.getItem('theme');
        const themeToApply = serverTheme || savedTheme || 'light';

        this.applyTheme(themeToApply);
        this.updateActiveButtons(themeToApply);
    }

    changeTheme(theme) {
        console.log('Zmieniam motyw na:', theme);

        localStorage.setItem('theme', theme);

        this.applyTheme(theme);
        this.updateActiveButtons(theme);

        if (!document.body.classList.contains('auth-page')) {
            fetch('/api/settings/theme', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ theme: theme.toUpperCase() })
            }).catch(err => console.error('Błąd zapisu motywu:', err));
        }
    }

    applyTheme(theme) {
        document.documentElement.setAttribute('data-bs-theme', theme);
    }

    updateActiveButtons(theme) {
        const buttons = document.querySelectorAll('[data-theme-value]');

        buttons.forEach(btn => {
            const btnVal = btn.getAttribute('data-theme-value');
            if (btnVal === theme) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }
}

const themeManager = new ThemeManager();
document.addEventListener('DOMContentLoaded', () => themeManager.init());

function changeTheme(theme) {
    themeManager.changeTheme(theme);
}