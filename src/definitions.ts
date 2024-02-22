export interface ZettlePaymentPlugin {
  initialize(options: { devMode: boolean }): Promise<any>;
  initiatePayment(options: { amount: number; currency: string }): Promise<any>;
  initiateRefund(options: {
    amount: number;
    taxAmount: number;
    receiptNumber: string;
  }): Promise<any>;
  showCardReaderSettings(): Promise<any>;
}
