export interface ZettlePaymentPlugin {
  initiatePayment(options: {
    amount: number;
    currency: string;
  }): Promise<void>;
  initialize(options: {
    devMode: boolean;
  }): Promise<void>;
}
