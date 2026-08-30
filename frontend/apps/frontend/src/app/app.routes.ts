import { Route } from '@angular/router';
import { authGuard } from "./auth/auth.guard";

export const appRoutes: Route[] = [
  {
    path: 'login',
    loadComponent: () => import('./views/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./views/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
