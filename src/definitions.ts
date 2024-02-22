export interface ZettlePaymentPlugin {
  initiatePayment(options: { amount: number; currency: string }): Promise<any>;
  initialize(options: { devMode: boolean }): Promise<any>;
}
