import { Pipe, PipeTransform } from '@angular/core';
import { Theme } from "@core/domain/enums/theme.enum";

@Pipe({
  name: 'themeTitleCase'
})
export class ThemeTitleCasePipe implements PipeTransform {
  transform(value: string | Theme): string {
    if (typeof value === 'string') {
      return value.toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }
    return value.toString().toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  }
}
