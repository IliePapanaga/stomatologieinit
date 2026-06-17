import {PublishSimpleTemporaryJobPostingInput} from './PublishSimpleTemporaryJobPostingInput';

export class SimpleTemporaryJobPostingInput extends PublishSimpleTemporaryJobPostingInput {
    id: string;

    constructor(fields?: {
        id?: string,
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