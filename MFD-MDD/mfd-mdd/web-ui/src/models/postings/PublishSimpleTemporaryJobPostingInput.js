import {AbstractTemporaryJobPostingInput} from './AbstractTemporaryJobPostingInput';


export class PublishSimpleTemporaryJobPostingInput extends AbstractTemporaryJobPostingInput {
    startTime: string;
    endTime: string;

    constructor(fields?: {
        name?: string,
        practiceLocationId?: string,
        requiredSubcategories?: Array<string>,
        requiredLanguages?: Array<string>,
        startDate?: string,
        startTime?: string,
        endDate?: string,
        endTime?: string,
        preferredCandidateId?: string
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}