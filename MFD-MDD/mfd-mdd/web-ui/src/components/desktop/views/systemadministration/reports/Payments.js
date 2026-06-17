/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import Report from '../../common/Report';
import {
    BUTTON_TYPE_EXPORT,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_DROPDOWN,
    FILTER_TYPE_SELECT,
    FILTER_TYPE_TOGGLE,
    REST_API_PREFIX_SIMPLE,
    SELECTION_MODE_SINGLE
} from '../../../../../utils/Constants';
import {REPORT_CLIENTS, REPORT_GROUP_BY} from '../../../../../data/Filters';
import DateHelper from '../../../../../utils/DateHelper';

class Payments extends Report {
    initView(props) {
        let me = this;
        let configuration = {
            url: REST_API_PREFIX_SIMPLE,
            requestInfo: {
                queryName: 'report/payment'
            },
            columns: [],
            actions: [
                {label: "Export to PDF", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadPdf},
                {label: "Export to Excel", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadExcel}
            ],
            filters: [
                {
                    type: FILTER_TYPE_DROPDOWN,
                    onClick: me.changeReport.bind(me, 'report/payment', {
                        // "from": this.getDefaultFromDate(),
                        // "to":  this.getDefaultToDate(),
                        // "groupBy": "WEEK",
                        "fileName": "my-report",
                        "download": false,
                        "format": "PDF"
                    }, false),
                    title: 'Total SUM by period',
                    children: [
                        {
                            type: FILTER_TYPE_SELECT,
                            name: "range",
                            menuItems: REPORT_CLIENTS,
                            multiple: false,
                            required: false,
                            title: 'Period'
                        },
                        {
                            type: FILTER_TYPE_DATE_RANGE,
                            fromName: "from",
                            toName: "to",
                            fromPlaceholder: "Last Activity From",
                            toPlaceholder: "Last Activity To",
                            onlyDate:true
                        },
                        {
                            type: FILTER_TYPE_SELECT,
                            name: "groupBy",
                            menuItems: REPORT_GROUP_BY,
                            multiple: false,
                            required: false,
                            title: 'Group By'
                        }
                    ]
                },
                {
                    type: FILTER_TYPE_TOGGLE,
                    title: 'Client with Expired Credit Cards',
                    onClick: me.changeReport.bind(me, 'report/expired_cc', {
                        "fileName": "my-report",
                        "download": false,
                        "format": "PDF"
                    }, true)
                },
                {
                    type: FILTER_TYPE_TOGGLE,
                    title: 'Aging Debts',
                    onClick: me.changeReport.bind(me, 'report/debt', {
                        "fileName": "my-report",
                        "download": false,
                        "format": "PDF"
                    }, true)
                }
            ],
            params: {
                "from": this.getDefaultFromDate(),
                "to":  this.getDefaultToDate(),
                "groupBy": "WEEK",
                "fileName": "my-report",
                "download": false,
                "format": "PDF"
            },
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    convertFilterDataBeforeFiltering(data) {
        let returnData = {};

        Object.assign(returnData, data);

        let clientcontacts = data.range;

        if (clientcontacts) {
            DateHelper.initNewClientsFiltersData(clientcontacts, returnData, 'to', 'from', true);
        }else{
            DateHelper.applyDateRange(returnData, 'to', 'from', true);
        }

        return returnData;

    }
}


const PaymentsConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(Payments);

export default PaymentsConnector;