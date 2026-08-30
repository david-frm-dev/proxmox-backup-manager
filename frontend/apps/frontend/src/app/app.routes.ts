import { Route } from '@angular/router';
import { authGuard } from './auth/auth.guard';

export const appRoutes: Route[] = [
  {
    path: 'login',
    loadComponent: () =>
      import('./views/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'setup/account',
    loadComponent: () => import('./views/setup/account-step/account-step.component').then((m) => m.AccountStepComponent),
  },
  { path: 'setup', pathMatch: 'full', redirectTo: 'setup/account' },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./views/shell/shell.component').then((m) => m.ShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./views/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
