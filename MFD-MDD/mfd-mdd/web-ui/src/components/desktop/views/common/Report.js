import React from 'react';
import UiView from '../../../../utils/UiView';
import BaseActions from '../../common/BaseActions';
import {FILTER_RANGE, REST_API_PREFIX_SIMPLE} from '../../../../utils/Constants';
import {Query} from "../../../../models/core/QueryInfo";
import axios from "axios/index";
import Cookies from "universal-cookie";
import ObjectHelper from "../../../../utils/Object";
import fileDownload from 'react-file-download';
import ReactDOM from 'react-dom';
import DateHelper,{serverShortDateFormat as dateFormat} from "../../../../utils/DateHelper";
import FindByLocation from "./FindByLocation";

const cookies = new Cookies();

export const FORMAT_TYPE_PDF = 'PDF';

const FORMAT_TYPE_XLS = 'XLSX';

export default class Report extends FindByLocation {

    query = undefined;
    lastFilters = undefined;

    constructor(props) {
        super(props);

        this.query = new Query({
            name: this.state.queryName,
            endpoint: this.state.url,
        });
        this.loadReport();
    }

    loadReport(filters, download = false, format = FORMAT_TYPE_PDF, callbackFn) {
        let me = this;
        let optionHeaders = {
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        };

        let data = me.state.params ? Object.assign({}, me.state.params) : {};

        if (filters) {

            for (var key in filters) {
                data[key] = filters[key];
            }
        }

        this.lastFilters = data;

        data.download = download;

        data.format = format;

        data.fileName = me.getFileName(me.state.params.fileName, format);

        let options = {
            url: `${REST_API_PREFIX_SIMPLE}/${me.query.name}`,
            method: 'post',
            responseType: 'blob',
            headers: optionHeaders,
            data: data
        };

        let loaderDialogNode = UiView.showLoader();


        if (!callbackFn) {
            callbackFn = function (response) {
                if (loaderDialogNode) {
                    ReactDOM.unmountComponentAtNode(loaderDialogNode);
                }
                me.setState({data: response.data});

                const file = new Blob(
                    [response.data],
                    {type: me.getContentType(format)});

                const fileURL = URL.createObjectURL(file);
                var iframe = document.getElementById("reportIFrame");
                iframe.src = fileURL;
            };
        } else {
            callbackFn = callbackFn.bind(me, loaderDialogNode);
        }
        axios(options).then(callbackFn);
    }

    getFileName(fileName, format) {
        switch (format) {
            case FORMAT_TYPE_PDF:
                return `${fileName}.pdf`;
            case FORMAT_TYPE_XLS:
                return `${fileName}.xlsx`;
            default:
                return null;
        }
    }

    getContentType(format) {
        switch (format) {
            case FORMAT_TYPE_PDF:
                return 'application/pdf';
            case FORMAT_TYPE_XLS:
                return 'application/xlsx';
            default:
                break;

        }
    }

    applyFilters(data) {
        let me = this,
            filters = ObjectHelper.copyObject(data);

        if (data.range === FILTER_RANGE) {
            alert(11);
        } else {
            me.loadReport(this.convertFilterDataBeforeFiltering(filters));
        }
    }

    render() {
        let {initialized} = this.state;

        if (!initialized) {
            return false;
        }
        return (
            [
                this.filtersFormComponent,
                <BaseActions
                    actions={this.actions}
                    baseView={this}
                    key="base_actions"
                    ref={(el) => {
                        this.baseActions = el
                    }}/>,
                <iframe id="reportIFrame" width="100%" height="100%" title="Report"/>
            ]

        );

    }

    changeReport(queryName, params, hideFilters = false) {
        if (queryName !== this.state.queryName) {

            if (hideFilters) {
                let element = document.querySelector(".hide__btn");
                if (element) {
                    element.click()
                }
            }

            this.setState({queryName: queryName, params: params});
            this.query = new Query({
                name: queryName,
                endpoint: this.state.url,
            });
            this.resetFilters(function(){});
            this.loadReport(params);
        }
    }

    onDownloadPdf(btn) {
        let me = this;
        me.download(btn, FORMAT_TYPE_PDF);
    }

    onDownloadExcel(btn) {
        let me = this;
        me.download(btn, FORMAT_TYPE_XLS);
    }

    download(btn, format) {
        let me = this;

        me.loadReport(me.lastFilters, true, format, function (loaderDialogNode, response) {
            if (loaderDialogNode) {
                ReactDOM.unmountComponentAtNode(loaderDialogNode);
            }
            fileDownload(response.data, me.getFileName(me.state.params.fileName, format), me.getContentType(format));
        });
    }

    getDefaultToDate(){
        return DateHelper.getCurrentDate().format(dateFormat);
    }

    getDefaultFromDate(years = 10) {
        return DateHelper.getCurrentDate().subtract(years,'years').format(dateFormat);
    }
}