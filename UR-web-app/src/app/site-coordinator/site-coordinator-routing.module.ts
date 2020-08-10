import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {AccountModule} from './account/account.module';
import {LocationModule} from './location/location.module';
import {UserModule} from './user/user.module';
import {DashboardHeaderComponent} from './dashboard-header/dashboard-header.component';
import {StudiesModule} from './studies/studies.module';

const routes: Routes = [
  {
    path: '',
    component: SiteCoordinatorComponent,
    children: [
      {
        path: 'accounts',
        loadChildren: async (): Promise<AccountModule> =>
          import('./account/account.module').then((m) => m.AccountModule),
      },
      {
        path: 'locations',
        loadChildren: async (): Promise<LocationModule> =>
          import('./location/location.module').then((m) => m.LocationModule),
      },
      {
        path: 'users',
        loadChildren: async (): Promise<UserModule> =>
          import('./user/user.module').then((m) => m.UserModule),
      },
      {
        path: '',
        component: DashboardHeaderComponent,
        children: [
          {
            path: 'studies',
            loadChildren: async (): Promise<StudiesModule> =>
              import('./studies/studies.module').then((m) => m.StudiesModule),
          },
          {
            path: '',
            redirectTo: 'studies',
            pathMatch: 'full',
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SiteCoordinatorRoutingModule {}
