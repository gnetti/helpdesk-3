import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {LoginComponent} from "@adapters/in/web/login/login.component";
import {NavComponent} from "@adapters/in/web/nav/nav.component";
import {AuthGuard} from "@adapters/in/web/guards/auth.guard";
import {DashboardComponent} from "@adapters/in/web/dashboard/dashboard.component";
import {PersonListComponent} from "@adapters/in/web/person/person-list/person-list.component";
import {PersonCreateComponent} from "@adapters/in/web/person/person-create/person-create.component";
import {ProfileComponent} from "@adapters/in/web/profile/profile.component";

const routes: Routes = [
  {path: "login", component: LoginComponent},
  {
    path: "",
    component: NavComponent,
    canActivate: [AuthGuard],
    children: [
      {path: "dashboard", component: DashboardComponent},
      {
        path: "persons",
        children: [
          {path: "", component: PersonListComponent},
          {path: "create", component: PersonCreateComponent}
        ]
      },
      {path: "profile", component: ProfileComponent},
      {path: "", redirectTo: "dashboard", pathMatch: "full"}
    ]
  },
  {path: "**", redirectTo: "login"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
