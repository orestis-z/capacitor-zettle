import { WebPlugin } from '@capacitor/core';

import type { ZettlePaymentPlugin } from './definitions';

export class ZettlePaymentWeb extends WebPlugin implements ZettlePaymentPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
