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
    if (this.user && this.user.name) {
      const nameParts = this.user.name.split(' ').filter(part => part.length > 0);
      if (nameParts.length >= 2) {
        return (nameParts[0][0] + nameParts[nameParts.length - 1][0]).toUpperCase();
      } else if (nameParts.length === 1) {
        return nameParts[0][0].toUpperCase();
      }
    }
    return "UD";
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
