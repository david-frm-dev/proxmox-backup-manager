import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ol class="flex items-center gap-2" aria-label="Setup">
      @for (label of steps(); track label; let i = $index) {
      <li [attr.aria-current]="i === current() ? 'step' : null">
        <span class="circle" [class.done]="i < current()" [class.active]="i === current()">
          {{ i < current() ? '✓' : i + 1 }}
        </span>
        <span class="label">{{ label }}</span>
      </li>
      @if (!$last) {
      <span class="connector"></span>
      } }
    </ol>
  `,
})
export class StepperComponent {
  readonly steps = input.required<string[]>();
  readonly current = input.required<number>();
}
