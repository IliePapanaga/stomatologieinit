import DateHelper from './DateHelper';
import ObjectHelper from './Object';
import moment from "moment/moment";
import {mask as maskValidator} from './Validators';

export const validateEmail = value => (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value) ? 'Invalid email address' : undefined);

export const baseValidation = function (value) {
    let props = this.props;
    if (props.required) {
        if (!value || value.length <= 0) {
            return 'Required';
        }
    }
    if (!value) return value;

    if (props.minLength) {
        if (value.length < props.minLength) {
            return `Must be more than ${props.minLength} characters`;
        }
    }

    if (props.maxLength) {
        if (value.length > props.maxLength) {
            return `Must be ${props.maxLength} characters or less`;
        }
    }

    if (props.max) {
        if (value > props.max) {
            return `Must be ${props.max} value or less`;
        }
    }

    if (props.min) {
        if (value < props.min) {
            return `Must be more than ${props.min} value`;
        }
    }

    if (props.regexPattern) {
        let currentMask = new RegExp(props.regexPattern);
        return maskValidator.call(this, value, currentMask);
    }
    return undefined;
};
export const matchPassword = (value, values) => (value !== values.password ? "Password don't match" : undefined)
export const mask = (value, mask) => (!mask.test(value) ? "Field's format is wrong" : undefined)
export const agree = (value) => (!value ? "You must agree with the terms and conditions" : undefined)
export const oneShouldBeChecked = function (value) {
    return ((!value) || value.length <= 0 ? "You must select one of the list" : undefined);
}

export const maxOrEqualsThanDate = function (otherField, equals = true) {
    return function (value, values) {
        let date = DateHelper.getServerDate(value);
        let otherDate = DateHelper.getServerDate(ObjectHelper.getValue(values, otherField));
        if (date && otherDate && (date.isBefore(otherDate) || (!equals && date.isSame(otherDate)))) {
            return "Current Date must be more or equal";
        }
        return undefined;
    }
}

export const maxOrEqualsThanTime = function (otherField, equals = true) {
    return function (value, values) {
        try {
            let timeParts = value.split(':');
            let date1 = moment({
                hour: timeParts[0],
                minute: timeParts[1],
                second: 0
            });
            let otherTime = ObjectHelper.getValue(values, otherField);
            timeParts = otherTime.split(':');

            let date2 = moment({
                hour: timeParts[0],
                minute: timeParts[1],
                second: 0
            });

            if (date1 && date2 && (date1.isBefore(date2) || (!equals && date1.isSame(date2)))) {
                return equals ? "Current Time must be more or equal" : "Current Time must be more";
            }
        } catch (ex) {
        }

        return undefined;
    }
}

export const validateCardExpDate = function (value) {
    let errorMessage = 'Invalid card expiration date';
    if (!value) {
        return errorMessage;
    }
    let date = moment({
        month: parseInt(value.substring(0, 2), 10) - 1,
        year: `20${value.substring(2, 4)}`
    });
    return !date.isValid() || date.isBefore(moment()) ? errorMessage : undefined;
}
