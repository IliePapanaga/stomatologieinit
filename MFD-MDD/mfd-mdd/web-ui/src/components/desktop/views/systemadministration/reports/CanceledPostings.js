/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import Report from '../../common/Report';
import {
    BUTTON_TYPE_EXPORT,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_SELECT,
    REST_API_PREFIX_SIMPLE,
    SELECTION_MODE_SINGLE,
} from '../../../../../utils/Constants';
import {REPORT_CLIENTS} from '../../../../../data/Filters';
import DateHelper from '../../../../../utils/DateHelper';

class CanceledPostings extends Report {
    initView(props) {
        let me = this;
        let configuration = {
            url: REST_API_PREFIX_SIMPLE,
            requestInfo: {
                queryName: 'report/posting/cancelled'
            },
            columns: [],
            actions: [
                {label: "Export to PDF", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadPdf},
                {label: "Export to Excel", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadExcel}
            ],
            filters: [


                {
                    type: FILTER_TYPE_DATE_RANGE,
                    fromName: "from",
                    toName: "to",
                    fromPlaceholder: "From",
                    toPlaceholder: "To",
                    onlyDate:true
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "range",
                    menuItems: REPORT_CLIENTS,
                    multiple: false,
                    required: false,
                    title: 'Period'
                },
            ],
            params:{
                // "from": this.getDefaultFromDate(),
                // "to":  this.getDefaultToDate(),
                "fileName" : "my-report",
                "download" : false,
                "format"   : "PDF"
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
            DateHelper.initNewClientsFiltersData(clientcontacts, returnData, 'to', 'from',true);
        }else{
            DateHelper.applyDateRange(returnData,'to', 'from', true);
        }

        return returnData;

    }
}


const CanceledPostingsConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(CanceledPostings);

export default CanceledPostingsConnector;