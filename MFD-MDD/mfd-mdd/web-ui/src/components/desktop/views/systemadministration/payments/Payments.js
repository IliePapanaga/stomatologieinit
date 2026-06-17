import React from 'react';
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_LOCATION,
    FILTER_TYPE_SELECT,
    SELECTION_MODE_SINGLE,
    NUMBER_CELL_STYLE
} from '../../../../../utils/Constants';

import PaymentOptions, {dialogInfo} from '../../../dialogs/payments/PaymentOptions';
import ViewPayment, {dialogInfo as viewPaymentdialogInfo} from '../../../dialogs/payments/ViewPayment';
import Renderer from '../../../../../utils/Renderer';
import {PAYMENTS, PAYMENTS_FOR_FILTER} from '../../../../../data/Statuses';
import {PAYMENT_TYPES} from '../../../../../data/Filters';
import FindByLocation from '../../common/FindByLocation';
import DateHelper from "../../../../../utils/DateHelper";


class Payments extends FindByLocation {
    initView(props) {
        let configuration = {
            requestInfo: {
                fetchQueryName: 'payments',
                getQueryName: ["payment", "paymentDetails"],
                //addQueryName: "addPracticeLocation",
                updateQueryName: "paymentManualOperation",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: ["PaymentModel", "PaymentDetails"],
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: "PaymentOptionsResponse",
                getQueryParameterName: ["id", "id"],
                reloadAfterEdit:true
            },

            additionalFields: ['practiceId', 'proLastName', 'jobDayId', 'jobInterviewId', 'jobApplicationId'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceLastName',
                    name: 'practiceLastName',
                    title: 'Client Last Name',
                    orderName: 'CLIENT_LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceFirstName',
                    name: 'practiceFirstName',
                    title: 'Client First Name',
                    orderName: 'CLIENT_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },


                {
                    dataIndex: 'office',
                    name: 'office',
                    title: 'Office',
                    orderName: 'PRACTICE_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'label',
                    name: 'label',
                    title: 'Posting',
                    orderName: 'POSTING_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },

                {
                    dataIndex: 'id',
                    name: 'type',
                    title: 'Type',
                    //orderName: 'POSTING_TYPE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer(value, cellClass, row, columnIndex, rowIndex) {
                        value = "Interview";

                        if (row.jobDayId) {
                            value = "Temporary";
                        } else if (row.jobApplicationId) {
                            value = "Permanent";
                        }
                        return <div class={cellClass}>{value}</div>
                    }
                },

                {
                    dataIndex: 'location',
                    name: 'location',
                    title: 'Location',
                    orderName: 'LOCATION',
                    cellClass: DEFAULT_CELL_STYLE
                },

                {
                    dataIndex: 'proFirstName',
                    name: 'proFirstName',
                    title: 'Professional',
                    //orderName: 'CLIENT_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer(proFirstName, cellClass, row, columnIndex, rowIndex) {
                        let value = `${proFirstName} ${row.proLastName}`;
                        return <div class={cellClass}>{value}</div>
                    }
                },

                {
                    dataIndex: 'date',
                    name: 'date',
                    title: 'Date',
                    orderName: 'DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRendererMonthDayYear
                },

                {
                    dataIndex: 'method',
                    name: 'method',
                    title: 'Payment method',
                    orderName: 'METHOD',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(PAYMENT_TYPES)
                },

                {
                    dataIndex: 'amount',
                    name: 'amount',
                    title: 'Amount',
                    orderName: 'AMOUNT',
                    cellClass: NUMBER_CELL_STYLE,
                    renderer: Renderer.getCoastRenderer
                },
                {
                    dataIndex: 'status',
                    name: 'status',
                    title: 'Status',
                    orderName: 'STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(PAYMENTS)
                }/*,
                {
                    dataIndex: 'description', name: 'description', title: 'Description', orderName: 'DESCRIPTION', cellClass: DEFAULT_CELL_STYLE
                }*/
            ],
            actions: [
                {
                    label: "View Payment",
                    type: BUTTON_TYPE_EDIT,
                    dialog: ViewPayment,
                    dialogInfo: viewPaymentdialogInfo
                },
                {
                    label: "Payment Options",
                    type: BUTTON_TYPE_EDIT_ON_SELECTION,
                    dialog: PaymentOptions,
                    dialogInfo: dialogInfo
                }
            ],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "method",
                    menuItems: PAYMENT_TYPES,
                    multiple: false,
                    required: false,
                    title: 'Payment Method'
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: PAYMENTS_FOR_FILTER,
                    multiple: false,
                    required: false,
                    title: 'Status'
                },
                {
                    type: FILTER_TYPE_DATE_RANGE,
                    fromName: "dateFrom",
                    toName: "dateTo",
                    fromPlaceholder: "Date Range From",
                    toPlaceholder: "Date Range To"
                },
                {type: FILTER_TYPE_LOCATION}
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareUpdatedObjectBeforeDisplay(oldObject, forUpdateObject, newObject) {
        let result = Object.assign({}, oldObject);
        result.status = newObject.status;
        return result;
    }

    convertFilterDataBeforeFiltering(data) {
        let returnData = {};

        Object.assign(returnData, data);


        // if (returnData.dateFrom && !returnData.dateTo) {
        //     returnData.dateTo = returnData.dateFrom;
        // }
        //
        // if (returnData.dateTo && !returnData.dateFrom) {
        //     returnData.dateFrom = returnData.dateTo;
        // }

        if (!returnData.dateFrom) {
            //     returnData.lastActivityTo = undefined;
        } else {
            returnData.dateFrom = DateHelper.setServerDateToStartTime(returnData.dateFrom);
        }

        if (!returnData.dateTo) {
            //     returnData.lastActivityFrom = undefined;
        } else {
            returnData.dateTo = DateHelper.setServerDateToEndTime(returnData.dateTo);
        }
        return returnData;
    }

    prepareManagedObject(loadedObject) {
        let result = loadedObject.payment;
        if (result) {
            result.paymentDetails = loadedObject.paymentDetails;
            return result;
        } else {
            return loadedObject
        }
    }
}

const PaymentsConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Payments);

export default PaymentsConnector;