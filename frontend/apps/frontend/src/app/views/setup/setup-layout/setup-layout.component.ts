import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { StepperComponent } from '../../../shared/stepper/stepper.component';

@Component({
  selector: 'app-setup-layout',
  imports: [StepperComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="min-h-screen flex flex-col items-center justify-center gap-6">
      <h1>Proxmox Backup Manager</h1>

      <app-stepper [steps]="steps" [current]="current()" />

      <div class="bg-app-surface rounded-2xl p-10 w-[600px]">
        <ng-content />
      </div>
    </div>
  `,
})
export class SetupLayoutComponent {
  readonly current = input.required<number>();
  readonly steps = ['Create Account', 'Connect Proxmox', 'Backup Target'];
}
