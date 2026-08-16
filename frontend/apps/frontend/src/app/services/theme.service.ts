import { DOCUMENT, effect, inject, Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private root = inject(DOCUMENT).documentElement;
  readonly dark = signal(localStorage.getItem('dark') !== 'false');

  constructor() {
    effect(() => {
      this.root.dataset['theme'] = this.dark() ? 'dark' : 'light';
      localStorage.setItem('dark', String(this.dark()));
    });
  }

  toggle = () => this.dark.update((v) => !v);
}
