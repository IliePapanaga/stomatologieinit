import {PaymentMethod} from './PaymentMethod';

export class CreditCardInput extends PaymentMethod {

    number: string;
    expiration: string;

    constructor(fields?: {
        number?: string,
        expiration?: string
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}