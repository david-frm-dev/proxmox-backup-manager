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
      { path: '', pathMatch: 'full', redirectTo: 'nodes' },
    ],
  },
  { path: '**', redirectTo: '' },
];
