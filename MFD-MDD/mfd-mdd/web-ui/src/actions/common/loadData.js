/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import ReactDOM from 'react-dom';
import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS, REST_API_PREFIX_SIMPLE} from '../../utils/Constants';
import ObjectHelper from '../../utils/Object';
import {QueryResult} from '../../models/core/QueryInfo';
import {Logger} from 'react-logger-lib';
import QueryRequest from '../../utils/GraphQl';
import {Enum} from 'enumify';
import UiView from '../../utils/UiView';
import {FieldsInfo} from "../../models/core/FieldsInfo";

const graphLoadData = (query, resolve) => {
    let loaderDialogNode = undefined;
    let result = Remote.collectResult([new FieldsInfo({select: query.select})], query.pagination.allowed ? 'nodes' : null);
    let queryRequest = new QueryRequest(query.name, "query");
    let orders = [];

    query.pagination.orders.forEach(function (order) {
        if (!order.nullsFirst) {
            orders.push(new Enum({name: `${order.field}_${order.direction.name}`}));
        }

    });
    let queryFilter = undefined;
    if (query.pagination.allowed) {
        queryFilter = {
            perPage: query.pagination.perPage,
            page: query.pagination.page
        };
    }
    if (orders && orders.length) {
        queryFilter = queryFilter || {};
        queryFilter['orders'] = orders;
    }

    if (query.parameters) {
        queryFilter = queryFilter || {};
        queryFilter = Object.assign(queryFilter, query.parameters);
    }

    if (queryFilter) {
        queryRequest.filter(queryFilter);
    }

    let findString = `${result}`;
    if (query.pagination.allowed) {
        findString += " count";
    }
    queryRequest.find([findString]);

    loaderDialogNode = UiView.showLoader();

    return Remote.executeQuery(queryRequest.toString(), function (status, response) {
        if (loaderDialogNode) {
            ReactDOM.unmountComponentAtNode(loaderDialogNode);
        }
        switch (status) {
            case RESPONSE_OK_STATUS:
                if (ObjectHelper.isObject(response)) {
                    let result = response;
                    if (!query.pagination.allowed) {
                        result = {
                            nodes: response,
                            count: response.length
                        }
                    }
                    let queryResult = new QueryResult(result);
                    resolve(queryResult);
                }
                break;

            default:
        }

        resolve(undefined);
    }, function (response) {
        if (loaderDialogNode) {
            ReactDOM.unmountComponentAtNode(loaderDialogNode);
        }
        Error.showRemoteErrors(response);
        resolve(undefined);
    });
}

const simpleLoadData = (query, resolve) => {
    let loaderDialogNode = undefined;

    // query.pagination.orders.forEach(function (order) {
    //     if (!order.nullsFirst) {
    //         orders.push(new Enum({name: `${order.field}_${order.direction.name}`}));
    //     }
    //
    // });
    // let queryFilter = undefined;
    // if (query.pagination.allowed) {
    //     queryFilter = {
    //         perPage: query.pagination.perPage,
    //         page: query.pagination.page
    //     };
    // }
    // if (orders && orders.length) {
    //     queryFilter = queryFilter || {};
    //     queryFilter['orders'] = orders;
    // }
    //
    // if (query.parameters) {
    //     queryFilter = queryFilter || {};
    //     queryFilter = Object.assign(queryFilter, query.parameters);
    // }
    //
    // if (queryFilter) {
    //     queryRequest.filter(queryFilter);
    // }
    //
    // let findString = `${result}`;
    // if (query.pagination.allowed) {
    //     findString += " count";
    // }
    // queryRequest.find([findString]);
    //
    loaderDialogNode = UiView.showLoader();

    let params={};

    return Remote.executeSimpleQuery(query.name,params,function (status, response) {
        if (loaderDialogNode) {
            ReactDOM.unmountComponentAtNode(loaderDialogNode);
        }
        switch (status) {
            case RESPONSE_OK_STATUS:
                if (ObjectHelper.isObject(response)) {
                    let result = response;
                    if (!query.pagination.allowed) {
                        result = {
                            nodes: response,
                            count: response.length
                        }
                    }
                    let queryResult = new QueryResult(result);
                    resolve(queryResult);
                }else{
                    resolve(response);
                }
                break;

            default:
        }

        resolve(undefined);
    }, function (response) {
        if (loaderDialogNode) {
            ReactDOM.unmountComponentAtNode(loaderDialogNode);
        }
        Error.showRemoteErrors(response);
        resolve(undefined);
    });
}


const loadData = (query) => {
    Logger.of('App.loadData').info('Query:', query);

    return new Promise((resolve) => {
        if (query.endpoint !== REST_API_PREFIX_SIMPLE) {
            return graphLoadData(query, resolve);
        }else{
            return simpleLoadData(query, resolve);
        }
    });
}

export default loadData;