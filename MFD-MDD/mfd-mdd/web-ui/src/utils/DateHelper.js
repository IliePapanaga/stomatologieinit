import moment from 'moment';
import {
    FILTER_DAILY,
    FILTER_LAST_WEEK,
    FILTER_QUARTER,
    FILTER_THIS_MONTH,
    FILTER_THIS_WEEK,
    FILTER_THIS_YEAR,
    TIME_AM,
    TIME_PM
} from './Constants';
import {JobDayModelInput} from "../models/postings/JobDayModelInput";
import Renderer from "./Renderer";

export const serverDateFormat = 'YYYY-MM-DDTHH:mm:ss.SSSZ';
export const serverShortDateFormat = 'YYYY-MM-DD';
export const uiDateFormat = 'MM/DD/YYYY hh:mm A';
export const uiDateFormatMonthDayYear = 'MM/DD/YYYY';

export const defaultStartTime = '08:00';
export const defaultEndTime = '17:00';

export default class DateHelper {
    static getMilliseconds(time) {
        let milliseconds = time.getHours() * 60 * 60 * 1000 + time.getMinutes() * 60 * 1000 + time.getSeconds() * 1000;

        return milliseconds;
    }

    static getTimeParts(milliseconds) {
        if (!milliseconds) {
            return {hours: '-', minutes: '-', a: '-'};
        }

        let hourMilliseconds = 3600000;
        let minMilliseconds = 60000;
        let hours = Math.floor(milliseconds / hourMilliseconds);
        let minutes = Math.floor((milliseconds - hourMilliseconds * hours) / minMilliseconds);
        let a = TIME_AM;

        if (hours > 12) {
            a = TIME_PM;
            hours -= 12;
        }

        return {hours: (hours < 10 ? `0${hours}` : hours), minutes: (minutes < 10 ? `0${minutes}` : minutes), a: a};
    }

    static convertStringToServerDateString(dateString, format, onlyDate = false) {
        let date = dateString ? moment(dateString, format) : null;
        if (date) {
            let formatedDate = date.format(onlyDate ? serverShortDateFormat : serverDateFormat);
            return onlyDate ? formatedDate : `${formatedDate}[UTC]`;
        }
        return null;
    }

    static convertMomentToServerDateString(date, format = serverDateFormat) {
        if (date) {
            let formatedDate = date.format(format);

            if (format === serverDateFormat) {
                formatedDate = `${formatedDate}[UTC]`;
            }

            return formatedDate;

        }
        return null;
    }

    static initNewClientsFiltersData(newclients, filterData, toField, fromField, onlyDate = false) {
        let newClientsFrom = undefined,
            newClientsTo = undefined;
        switch (newclients) {
            case FILTER_DAILY:
                newClientsTo = moment({
                    hour: 23,
                    minute: 59,
                    second: 59
                });
                newClientsFrom = moment({
                    hour: 0,
                    minute: 0,
                    second: 0
                })
                break;
            case FILTER_THIS_WEEK:
                newClientsFrom = moment({hour: 0, minute: 0, seconds: 0}).startOf('week');
                newClientsTo = moment();
                break;
            case FILTER_LAST_WEEK:
                newClientsFrom = moment({hour: 0, minute: 0, seconds: 0}).weekday(-7);
                newClientsTo = moment({hour: 23, minute: 59, seconds: 59}).weekday(0);
                break;
            case FILTER_THIS_MONTH:
                newClientsFrom = moment({hour: 0, minute: 0, seconds: 0}).startOf('month');
                newClientsTo = moment();
                break;
            case FILTER_QUARTER:
                newClientsFrom = moment({hour: 0, minute: 0, seconds: 0}).startOf('quarter');
                newClientsTo = moment();
                break;
            case FILTER_THIS_YEAR:
                newClientsFrom = moment({hour: 0, minute: 0, seconds: 0}).startOf('year');
                newClientsTo = moment();
                break;
            default:
                break;
        }

        filterData[fromField] = this.convertMomentToServerDateString(newClientsFrom, onlyDate ? serverShortDateFormat : serverDateFormat);
        filterData[toField] = this.convertMomentToServerDateString(newClientsTo, onlyDate ? serverShortDateFormat : serverDateFormat);
    }

    static convertServerDateStringToString(serverDateString, format, onlyDate = false) {
        let date = serverDateString ? moment(serverDateString, onlyDate ? serverShortDateFormat : serverDateFormat) : null;
        if (date) {
            return date.format(format);
        }
        return null;
    }

    static convertServerTimeStringToString(serverTimeString, format) {
        let date = moment();
        Renderer.setTime(date, ...serverTimeString.split(":"));
        return date.format(format);
    }

    static setServerDateToEndTime(dateString) {
        let date = moment(dateString, serverDateFormat);
        if (date) {
            date.hour(23);
            date.minute(59);
            date.second(59);
            return date.format(serverDateFormat);
        }
        return null;
    }

    static setServerDateToStartTime(dateString) {
        let date = moment(dateString, serverDateFormat);
        if (date) {
            date.hour(0);
            date.minute(0);
            date.second(0);
            return date.format(serverDateFormat);
        }
        return null;
    }


    static getCurrentDate() {
        return moment();
    }

    static getServerDate(dateString) {
        let date = dateString ? moment(dateString, serverDateFormat) : null;
        if (date) {
            return date;
        }
        return null;
    }

    static getJobDays(startDateString, endDateString, format) {
        let result: Array<JobDayModelInput> = [];
        let startDate = moment(startDateString, format);
        let endDate = moment(endDateString, format);

        while (startDate.isBefore(endDate) || startDate.isSame(endDate)) {
            result.push(new JobDayModelInput({
                excluded: false,
                date: startDate.format(format),
                startTime: defaultStartTime,
                endTime: defaultEndTime,
            }));
            startDate = startDate.add(1, 'day');
        }

        return result;
    }

    static getDaysBetween(dateString1, dateString2) {
        let date1 = moment(dateString1, serverDateFormat);
        let date2 = moment(dateString2, serverDateFormat);
        if (date1 && date2) {
            let days = date2.diff(date1, 'days');
            return days <= 0 ? 1 : days + 1
        }
        return null;
    }

    static getDays(dateString1, dateString2, asMoment = false) {
        let date1 = moment(dateString1, serverDateFormat);
        let date2 = moment(dateString2, serverDateFormat);
        return DateHelper.getMomentsDays(date1, date2, asMoment);
    }

    static getMomentsDays(date1, date2, asMoment = false) {
        let result = [];
        while (date1.isBefore(date2) || date1.isSame(date2)) {
            result.push(asMoment ? date1.clone() : date1.format(serverShortDateFormat));
            date1.add(1, 'day');
        }
        return result;
    }

    static joinDateAndTimeString(dateStr, timeStr, format) {
        if (dateStr) {

            let date = moment(dateStr, serverDateFormat);
            if (date) {

                let times: Array<string> = undefined;
                if (timeStr && timeStr.indexOf(':') >= 0) {
                    times = timeStr.split(':');
                }
                if (times) {
                    Renderer.setTime(date, ...times);
                }
                return date.format(format);
            }
        }
        return null;
    }

    static applyDateRange(owner, toField, fromField, onlyDate) {
        if (owner[fromField] && !owner[toField]) {
            owner[toField] = owner[fromField];
        }

        if (owner[toField] && !owner[fromField]) {
            owner[fromField] = owner[toField];
        }

        if (!onlyDate) {
            if (owner[fromField]) {
                owner[fromField] = DateHelper.setServerDateToStartTime(owner[fromField]);
            }

            if (owner[toField]) {
                owner[toField] = DateHelper.setServerDateToEndTime(owner[toField]);
            }
        }

        return owner

    }
}