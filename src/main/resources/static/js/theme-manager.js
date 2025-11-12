
class ThemeManager {
    constructor() {
        this.settingsMenu = null;
    }

    init() {
        this.settingsMenu = document.getElementById('settingsMenu');
        this.loadSavedTheme();
        this.setupEventListeners();
    }

    toggleSettings() {
        if (this.settingsMenu) {
            this.settingsMenu.classList.toggle('show');
        }
    }

    setupEventListeners() {
        window.onclick = (event) => {
            if (!event.target.matches('.settings-btn')) {
                if (this.settingsMenu && this.settingsMenu.classList.contains('show')) {
                    this.settingsMenu.classList.remove('show');
                }
            }
        };
    }

    changeTheme(theme) {
        console.log('Changing theme to:', theme);

        const isAuthPage = document.body.classList.contains('auth-page');
        const isDashboardPage = document.body.classList.contains('dashboard-page');

        if (isAuthPage) {
            document.body.className = 'auth-page theme-' + theme;
            localStorage.setItem('theme', theme);
        } else if (isDashboardPage) {
            this.changeDashboardTheme(theme);
        }
    }

    changeDashboardTheme(theme) {
        document.body.className = 'dashboard-page theme-' + theme;
        document.body.setAttribute('data-theme', theme);

        fetch('/api/settings/theme', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ theme: theme.toUpperCase() })
        })
            .then(response => {
                if (response.ok) {
                    console.log('Theme saved successfully');
                    location.reload();
                } else {
                    console.error('Failed to save theme');
                }
            })
            .catch(error => {
                console.error('Error saving theme:', error);
            });
    }

    loadSavedTheme() {
        const isAuthPage = document.body.classList.contains('auth-page');
        const isDashboardPage = document.body.classList.contains('dashboard-page');

        if (isAuthPage) {
            const savedTheme = localStorage.getItem('theme');
            if (savedTheme) {
                document.body.className = 'auth-page theme-' + savedTheme;
            }
        } else if (isDashboardPage) {
            const savedTheme = document.body.getAttribute('data-theme');
            if (savedTheme) {
                document.body.className = 'dashboard-page theme-' + savedTheme;
            }
        }
    }
}

const themeManager = new ThemeManager();

document.addEventListener('DOMContentLoaded', function() {
    themeManager.init();
});

function toggleSettings() {
    themeManager.toggleSettings();
}

function changeTheme(theme) {
    themeManager.changeTheme(theme);
}