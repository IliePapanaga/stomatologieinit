export class JobDayModelInput {
    date: string;
    excluded: boolean;
    startTime: string;
    endTime: string;

    constructor(fields?: {
        date?: string,
        excluded?: boolean,
        startTime?: string,
        endTime?: string
    }) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}