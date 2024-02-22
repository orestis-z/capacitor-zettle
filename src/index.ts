import { registerPlugin } from '@capacitor/core';
import type { ZettlePaymentPlugin } from './definitions';

const ZettlePayment = registerPlugin<ZettlePaymentPlugin>('ZettlePayment', {
  web: () => {
    throw new Error('Web SDK not provided by Zettle. More info at https://developer.zettle.com/');
  },
});

export * from './definitions';
export { ZettlePayment };
