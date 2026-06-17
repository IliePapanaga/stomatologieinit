export class IgnoreFieldsInfo {
    parent: string;
    children: Array<string>;
    constructor(fields?: {
        parent?: string,
        children?: Array<string>
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}

export class FieldsInfoByModel {
    model: string;
    modelMode: boolean;
    ignoreFields: Array<IgnoreFieldsInfo>;

    constructor(fields?: {
        model?: string,
        modelMode?: boolean,
        ignoreFields?: Array<IgnoreFieldsInfo>
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}