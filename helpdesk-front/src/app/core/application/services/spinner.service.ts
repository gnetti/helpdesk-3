import {Injectable} from '@angular/core';
import {SpinnerComponent} from "@shared/components/spinner/spinner.component";


@Injectable({
  providedIn: 'root',
})
export class SpinnerService {
  private spinner!: SpinnerComponent;

  register(spinner: SpinnerComponent): void {
    this.spinner = spinner;
  }

  show(): void {
    if (this.spinner) {
      this.spinner.show();
    }
  }

  hide(): void {
    if (this.spinner) {
      this.spinner.hide();
    }
  }
}
