export const APP_TYPE_DESKTOP = 'desktop';
export const APP_TYPE_MOBILE = 'mobile';

export const EVENT_START_LOADING_DATA_INTO_GRID = 'START_LOADING_DATA_INTO_GRID';
export const EVENT_FINISH_LOADING_DATA_INTO_GRID = 'FINISH_LOADING_DATA_INTO_GRID';
export const EVENT_CHANGE_SELECTION_IN_GRID = 'CHANGE_SELECTION_IN_GRID';
export const EVENT_VIEW_GET_DATA = 'VIEW_GET_DATA';
export const EVENT_VIEW_SAVE_DATA = 'VIEW_SAVE_DATA';
export const EVENT_VIEW_DELETE_DATA = 'VIEW_DELETE_DATA';

export const EVENT_AUTH_LOGIN = 'AUTH_LOGIN';
export const EVENT_AUTH_IMPERSONATE = 'AUTH_IMPERSONATE';
export const EVENT_AUTH_IMPERSONATE_EXIT = 'AUTH_IMPERSONATE_EXIT';
export const EVENT_AUTH_LOGOUT = 'AUTH_LOGOUT';
export const EVENT_AUTH_LOGIN_PERFORMED = 'AUTH_LOGIN_PERFORMED';
export const EVENT_AUTH_USER_LOADED = 'AUTH_USER_LOADED';
export const EVENT_AUTH = 'AUTH_LOGOUT';
export const EVENT_AUTH_UNAUTHORIZED = 'AUTH_UNAUTHORIZED';
export const EVENT_AUTH_LOGIN_PROCCESSINS = 'AUTH_LOGIN_PROCCESSINS';
export const EVENT_CHANGED_RECAPTCHA_HASH = 'CHANGED_RECAPTCHA_HASH';
export const EVENT_LOADED_VERSION = 'LOADED_VERSION';
export const EVENT_UPDATED_REFERENCES = 'UPDATED_REFERENCES';

export const BUTTON_TYPE_ADD = 'ADD';
export const BUTTON_TYPE_EDIT = 'EDIT';
export const BUTTON_TYPE_EDIT_ON_SELECTION = 'EDIT_ON_SELECTION';
export const BUTTON_TYPE_DELETE = 'DELETE';
export const BUTTON_TYPE_EXPORT = 'EXPORT';

export const GOOGLE_API_KEY = 'AIzaSyDCxUEEDM8KtYo80kj1hNBVDfIA7r63gLM';

export const REST_API_PREFIX = '/api/v1/graphql';

export const REST_API_PREFIX_PUBLIC = '/api/v1/graphql/public';

export const REST_API_PREFIX_SIMPLE = '/api/v1';

export const REST_API_PREFIX_UPLOAD_PHOTO = '/api/v1/user/contact-photo/upload';

export const REST_API_PREFIX_UPLOAD_CERTIFICATE = '/api/v1/upload/certificate';

export const REST_API_PREFIX_VIEW_CERTIFICATE = '/api/v1/certificate/view';

export const REST_API_PREFIX_DOWNLOAD_CERTIFICATE = '/api/v1/certificate/download';

export const REST_API_PREFIX_GET_USER_PHOTO = 'api/v1/user/{0}/contact-photo';

export const RESPONSE_OK_STATUS = 200;

export const DEFAULT_CELL_STYLE = 'row__title';

export const NUMBER_CELL_STYLE = 'row__title row__title-center ';

export const DEFAULT_PER_PAGE = 25;

export const SELECTION_MODE_NONE = 'none';

export const SELECTION_MODE_SINGLE = 'single';

export const SELECTION_MODE_MULTY = 'multy';

export const ROOT_PATH = "/";


export const STATUS_ACTIVE = 'ACTIVE';

export const STATUS_INACTIVE = 'INACTIVE';

export const STATUS_EMAIL_CONFIRMATION_PENDING = 'EMAIL_CONFIRMATION_PENDING';

export const STATUS_APPROVED = 'APPROVED';

export const STATUS_REJECTED = 'REJECTED';

export const STATUS_REQUIRES_REVIEW = 'REQUIRES_REVIEW';

export const STATUS_EXPIRED = 'EXPIRED';

export const STATUS_PENDING = 'PENDING';

export const STATUS_NEW = 'NEW';

export const STATUS_BOOKED = 'BOOKED';

export const STATUS_INVITED = 'INVITED';

export const STATUS_SCHEDULED = 'SCHEDULED';

export const STATUS_UNDER_REVIEW = 'UNDER_REVIEW';

export const STATUS_CANCELLED = 'CANCELLED';

export const STATUS_CANCELED = 'CANCELED';

export const STATUS_COMPLETED = 'COMPLETED';

export const STATUS_PAID = 'PAID';

export const STATUS_FAILED = 'FAILED';

export const STATUS_FAILED_FINAL = 'FAILED_FINAL';

export const STATUS_PARTIALLY_FILLED = 'PARTIALLY_FILLED';

export const STATUS_FILLED = 'FILLED';

export const STATUS_PARTIAL = 'PARTIAL';

export const STATUS_SOS = 'SOS';

export const STATUS_ACCEPTED = 'ACCEPTED';

export const STATUS_CHECKED_IN = 'CHECKED_IN';

export const STATUS_NO_SHOW = 'NO_SHOW';

export const STATUS_NEED_CHECK_IN = 'NEED_CHECK_IN';

export const STATUS_DONE = 'DONE';

export const STATUS_NOT_BOOKING = 'NOT_BOOKING';

export const STATUS_BOOKING_APPLIED = 'BOOKING_APPLIED';

export const STATUS_BOOKING_OFFER_SENT = 'BOOKING_OFFER_SENT';

export const STATUS_BOOKING_FILLED = 'BOOKING_FILLED';


export const SOFTWARES = [
    {value: 'DENTRIX', name: 'Dentrix'},
    //{value: 'Digital', name: 'Digital'},
    {value: 'EAGLESOFT', name: 'Eaglesoft'},
    {value: 'OPEN_DENTAL', name: 'Open Dental'},
    {value: 'PRACTICE_WORKS', name: 'Ortho Practice Works'},
    {value: 'SCANDENT', name: 'Scandent'},
    {value: 'SOFTDENT', name: 'Softdent'},
    {value: 'TRADITIONAL_FILM', name: 'Traditional film'}
];

export const TITLES = [
    {value: 'Mr', name: 'Mr'},
    {value: 'Mrs', name: 'Mrs'},
    {value: 'Ms', name: 'Ms'},
    {value: 'Miss', name: 'Miss'},
    {value: 'Dr', name: 'Dr'}
];

export const TIME_AM = 'AM';
export const TIME_PM = 'PM';

export const PASSWORD_PATTERN_REGEX = /^((?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=()!]).*)|((?=.*[A-Z])(?=.*[a-z])(?=.*\d).*)|((?=.*[A-Z])(?=.*[@#$%^&+=()!])(?=.*\d).*)|((?=.*[a-z])(?=.*[@#$%^&+=()!])(?=.*\d).*)$/;
export const TEXTFIELD_PATTERN_REGEX = /^[a-zA-Z'\s\-]*$/;
export const TEXTFIELD_WITH_SPACE_PATTERN_REGEX = /^[0-9a-zA-Z'\s]*$/;
export const TEXTFIELD_DIGITS_PATTERN_REGEX = /^[0-9]*$/;
export const TEXTFIELD_NUMBERS_PATTERN_REGEX = /^([1-9][0-9]*)|^([0])$/;
export const CARD_NUMBER_PATTERN_REGEX = /^[0-9]{15}|[0-9]{16}$/;


export const WEBSITE_PATTERN_REGEX = /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/;

export const FILTER_DAILY = 'DAILY';
export const FILTER_THIS_WEEK = 'THIS_WEEK';
export const FILTER_LAST_WEEK = 'LAST_WEEK';
export const FILTER_THIS_MONTH = 'THIS_MONTH';
export const FILTER_QUARTER = 'QUARTER';
export const FILTER_YES = 'YES';
export const FILTER_THIS_YEAR = 'THIS_YEAR';
export const FILTER_RANGE = 'RANGE';

export const CATEGORY_ID_FRONT_OFFICE_PERSONNEL = 'FRONT_OFFICE_PERSONNEL';
export const CATEGORY_ID_DENTISTS = 'DENTISTS';
export const CATEGORY_ID_ASSISTANTS = 'ASSISTANTS';
export const CATEGORY_ID_HYGIENISTS = 'HYGIENISTS';
export const SUB_CATEGORY_ID_CPR = 'CPR';
export const SUB_CATEGORY_ID_DA = 'DA';
export const CERTIFICATE_TYPE_DA = 'DAC';
export const SUB_CATEGORY_ID_XRAY = 'XRAY';
export const SUB_CATEGORY_ID_RDA = 'RDA';
export const SUB_CATEGORY_ID_CDA = 'CDA';
export const SUB_CATEGORY_ID_DDS_OR_DMD = 'DDS_OR_DMD';
export const SUB_CATEGORY_ID_DEA = 'DEA';
export const SUB_CATEGORY_ID_RDAEF = 'RDAEF';
export const SUB_CATEGORY_ID_RDAEF1 = 'RDAEF1';
export const SUB_CATEGORY_ID_RDAEF2 = 'RDAEF2';
export const SUB_CATEGORY_ID_RDH = 'RDH';
export const SUB_CATEGORY_ID_RDHAP = 'RDHAP';
export const SUB_CATEGORY_ID_RDH_LASER = 'RDH_LASER';
export const SUB_CATEGORY_ID_DIODE_LASER = 'DIODE_LASER';
export const SUB_CATEGORY_ID_LIABILITY = 'LIABILITY';
export const SUB_CATEGORY_ID_NPI = 'NPI';
export const SUB_CATEGORY_ID_ENDODONTIC_ASSISTANT = 'ENDODONTIC_ASSISTANT';
export const SUB_CATEGORY_ID_ORAL_SURGERY_ASSISTANT = 'ORAL_SURGERY_ASSISTANT';
export const SUB_CATEGORY_ID_ORTHODONTIC_ASSISTANT = 'ORTHODONTIC_ASSISTANT';
export const SUB_CATEGORY_ID_PEDODONTIC_ASSISTANT = 'PEDODONTIC_ASSISTANT';
export const SUB_CATEGORY_ID_PERIODONTAL_ASSISTANT = 'PERIODONTAL_ASSISTANT';

export const CERTIFICATE_TYPE_TITLES = [
    {code: CERTIFICATE_TYPE_DA, title: 'DA Certificate'},
    {code: SUB_CATEGORY_ID_LIABILITY, title: 'LIABILITY INSURANCE'},
    {code: SUB_CATEGORY_ID_DDS_OR_DMD, title: 'DDS/DMD OR SPECIALIST'},
    {code: SUB_CATEGORY_ID_RDA, title: [SUB_CATEGORY_ID_RDA,SUB_CATEGORY_ID_RDAEF,'RDAEF 1','RDAEF 2'].join("/")}
];

export const FILTER_TYPE_DATE = 'FILTER_DATE';
export const FILTER_TYPE_CHECK = 'FILTER_CHECK';
export const FILTER_TYPE_DATE_RANGE = 'FILTER_DATE_RANGE';
export const FILTER_TYPE_SELECT = 'FILTER_SELECT';
export const FILTER_TYPE_LOCATION = 'FILTER_LOCATION';
export const FILTER_TYPE_DROPDOWN = 'FILTER_DROPDOWN';
export const FILTER_TYPE_TOGGLE = 'FILTER_TOGGLE';
export const FILTER_TYPE_SEARCH = 'FILTER_SEARCH';

export const CANDIDATE_ANY = "ANY";
export const CANDIDATE_DIRECT_BOOKING = "DIRECT";
export const POSTING_TYPE_SIMPLE = "SIMPLE";
export const POSTING_TYPE_COMPLEX = "COMPLEX";
export const POSTING_TYPE_WEEKLY = "WEEKLY";

export const LANGUAGE_ENGLISH = "ENGLISH";

export const PAYMENT_TYPE_CREDIT_CARD = "CREDIT_CARD";
export const PAYMENT_TYPE_BANK_ACCOUNT = "BANK_ACCOUNT";

export const INTERVIEW_TYPE_WORKING = "WORKING";
export const INTERVIEW_TYPE_PERSONAL = "PERSONAL";

let data = {};
data[CATEGORY_ID_ASSISTANTS] = [
    SUB_CATEGORY_ID_DA,
    SUB_CATEGORY_ID_RDA,
    SUB_CATEGORY_ID_RDAEF,
    SUB_CATEGORY_ID_RDAEF1,
    SUB_CATEGORY_ID_RDAEF2,
    'ENDODONTIC_ASSISTANT',
    'ORAL_SURGERY_ASSISTANT',
    'ORTHODONTIC_ASSISTANT',
    'PEDODONTIC_ASSISTANT',
    'PERIODONTAL_ASSISTANT'
];

data[CATEGORY_ID_DENTISTS] = [
    'GENERAL_DENTIST',
    'COSMETIC_DENTIST',
    'PEDODONTIST',
    'PROSTHODONTIST',
    'PERIODONTIST',
    'ORTHODONTIST',
    'ENDODONTIST',
    'ORAL_SURGEON',
    'DENTAL_ANESTHESIOLOGIST'
];

data[CATEGORY_ID_HYGIENISTS] = [
    SUB_CATEGORY_ID_RDH,
    SUB_CATEGORY_ID_RDHAP,
    SUB_CATEGORY_ID_RDH_LASER
];

data[CATEGORY_ID_FRONT_OFFICE_PERSONNEL] = [
    'RECEPTIONIST',
    'PATIENT_COORDINATOR',
    'TREATMENT_COORDINATOR',
    'INSURANCE_COORDINATOR',
    'OFFICE_MANAGER',
    'REGIONAL_MANAGER',
    'FINANCIAL_COORDINATOR'
];

export const SUBCATEGORY_ORDERS = data;

export const PAYMENT_OPTION_MODIFY = 'MODIFY';
export const PAYMENT_OPTION_COMPLETE = 'COMPLETE';
export const PAYMENT_OPTION_RUN = 'RUN';
export const PAYMENT_OPTION_CANCEL = 'CANCEL';
export const PAYMENT_OPTION_INSTANT_PAY = 'INSTANT_PAY';
export const PAYMENT_OPTION_PARTIAL = 'PARTIAL';
export const PAYMENT_INSTANT_PAY_BY_NEW_CARD = 'INSTANT_PAY_BY_NEW_CARD';

export const ALERT_TYPE_ARRIVING_IN_MIN = 'ARRIVE_IN_A_MINUTE';
export const ALERT_TYPE_COUPLE_MIN_LATE = 'COUPLE_OF_MINUTES_LATE';
export const ALERT_TYPE_STACK_IN_TRAFFIC = 'ARRIVE_IN_AN_HOUR';
export const ALERT_TYPE_CANNOT_COME = 'CANNOT_COME_TODAY';

export const TRANSPORT_TYPE_SMS = 'SMS';