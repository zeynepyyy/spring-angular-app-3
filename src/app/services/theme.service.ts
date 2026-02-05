import { Injectable, signal, effect } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    isDarkMode = signal<boolean>(false);

    constructor() {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme === 'dark') {
            this.setDarkMode(true);
        }

        // Effect to update body class automatically
        effect(() => {
            if (this.isDarkMode()) {
                document.body.classList.add('dark-mode');
                localStorage.setItem('theme', 'dark');
            } else {
                document.body.classList.remove('dark-mode');
                localStorage.setItem('theme', 'light');
            }
        });
    }

    toggle() {
        this.isDarkMode.update(v => !v);
    }

    setDarkMode(value: boolean) {
        this.isDarkMode.set(value);
    }
}
