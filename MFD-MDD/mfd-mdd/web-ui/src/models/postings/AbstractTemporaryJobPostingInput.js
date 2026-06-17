export class AbstractTemporaryJobPostingInput {
    id: string;

    constructor(fields?: {
        name?: string,
        practiceLocationId?: string,
        requiredSubcategories?: Array<string>,
        requiredLanguages?: Array<string>,
        startDate?: string,
        endDate?: string,
        preferredCandidateId?: string
    }) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}