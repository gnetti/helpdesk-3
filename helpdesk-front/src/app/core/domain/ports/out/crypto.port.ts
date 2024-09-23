export interface CryptoPort {
  encrypt(data: string): string;
  decrypt(data: string): string;
}
