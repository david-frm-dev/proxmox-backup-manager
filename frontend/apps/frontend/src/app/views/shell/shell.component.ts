import { Component, effect, inject, signal } from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from "@angular/material/sidenav";
import {
  RouterLink,
  RouterLinkActive,
  RouterModule,
  RouterOutlet,
} from '@angular/router';
import { MatTooltip } from '@angular/material/tooltip';
import { AuthService } from '../../auth/auth.service';

type NavItem = {
  icon: string;
  label: string;
  link?: string;
  children?: NavItem[];
};

@Component({
  selector: 'app-shell',
  imports: [
    RouterModule,
    MatIcon,
    MatSidenav,
    MatSidenavContainer,
    MatSidenavContent,
    MatSidenavContainer,
    MatSidenav,
    MatIcon,
    RouterLink,
    MatTooltip,
    RouterLinkActive,
    MatSidenavContent,
    RouterOutlet,
  ],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  readonly authService = inject(AuthService);

  readonly collapsed = signal(localStorage.getItem('nav-collapsed') === '1');
  readonly openGroup = signal<string | null>(null);
  readonly nav: NavItem[] = [
    { icon: 'dashboard', label: 'Dashboard', link: '/dashboard' },
    { icon: 'apartment', label: 'Nodes', link: '/nodes' },
    { icon: 'hourglass_empty', label: 'Backup Jobs', link: '/backup-jobs' },
    { icon: 'backup_table', label: 'Backup Records', link: '/backup-records' },
    { icon: 'settings_backup_restore', label: 'Restore', link: '/restore' },
    { icon: 'restart_alt', label: 'Recovery', link: '/recovery' },
    {
      icon: 'key',
      label: 'Credentials',
      children: [
        { icon: '', label: 'Users', link: '/users' },
        { icon: '', label: 'Groups', link: '/groups' },
      ],
    },
    { icon: 'settings', label: 'Settings', link: '/settings' },
  ];

  constructor() {
    effect(() =>
      localStorage.setItem('nav-collapsed', this.collapsed() ? '1' : '0')
    );
  }

  toggle() {
    this.collapsed.update((v) => !v);
    if (this.collapsed()) this.openGroup.set(null);
  }
}
