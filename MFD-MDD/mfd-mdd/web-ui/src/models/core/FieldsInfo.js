import {Field} from "./QueryInfo";

export class FieldsInfo {
    select: Array<Field>;
    model: string;
    subClass: boolean;

    constructor(fields?: {
        select?: Array<Field>,
        model?: string,
        subClass?: boolean
    }) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}