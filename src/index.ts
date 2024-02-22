import { registerPlugin } from '@capacitor/core';

import type { ZettlePaymentPlugin } from './definitions';

const ZettlePayment = registerPlugin<ZettlePaymentPlugin>('ZettlePayment', {
  web: () => import('./web').then(m => new m.ZettlePaymentWeb()),
});

export * from './definitions';
export { ZettlePayment };
