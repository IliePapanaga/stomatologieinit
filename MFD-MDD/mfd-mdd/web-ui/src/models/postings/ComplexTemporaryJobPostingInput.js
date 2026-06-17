import {PublishComplexTemporaryJobPostingInput} from './PublishComplexTemporaryJobPostingInput';

export class ComplexTemporaryJobPostingInput extends PublishComplexTemporaryJobPostingInput {
    id: string;

    constructor(fields?: {
        id?: string,
        name?: string,
        practiceLocationId?: string,
        requiredSubcategories?: Array<string>,
        requiredLanguages?: Array<string>,
        startDate?: string,
        endDate?: string,
        preferredCandidateId?: string,
        jobDays?: Array<JobDayModelInput>
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}