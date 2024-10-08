import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { AuthService } from "@application/services/auth.service";
import { User } from "@model//user.model";

@Component({
  selector: "app-menu",
  templateUrl: "./menu.component.html",
  styleUrls: ["./menu.component.css"]
})
export class MenuComponent implements OnInit {

  user: User | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.user = this.authService.getUserFromToken();
  }

  getAvatarColorClass(): string {
    const initials = this.getUserInitials();
    const firstLetter = initials.charAt(0).toUpperCase();
    return `avatar-color-${firstLetter}`;
  }

  getUserInitials(): string {
    if (this.user && this.user.email) {
      const [localPart] = this.user.email.split("@");
      if (localPart.length >= 2) {
        return (localPart[0] + localPart[localPart.length - 1]).toUpperCase();
      }
      return localPart[0].toUpperCase();
    }
    return "U";
  }

  async logout(): Promise<void> {
    try {
      await this.router.navigate(["login"]);
      this.authService.logout();
      this.toast.success("Logout realizado com sucesso", "Logout");
    } catch (error) {
      this.toast.error("Erro ao realizar logout", "Erro");
    }
  }
}
