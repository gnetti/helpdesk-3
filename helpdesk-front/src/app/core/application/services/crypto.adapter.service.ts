import { Injectable } from "@angular/core";
import { CryptoPort } from "@domain/ports/out/crypto.port";
import { CryptoService } from "@security//crypto.service";

@Injectable({
  providedIn: "root"
})
export class CryptoAdapterService implements CryptoPort {

  constructor(private cryptoService: CryptoService) {
  }

  encrypt(data: string): string {
    return this.cryptoService.encrypt(data);
  }

  decrypt(data: string): string {
    return this.cryptoService.decrypt(data);
  }
}
