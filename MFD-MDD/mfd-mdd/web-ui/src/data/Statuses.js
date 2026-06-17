import {
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
    STATUS_NOT_BOOKING,
    STATUS_PAID,
    STATUS_PARTIAL,
    STATUS_PARTIALLY_FILLED,
    STATUS_PENDING,
    STATUS_REJECTED,
    STATUS_REQUIRES_REVIEW,
    STATUS_SCHEDULED,
    STATUS_SOS,
    STATUS_UNDER_REVIEW
} from '../utils/Constants';

export const STATUSES = [
    {"code": STATUS_ACTIVE, "name": "Active"},
    {"code": STATUS_NEW, "name": "You have applied"},
    {"code": STATUS_INACTIVE, "name": "Inactive"},
    {"code": STATUS_EMAIL_CONFIRMATION_PENDING, "name": "Email Confirmation"},
    {"code": STATUS_APPROVED, "name": "Approved"},
    {"code": STATUS_REJECTED, "name": "Rejected"},
    {"code": STATUS_PENDING, "name": "Pending"},
    {"code": STATUS_REQUIRES_REVIEW, "name": "Requires review"},
    {"code": STATUS_EXPIRED, "name": "Expired"},
    {"code": STATUS_BOOKED, "name": "Invitation sent"},
    {"code": STATUS_INVITED, "name": "You are invited"},
    {"code": STATUS_SCHEDULED, "name": "You accepted interview"},
    {"code": STATUS_UNDER_REVIEW, "name": "You are confirmed"},
    {"code": STATUS_CANCELLED, "name": "Cancelled"}
];

export const PROFESSIONAL = [
    {"code": STATUS_ACTIVE, "name": "Active"},
    {"code": STATUS_INACTIVE, "name": "Inactive"}
];

//NEW, PAID, PENDING, FAILED, FAILED_FINAL, CANCELED, PARTIAL
export const PAYMENTS = [
    {"code": STATUS_NEW, "name": "New"},
    {"code": STATUS_PAID, "name": "Paid"},
    {"code": STATUS_DONE, "name": "Done"},
    {"code": STATUS_PENDING, "name": "Pending"},
    {"code": STATUS_FAILED, "name": "Failed"},
    {"code": STATUS_FAILED_FINAL, "name": "Failed finally"},
    {"code": STATUS_CANCELED, "name": "Canceled"},
    {"code": STATUS_PARTIAL, "name": "Partial"}
];
export const PAYMENTS_FOR_FILTER=Object.assign([], PAYMENTS);

PAYMENTS_FOR_FILTER.splice(2,1);

export const INTERVIEW = [
    {"code": STATUS_INVITED, "name": "Invitation sent"},
    {"code": STATUS_SCHEDULED, "name": "Invitation accepted"},
    {"code": STATUS_REJECTED, "name": "Invitation rejected"},
    {"code": STATUS_COMPLETED, "name": "Completed"},
    {"code": STATUS_CANCELLED, "name": "Cancelled"}
];

export const BOOKING = [
    {"code": STATUS_NOT_BOOKING, "name": ""},
    {"code": STATUS_BOOKING_APPLIED, "name": "Applied"},
    {"code": STATUS_BOOKING_OFFER_SENT, "name": "Offer sent"},
    {"code": STATUS_BOOKING_FILLED, "name": "Filled"}
];


export const ATTENDANCE = [
    {"code": STATUS_NEW, "name": "New"},
    {"code": STATUS_NEED_CHECK_IN, "name": "Need check in"},
    {"code": STATUS_CHECKED_IN, "name": "Checked in"},
    {"code": STATUS_NO_SHOW, "name": "No Show"},
    {"code": STATUS_SOS, "name": "SOS"},
    {"code": STATUS_REJECTED, "name": "Rejected"}
];

export const TEMPORARY_POSTING = {
    system: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_PARTIALLY_FILLED, "name": "Partially filled"},
        {"code": STATUS_FILLED, "name": "Filled"},
        {"code": STATUS_REJECTED, "name": "Rejected"},
        {"code": STATUS_SOS, "name": "SOS"},
        {"code": STATUS_CANCELLED, "name": "Cancelled"}/*,
        {"code": STATUS_DONE, "name": "Done"}*/
    ],
    practice: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_PARTIALLY_FILLED, "name": "Partially filled"},
        {"code": STATUS_FILLED, "name": "Filled"},
        {"code": STATUS_REJECTED, "name": "Rejected"}/*,
        {"code": STATUS_DONE, "name": "Done"}*/
    ],
    professional: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_NEW, "name": "You have applied"},
        {"code": STATUS_BOOKED, "name": "Please accept"},
        {"code": STATUS_ACCEPTED, "name": "Accepted"},
        {"code": STATUS_NEED_CHECK_IN, "name": "Need check in"},
        {"code": STATUS_CHECKED_IN, "name": "Checked in"},
        {"code": STATUS_COMPLETED, "name": "Completed"}
    ]
};

export const PERMANENT_POSTING = {
    system: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_FILLED, "name": "Filled"},
        {"code": STATUS_UNDER_REVIEW, "name": "Under Review"},
        {"code": STATUS_REJECTED, "name": "Rejected"},
        {"code": STATUS_CANCELLED, "name": "Cancelled"}
    ],
    practice: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_UNDER_REVIEW, "name": "Under Review"},
        {"code": STATUS_FILLED, "name": "Filled"},
        {"code": STATUS_REJECTED, "name": "Rejected"}

    ],
    professional: [
        {"code": STATUS_ACTIVE, "name": "Active"},
        {"code": STATUS_NEW, "name": "You have applied"},
        {"code": STATUS_INVITED, "name": "You are invited"},
        {"code": STATUS_SCHEDULED, "name": "Interview is scheduled"},
        {"code": STATUS_BOOKED, "name": "You have an offer"},
        {"code": STATUS_ACCEPTED, "name": "Accepted"},
    ]
};

export function findStatus(findStatus, statusSet = STATUSES) {
    let result = statusSet.filter(function (status) {
        return status.code === findStatus;
    });
    return result && result.length > 0 ? result[0] : result;
}

export function findStatusName(status) {
    let result = findStatus(status);
    return result ? result.name : result;
}