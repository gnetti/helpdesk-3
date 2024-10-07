import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class SettingsValidator {
  constructor(private toast: ToastrService) {}

  validateForm(isValid: boolean): boolean {
    if (!isValid) {
      this.showFormInvalidError();
      return false;
    }
    return true;
  }

  validateUpdatedSettings(updatedSettings: any): boolean {
    if (Object.keys(updatedSettings).length === 0) {
      this.showNoChangesInfo();
      return false;
    }
    return true;
  }

  showFormInvalidError(): void {
    this.toast.error('Formulário inválido. Por favor, corrija os erros.', 'Erro');
  }

  showNoChangesInfo(): void {
    this.toast.info('Nenhuma alteração detectada.', 'Info');
  }

  showUpdateError(error: any): void {
    const errorMessage = error.error?.message || 'Erro ao atualizar as configurações.';
    this.toast.error(errorMessage, 'Erro');
  }

  showUpdateSuccess(): void {
    this.toast.success('Configurações atualizadas com sucesso!', 'Sucesso');
  }

  showLoadError(): void {
    this.toast.error('Erro ao carregar configurações de usuário.', 'Erro');
  }
}
