import { inject, Service } from '@angular/core';
import { MatSnackBar } from "@angular/material/snack-bar";

@Service()
export class SnackbarService {
  private readonly _snackbar = inject(MatSnackBar);

  public openSnackBar(message: string, action: string, duration: number = 1500, panelClass: 'error' | 'success' | 'info'): void {
    this._snackbar.open(message, action, { duration: duration, panelClass: panelClass });
  }

}
