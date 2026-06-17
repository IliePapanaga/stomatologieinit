import {PaymentMethod} from './PaymentMethod';

export class BankAccountInput extends PaymentMethod {

    routing: string;
    name: string;
    account: string;

    constructor(fields?: {
        routing?: string,
        name?: string,
        account?: string
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}