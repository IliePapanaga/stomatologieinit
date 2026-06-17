/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import Report, {FORMAT_TYPE_PDF} from '../../common/Report';
import {
    BUTTON_TYPE_EXPORT, FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_SELECT,
    REST_API_PREFIX_SIMPLE,
    SELECTION_MODE_SINGLE,
  /*  SUB_CATEGORY_ID_DA,*/
    SUBCATEGORY_ORDERS,
    FILTER_TYPE_LOCATION
} from '../../../../../utils/Constants';
import {REPORT_POSTING_TYPE, REPORT_UNFILLED_DAYS, REPORT_POSTING_STATUS} from '../../../../../data/Filters';
import DateHelper from '../../../../../utils/DateHelper';

class NotFilledPositions extends Report {
    initView(props) {
        let me = this;
        me.subCategories = [];
        let configuration = {
            url: REST_API_PREFIX_SIMPLE,
            requestInfo: {
                queryName: 'report/position/unfilled'
            },
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
                    name: "requiredSubcategory",
                    menuItems: me.subCategories,
                    multiple: false,
                    required: false,
                    title: 'Position',
                    grouped: true
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "positionType",
                    menuItems: REPORT_POSTING_TYPE,
                    multiple: false,
                    required: false,
                    title: 'Position Type'
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "postingStatus",
                    menuItems: REPORT_POSTING_STATUS,
                    multiple: false,
                    required: false,
                    title: 'Posting Status'
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "minMaxUnfilledDays",
                    menuItems: REPORT_UNFILLED_DAYS,
                    multiple: false,
                    required: false,
                    title: 'Days Unfilled'
                },
                {type: FILTER_TYPE_LOCATION}],
            columns: [],
            actions: [
                {label: "Export to PDF", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadPdf},
                {label: "Export to Excel", type: BUTTON_TYPE_EXPORT, onClick: me.onDownloadExcel}
            ],
            params: {
                // "requiredSubcategory": SUB_CATEGORY_ID_DA,
                // "positionType": "PERMANENT_JOB_POSTING",
                "fileName": "my-report",
                "download": false,
                "format": "PDF",
                // "from": this.getDefaultFromDate(),
                // "to":  this.getDefaultToDate(),
            },
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    loadReport(filters, download = false, format = FORMAT_TYPE_PDF, callbackFn) {
        let me = this,
            baseLoadReport = super.loadReport;

        me.props.onLoadRefs(['categories'], me.props.references, me.props.metaInfo, function (result) {
            try {
                me.props.references.categories.forEach(function (category) {
                    let cats = category.subCategories.sort(function (a, b) {
                        let value1 = SUBCATEGORY_ORDERS[category.id].indexOf(a.id);
                        let value2 = SUBCATEGORY_ORDERS[category.id].indexOf(b.id);

                        if (value2 > value1) {
                            return -1;
                        }
                        if (value1 > value2) {
                            return 1;
                        }
                        return 0;
                    });
                    me.subCategories.push({group: category.name, options: cats});

                });
                baseLoadReport.call(me, filters, download, format, callbackFn);
            } catch (ex) {
            }
        });
    }

    convertFilterDataBeforeFiltering(data) {
        let returnData = {};

        Object.assign(returnData, data);

        let clientcontacts = data.range;

        if (clientcontacts) {
            DateHelper.initNewClientsFiltersData(clientcontacts, returnData, 'to', 'from', true);
        } else {
            DateHelper.applyDateRange(returnData, 'to', 'from', true);
        }
        if(data.minMaxUnfilledDays){
            let [min, max] = data.minMaxUnfilledDays.split('_');
            delete returnData.minMaxUnfilledDays;
            if (min) {
                returnData.minUnfilledDays = parseInt(min, 10);
            }
            if (max) {
                returnData.maxUnfilledDays = parseInt(max, 10);
            }
        }

        if(data.lng) {
            returnData.longitude = data.lng;
            delete returnData.lng;
        }

        if(data.lat) {
            returnData.latitude = data.lat;
            delete returnData.lat;
        }

        if(data.distance){
            returnData.radius=data.distance;
            delete returnData.distance;
        }

        return returnData;

    }
}


const NotFilledPositionsConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(NotFilledPositions);

export default NotFilledPositionsConnector;