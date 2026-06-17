import React from 'react';
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE_RANGE,
    SELECTION_MODE_SINGLE,
    NUMBER_CELL_STYLE
} from '../../../../../utils/Constants';
import {PAYMENTS} from '../../../../../data/Statuses';
import Renderer from '../../../../../utils/Renderer';

class Payments extends BaseView {
    initView(props) {
        let configuration = {
            requestInfo: {
                fetchQueryName: 'practicePayments'
            },
            additionalFields: ['jobDayId', 'jobInterviewId', 'jobApplicationId'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'label',
                    name: 'label',
                    title: 'Posting',
                    orderName: 'POSTING_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'label', name: 'label', title: 'Type', cellClass: DEFAULT_CELL_STYLE,
                    //orderName: 'POSTING_TYPE',
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
                    dataIndex: 'proLastName',
                    name: 'proLastName',
                    title: 'Professional Last Name',
                    orderName: 'PRO_LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'proFirstName',
                    name: 'proFirstName',
                    title: 'Professional First Name',
                    orderName: 'PRO_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
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
                    title: 'Payment Method',
                    orderName: 'METHOD',
                    cellClass: DEFAULT_CELL_STYLE
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
                },
            ],
            filters: [
                {
                    type: FILTER_TYPE_DATE_RANGE,
                    fromName: "dateFrom",
                    toName: "dateTo",
                    fromPlaceholder: "Select Date From",
                    toPlaceholder: "Select Date To",
                    onlyDate: false
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject) {
        let practiceOwner = loadedObject.practiceOwner
        delete loadedObject['practiceOwner'];
        return {
            practice: loadedObject,
            contact: practiceOwner.contact
        };
    }


    onReject() {
        alert("Reject");
    }

    onWithRaw() {
        alert("Withraw");
    }
}

const PaymentsConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Payments);

export default PaymentsConnector;

