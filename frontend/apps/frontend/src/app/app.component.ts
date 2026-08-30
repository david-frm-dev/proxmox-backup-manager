import { ChangeDetectionStrategy, Component, effect, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { MatIcon } from "@angular/material/icon";
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from "@angular/material/sidenav";
import { MatTooltip } from "@angular/material/tooltip";

type NavItem = { icon: string; label: string; link?: string; children?: NavItem[] };

@Component({
  imports: [RouterModule, MatIcon, MatSidenav, MatSidenavContainer, MatSidenavContent,
    MatSidenavContainer,
    MatSidenav,
    MatIcon,
    RouterLink,
    MatTooltip,
    RouterLinkActive,
    MatSidenavContent,
    RouterOutlet],
  selector: 'app-root',
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './app.component.scss',
})
export class AppComponent {
  collapsed = signal(localStorage.getItem('nav-collapsed') === '1');
  openGroup = signal<string | null>(null);

  constructor() {
    effect(() => localStorage.setItem('nav-collapsed', this.collapsed() ? '1' : '0'));
  }

  toggle() {
    this.collapsed.update(v => !v);
    if (this.collapsed()) this.openGroup.set(null);
  }

  nav: NavItem[] = [
    { icon: 'dashboard', label: 'Dashboard', link: '/dashboard' },
    { icon: 'apartment', label: 'Nodes', link: '/nodes' },
    { icon: 'hourglass_empty', label: 'Backup Jobs', link: '/backup-jobs' },
    { icon: 'backup_table', label: 'Backup Records', link: '/backup-records' },
    { icon: 'settings_backup_restore', label: 'Restore', link: '/restore' },
    { icon: 'restart_alt', label: 'Recovery', link: '/recovery' },
    { icon: 'key', label: 'Credentials', children: [
        { icon: '', label: 'Users', link: '/users' },
        { icon: '', label: 'Groups', link: '/groups' },
      ]},
    { icon: 'settings', label: 'Settings', link: '/settings' },
  ];
}
