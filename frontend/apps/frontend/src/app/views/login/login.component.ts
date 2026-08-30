import { ChangeDetectionStrategy, Component, inject, signal, } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { ThemeService } from '../../services/theme.service';
import { MatDivider } from '@angular/material/list';
import { MatFormField, MatInput, MatLabel, MatPrefix, } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { MatButton, MatMiniFabButton } from "@angular/material/button";

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatLabel,
    MatPrefix,
    MatIcon,
    MatCard,
    MatFormField,
    MatInput,
    MatCardContent,
    MatCardHeader,
    MatDivider,
    MatCardTitle,
    MatCardSubtitle,
    MatButton,
    MatMiniFabButton,
  ],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styles: '',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  public readonly theme = inject(ThemeService);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.router.navigateByUrl("/dashboard");
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.messageFor(err));
      },
    });
  }

  private messageFor(err: HttpErrorResponse): string {
    if (err.status === 401 || err.status === 403)
      return 'Benutzername oder Passwort falsch.';
    if (err.status === 0) return 'Backend nicht erreichbar';
    return `Unerwarteter Fehler (${err.status}).`;
  }
}
