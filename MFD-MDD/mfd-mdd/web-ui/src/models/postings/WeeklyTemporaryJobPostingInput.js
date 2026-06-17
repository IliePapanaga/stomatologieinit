import {PublishWeeklyTemporaryJobPostingInput} from './PublishWeeklyTemporaryJobPostingInput';

export class WeeklyTemporaryJobPostingInput extends PublishWeeklyTemporaryJobPostingInput {
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
        workSchedules?:Array<WorkScheduleModelInput>
    }) {
        if (fields) {
            super(fields);
            Object.assign(this, fields);
        }
    }
}