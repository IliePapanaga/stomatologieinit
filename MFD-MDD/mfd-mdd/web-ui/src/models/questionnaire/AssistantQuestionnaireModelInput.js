export class AssistantQuestionnaireModelInput {
    yoeInDental:number;
    constructor(fields?: {yoeInDental?: number}) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}