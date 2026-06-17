import {AbstractTemporaryJobPostingInput} from './AbstractTemporaryJobPostingInput';
import {JobDayModelInput} from './JobDayModelInput';


export class PublishComplexTemporaryJobPostingInput extends AbstractTemporaryJobPostingInput {
    jobDays: Array<JobDayModelInput>;

    constructor(fields?: {
        name?: string,
        practiceLocationId?: string,
        requiredSubcategories?: Array<string>,
        requiredLanguages?: Array<string>,
        startDate?: string,
        endDate?: string,
        preferredCandidateId?: string,
        jobDays?:Array<JobDayModelInput>
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}