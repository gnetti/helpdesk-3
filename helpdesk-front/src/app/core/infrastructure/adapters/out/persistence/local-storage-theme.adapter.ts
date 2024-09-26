import { Injectable } from "@angular/core";
import { ThemeStoragePort } from "@domain/ports/out/theme.storage.port";

@Injectable({
  providedIn: 'root',
})
export class ThemeStorageAdapter implements ThemeStoragePort {
  private readonly THEME_KEY = 'theme';

  setTheme(theme: string): void {
    localStorage.setItem(this.THEME_KEY, theme);
  }

  getTheme(): string | null {
    return localStorage.getItem(this.THEME_KEY);
  }
}
