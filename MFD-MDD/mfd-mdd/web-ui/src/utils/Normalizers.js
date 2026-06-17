import DateHelper from './DateHelper';

export const phone = (value, previousValue, allValues) => value.replace(/[- ( )]*/g, '');
export const date = function (dateFormat, placeholder, onlyDate = false, value, previousValue, allValues) {
    if (value === placeholder) {
        return undefined;
    }
    return DateHelper.convertStringToServerDateString(value, dateFormat, onlyDate);
}

export const lessThanDate = function (otherField) {
    return function (value, previousValue, allValues) {
        let date = DateHelper.getServerDate(value);
        let otherDate = DateHelper.getServerDate(allValues[otherField]);
        if (date && otherDate) {
            return date.isBefore(otherDate) ? value : previousValue;
        }

        return value;
    }
}
export const greaterThanDate = function (otherField) {
    return function (value, previousValue, allValues) {
        let date = DateHelper.getServerDate(value);
        let otherDate = DateHelper.getServerDate(allValues[otherField]);
        if (date && otherDate) {
            return date.isAfter(otherDate) ? value : previousValue;
        }

        return value;
    }
}
export const stringToInt = (value, previousValue, allValues) => value ? parseInt(value, 10) : value;