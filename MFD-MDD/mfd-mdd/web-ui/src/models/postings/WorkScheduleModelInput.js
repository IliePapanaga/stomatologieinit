export class WorkScheduleModelInput {
    _enabled: boolean;
    weekDay: string;
    startTime: string;
    endTime: string;

    constructor(fields?: {
        _enabled?: boolean,
        weekDay?: string,
        startTime?: string,
        endTime?: string
    }) {
        if (fields) {
            Object.assign(this, fields);
        }
    }
}