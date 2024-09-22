import {CoolDialogService, CoolDialogResult} from '@angular-cool/dialogs';

export class DialogUtils {
  static async showDeleteConfirmation(dialogService: CoolDialogService, name: string): Promise<boolean> {
    const result: CoolDialogResult = await dialogService.showDialog({
      titleText: 'Confirmação',
      questionText: `Você realmente deseja deletar a pessoa ${name}?`,
      confirmActionButtonText: 'Sim',
      cancelActionButtonText: 'Não',
      confirmActionButtonColor: 'warn'
    });

    return result.isConfirmed;
  }
}
