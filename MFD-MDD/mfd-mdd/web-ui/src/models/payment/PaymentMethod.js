export class PaymentMethod {
    id: string;
    preferred: boolean;
    label: string;

    constructor(fields?: {
        id?: string,
        preferred?: boolean,
        label?: string
    }) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}