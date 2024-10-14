import { AbstractControl, ValidatorFn } from "@angular/forms";

export function bigDecimalValidator(min: number, max: number): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const value = control.value;
    if (value === null || value === undefined || value === "") {
      return null;
    }
    const num = parseFloat(value);
    if (isNaN(num)) {
      return { "bigDecimal": { value: control.value, message: "Deve ser um número válido" } };
    }

    if (num < min) {
      return { "min": { value: control.value, message: `O valor deve ser pelo menos ${min}` } };
    }
    if (num > max) {
      return { "max": { value: control.value, message: `O valor não pode ser maior que ${max}` } };
    }
    return null;
  };
}
