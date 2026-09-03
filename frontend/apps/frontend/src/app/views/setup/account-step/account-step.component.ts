import { Component, inject } from '@angular/core';
import { SetupLayoutComponent } from '../setup-layout/setup-layout.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthControllerService } from '@lib/api';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { SnackbarService } from "../../../shared/snack-bar/snackbar.service";

@Component({
  selector: 'app-account-setup',
  imports: [SetupLayoutComponent, ReactiveFormsModule, MatButton, MatFormField, MatLabel, MatInput],
  templateUrl: './account-step.component.html',
  styleUrl: './account-step.component.scss',
})
export class AccountStepComponent {
  private readonly api = inject(AuthControllerService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(SnackbarService);

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    email: ['', Validators.email],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required],
  });

  submit(): void {
    if (!this.form.valid) {
      this.snackBar.openSnackBar("Form is invalide!", '', undefined, 'info')
    }
  }
}
