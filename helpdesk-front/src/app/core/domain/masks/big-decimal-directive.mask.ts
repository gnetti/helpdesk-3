import { Directive, HostListener, ElementRef, OnInit } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appBigDecimalMask]'
})
export class BigDecimalDirectiveMask implements OnInit {
  constructor(private el: ElementRef, private ngControl: NgControl) {}

  ngOnInit() {
    this.formatValue(this.el.nativeElement.value);
  }

  @HostListener('input', ['$event.target.value'])
  onInput(value: string) {
    this.formatValue(value);
  }

  @HostListener('blur')
  onBlur() {
    this.formatValue(this.el.nativeElement.value, true);
  }

  private formatValue(value: string, blur: boolean = false) {
    if (value === '') {
      return;
    }

    let numericValue = value.replace(/[^0-9]/g, '');

    if (numericValue.length === 0) {
      numericValue = '00';
    } else if (numericValue.length === 1) {
      numericValue = '0' + numericValue;
    }

    let integerPart = numericValue.slice(0, -2);
    const decimalPart = numericValue.slice(-2);

    if (integerPart.length === 0) {
      integerPart = '0';
    } else {
      integerPart = integerPart.replace(/^0+/, '');
    }

    let formattedValue = `${integerPart}.${decimalPart}`;

    if (blur) {
      formattedValue = Number(formattedValue).toFixed(2);
    }

    this.ngControl.control?.setValue(formattedValue, { emitEvent: false });
    this.el.nativeElement.value = formattedValue;
  }
}
