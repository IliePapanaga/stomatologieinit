import {AbstractTemporaryJobPostingInput} from './AbstractTemporaryJobPostingInput';
import {WorkScheduleModelInput} from './WorkScheduleModelInput';


export class PublishWeeklyTemporaryJobPostingInput extends AbstractTemporaryJobPostingInput {
    workSchedules: Array<WorkScheduleModelInput>;

    constructor(fields?: {
        name?: string,
        practiceLocationId?: string,
        requiredSubcategories?: Array<string>,
        requiredLanguages?: Array<string>,
        startDate?: string,
        endDate?: string,
        preferredCandidateId?: string,
        workSchedules?:Array<WorkScheduleModelInput>
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}