import React from 'react';
import {
    ALERT_TYPE_ARRIVING_IN_MIN,
    ALERT_TYPE_CANNOT_COME,
    ALERT_TYPE_COUPLE_MIN_LATE,
    ALERT_TYPE_STACK_IN_TRAFFIC,
    STATUS_ACCEPTED,
    STATUS_ACTIVE,
    STATUS_APPROVED,
    STATUS_BOOKED,
    STATUS_BOOKING_APPLIED,
    STATUS_BOOKING_FILLED,
    STATUS_BOOKING_OFFER_SENT,
    STATUS_CANCELED,
    STATUS_CANCELLED,
    STATUS_CHECKED_IN,
    STATUS_COMPLETED,
    STATUS_DONE,
    STATUS_EMAIL_CONFIRMATION_PENDING,
    STATUS_EXPIRED,
    STATUS_FAILED,
    STATUS_FAILED_FINAL,
    STATUS_FILLED,
    STATUS_INACTIVE,
    STATUS_INVITED,
    STATUS_NEED_CHECK_IN,
    STATUS_NEW,
    STATUS_NO_SHOW,
    STATUS_PAID,
    STATUS_PARTIAL,
    STATUS_PARTIALLY_FILLED,
    STATUS_PENDING,
    STATUS_REJECTED,
    STATUS_REQUIRES_REVIEW,
    STATUS_SCHEDULED,
    STATUS_SOS,
    STATUS_UNDER_REVIEW,
    CERTIFICATE_TYPE_TITLES
} from './Constants';
import DateHelper, {serverDateFormat, uiDateFormat, uiDateFormatMonthDayYear} from './DateHelper';
import {findStatus, STATUSES} from '../data/Statuses';
import String from '../utils/String';
import moment from "moment/moment";

export default class Renderer {

    static getRowStatusClassPrefix(status) {
        switch (status) {
            case STATUS_ACTIVE:
            case STATUS_DONE:
            case STATUS_PAID:
                return 'active';

            case STATUS_APPROVED:
            case STATUS_ACCEPTED:
            case STATUS_COMPLETED:
            case STATUS_BOOKING_APPLIED:
                return 'appr';

            case STATUS_INVITED:
                return 'invited';

            case STATUS_CANCELLED:
            case STATUS_CANCELED:
            case STATUS_FAILED_FINAL:
            case STATUS_BOOKING_FILLED:
                return 'cancelled';

            case STATUS_INACTIVE:
            case STATUS_REJECTED:
            case STATUS_PARTIALLY_FILLED:
            case STATUS_FILLED:
                return 'cancelled';

            case STATUS_SCHEDULED:
                return 'scheduled';

            case STATUS_CHECKED_IN:
            case STATUS_BOOKING_OFFER_SENT:
                return 'booked';

            case STATUS_BOOKED:
                return 'bookedOrange';

            case STATUS_PENDING:
            case STATUS_UNDER_REVIEW:
            case STATUS_NEED_CHECK_IN:
            case STATUS_NO_SHOW:
            case STATUS_PARTIAL:
                return 'pending';

            case STATUS_EXPIRED:
            case STATUS_REQUIRES_REVIEW:
                return 'expired';

            case STATUS_SOS:
                return 'sos';

            case STATUS_NEW:
                return 'new';

            case STATUS_EMAIL_CONFIRMATION_PENDING:
            case STATUS_FAILED:
                return 'err';
            default:
                break;

        }
    }

    static getStatusRenderer(statusSet = STATUSES) {
        return function (value, cellClass, row, columnIndex, rowIndex) {
            let classPrefix = Renderer.getRowStatusClassPrefix(value)

            let status = findStatus(value, statusSet);

            return <div><span class={`row__status-${classPrefix}`}>{status ? status.name : 'N/A'}</span></div>;
        }
    }

    static getStatusCenterRenderer(statusSet = STATUSES) {
        return function (value, cellClass, row, columnIndex, rowIndex) {
            let classPrefix = Renderer.getRowStatusClassPrefix(value)

            let status = findStatus(value, statusSet);

            return <div class="center__status"><span
                class={`row__status-${classPrefix}`}>{status ? status.name : 'N/A'}</span></div>;
        }
    }

    static getRequiredRenderer(reversed = false) {
        return function (value, cellClass, row, columnIndex, rowIndex) {
            return <div class="checkbox__row-wrapper">
                {(reversed ? !value : value) ?
                    <div class="checkbox__row checkbox__row-req-true">
                        <input type="checkbox" id="checked" checked={true}/>
                        <label for="checked"></label>
                    </div>
                    :
                    <div class="checkbox__row checkbox__row-req-false">
                        <input type="checkbox" id="checked" checked={false}/>
                        <label for="checked"></label>
                    </div>
                }
            </div>;
        }

    }

    static getPhoneRenderer(value, cellClass, row, columnIndex, rowIndex) {
        if (value && value.length === 10) {
            let p1 = value.substr(0, 3)
            let p2 = value.substr(3, 3);
            let p3 = value.substr(6, 4);
            value = `(${p1}) ${p2}-${p3}`;
        }
        return <div><span class={cellClass}>{value}</span></div>;
    }

    static getDateRenderer(value, cellClass, row, columnIndex, rowIndex) {
        if (value) {
            value = DateHelper.convertServerDateStringToString(value, uiDateFormat);
        }
        return <div class={cellClass}>{value}</div>;
    }


    static getDateRendererMonthDayYear(value, cellClass, row, columnIndex, rowIndex) {
        if (value) {
            value = DateHelper.convertServerDateStringToString(value, uiDateFormatMonthDayYear);
        }
        return <div class={cellClass}>{value}</div>;
    }


    static setTime(date, h, m) {
        date.hour(h);
        date.minute(m);
    }

    static getDatePostingRenderer(value, cellClass, row, columnIndex, rowIndex, dateIndex) {
        if (value) {

            let date = moment(value, serverDateFormat);
            if (date) {

                let times: Array<string> = undefined;
                switch (dateIndex) {
                    case 'startDate':
                        if (row['startTime'] && row['startTime'].indexOf(':') >= 0) {
                            times = row['startTime'].split(':');
                        }
                        break;
                    case 'endDate':
                        if (row['endTime'] && row['endTime'].indexOf(':') >= 0) {
                            times = row['endTime'].split(':');
                        }
                        break;
                    default:
                        break;
                }
                if (times) {
                    Renderer.setTime(date, ...times);
                    value = date.format(uiDateFormat);
                } else {
                    value = date.format(uiDateFormatMonthDayYear);
                }

            }
        }
        return <div><span class={cellClass}>{value}</span></div>;
    }

    static getAlertRenderer(onSendAlert, enabledFn, scope) {
        return function (value, cellClass, row, columnIndex, rowIndex) {
            return <div class="row__alert-wrapper">
                <div class="row__alert">


                    {((!enabledFn(row)) || (!row.applicationId) || ![STATUS_NEED_CHECK_IN].includes(row.applicationStatus)) &&
                    <div class="row__alert-icon"></div>}

                    {(enabledFn(row)) && row.applicationId && [STATUS_NEED_CHECK_IN].includes(row.applicationStatus) &&
                    <div class="row__alert-icon row__alert-icon-active">

                        <div class="row__alert-list">
                            <ul>
                                <li onClick={onSendAlert.bind(scope, ALERT_TYPE_ARRIVING_IN_MIN, row)}>Parking. Will be there shortly</li>
                                <li onClick={onSendAlert.bind(scope, ALERT_TYPE_COUPLE_MIN_LATE, row)}>15 minutes late</li>
                                <li onClick={onSendAlert.bind(scope, ALERT_TYPE_STACK_IN_TRAFFIC, row)}>Sorry, stuck in traffic. Will be there within the hour</li>
                                <li onClick={onSendAlert.bind(scope, ALERT_TYPE_CANNOT_COME, row)}>Please accept my apologies, but I cannot make it today</li>
                            </ul>
                        </div>

                    </div>}
                    <p>{String.clearUndersores(value)}</p></div>
            </div>;
        }
    }

    static getRatingRenderer(value, cellClass, row, columnIndex, rowIndex) {
        value = Number(value);
        if (value > 0) {
            return <div class="row__title-center"><span class="row__title-rating">{(value || 0).toFixed(1)}</span>
            </div>;
        }
        return <div class="row__title-center"><span class="row__title-rating-disabled">{value}</span></div>;
    }

    static getCoastRenderer(value, cellClass, row, columnIndex, rowIndex) {
        return <div class="row__title-center"><span class="row__title-coast">{value}</span></div>;
    }

    static getCertificateTypeTitle(titles = CERTIFICATE_TYPE_TITLES) {
        return function (value, cellClass, row, columnIndex, rowIndex) {
            const title = titles.find(t => t.code === value);

            value = title ? title.title : value;

            return <div class={cellClass}>{String.clearUndersores(value)}</div>;
        }
    }

    static clearUndersores(value){
        return function (value, cellClass, row, columnIndex, rowIndex) {
            return <div class={cellClass}>{String.clearUndersores(value)}</div>;
        }
    }
}

