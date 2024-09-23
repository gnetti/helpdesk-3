import { Injectable } from "@angular/core";
import * as CryptoJS from "crypto-js";
import { environment } from "@env/environment";

@Injectable({
  providedIn: "root"
})
export class CryptoService {

  private readonly key = CryptoJS.enc.Base64.parse(environment.secretKey);
  private readonly iv = CryptoJS.enc.Base64.parse(environment.secretIv);

  constructor() {
  }

  encrypt(data: string): string {
    const encrypted = CryptoJS.AES.encrypt(data, this.key, {
      keySize: 256 / 32,
      iv: this.iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
    return encrypted.toString();
  }

  decrypt(data: string): string {
    const decrypted = CryptoJS.AES.decrypt(data, this.key, {
      keySize: 256 / 32,
      iv: this.iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
  }
}
