import {
    FILTER_DAILY,
    FILTER_LAST_WEEK,
    FILTER_QUARTER,
    FILTER_THIS_MONTH,
    FILTER_THIS_WEEK,
    FILTER_THIS_YEAR,
    FILTER_YES} from '../utils/Constants';

export const BLACKLISTED = [
    {"code": FILTER_YES, "name": "Yes"},
    {"code": "NO", "name": "No"}
];

export const NEWCLIENTS = [
    {"code": FILTER_DAILY, "name": "Today"},
    {"code": FILTER_THIS_WEEK, "name": "New This Week"},
    {"code": FILTER_LAST_WEEK, "name": "New Last Week"},
    {"code": FILTER_THIS_MONTH, "name": "New This Month"},
    {"code": FILTER_QUARTER, "name": "This Quarter"}
];

export const PROBLEMATIC = [
    {"code": 'NO_SHOW_1', "name": "No Show"},
    {"code": 'NO_SHOW_2', "name": "2 No Show"},
    {"code": 'DENIALS', "name": "5 Denials"},
    {"code": 'BLACK_LISTED', "name": "Blacklisted"}
];


export const REPORT_CLIENTS = NEWCLIENTS.concat([
    {"code": FILTER_THIS_YEAR, "name": "This Year"}
]);

export const REPORT_GROUP_BY = [
    {"code": "DAY", "name": "Day"},
    {"code": "WEEK", "name": "Week"},
    {"code": "MONTH", "name": "Month"},
    {"code": "YEAR", "name": "Year"}
];

export const REPORT_POSTING_TYPE = [
    {"code": "PERMANENT_JOB_POSTING", "name": "Permanent"},
    {"code": "TEMPORARY_JOB_POSTING", "name": "Temporary"}
];

export const PAYMENT_TYPES = [
    { "code": 'CC', "name": "Credit Card" },
    { "code": 'ACH', "name": "Bank Account"},
    { "code": 'MANUAL', "name": "Manual"}
];

export const REPORT_UNFILLED_DAYS = [
    {"code": "0_30", "name": "0-30 Days"},
    {"code": "31_60", "name": "31-60 Days"},
    {"code": "61_90", "name": "61-90 Days"},
    {"code": "91_", "name": "91+ Days"},

];

export const REPORT_POSTING_STATUS = [
    {"code": "ACTIVE", "name": "Active"},
    {"code": "EXPIRED", "name": "Expired"}
];