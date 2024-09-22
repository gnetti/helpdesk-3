export class FormUtils {
  static formatCpf(cpf: string): string {
    return cpf.replace(/\D/g, '').replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  static formatCep(cep: string): string {
    return cep.replace(/\D/g, '').replace(/(\d{5})(\d{3})/, '$1-$2');
  }

  static validateCep(cep: string): boolean {
    return cep.replace(/\D/g, '').length === 8;
  }

  static getPageParams(pageIndex: number, pageSize: number): { page: number; size: number } {
    return {
      page: pageIndex,
      size: pageSize
    };
  }

  static applyFilter(event: Event, dataSource: any): void {
    const filterValue = (event.target as HTMLInputElement).value;
    dataSource.filter = filterValue.trim().toLowerCase();

    if (dataSource.paginator) {
      dataSource.paginator.firstPage();
    }
  }

  static extractLinks(links: any): { [key: string]: string } {
    return Object.entries(links).reduce((acc, [key, value]: [string, any]) => {
      acc[key] = value.href;
      return acc;
    }, {} as { [key: string]: string });
  }

  static validateCpf(cpf: string): boolean {
    cpf = cpf.replace(/[^\d]+/g, '');
    if (cpf.length !== 11 || !!cpf.match(/(\d)\1{10}/)) return false;
    const cpfDigits = cpf.split('').map(el => +el);
    const rest = (count: number) => (cpfDigits
      .slice(0, count-12)
      .reduce((soma, el, index) => (soma + el * (count-index)), 0) * 10) % 11 % 10;
    return rest(10) === cpfDigits[9] && rest(11) === cpfDigits[10];
  }

  static maskCpf(cpf: string): string {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  static unmaskCpf(cpf: string): string {
    return cpf.replace(/[^\d]+/g, '');
  }

  static maskCep(cep: string): string {
    return cep.replace(/(\d{5})(\d{3})/, '$1-$2');
  }

  static unmaskCep(cep: string): string {
    return cep.replace(/[^\d]+/g, '');
  }
}
