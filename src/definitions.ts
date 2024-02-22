export interface ZettlePaymentPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
