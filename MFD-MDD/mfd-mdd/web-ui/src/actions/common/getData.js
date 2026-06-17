/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import ReactDOM from 'react-dom';
import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS} from '../../utils/Constants';
import ObjectHelper from '../../utils/Object';
import {Logger} from 'react-logger-lib';
import QueryRequest from '../../utils/GraphQl';
import UiView from '../../utils/UiView';
import {FieldsInfo} from "../../models/core/FieldsInfo";

export function getDataPromise(queryName: string, objectId: string, fields: Array<FieldsInfo>) {
    let loaderDialogNode = undefined;
    return new Promise((resolve) => {
        let result = Remote.collectResult(fields);

        let queryRequest = new QueryRequest(queryName, "query");

        if (objectId) {
            queryRequest.filter(ObjectHelper.isObject(objectId) ? objectId : {id: objectId});
        }

        queryRequest.find([`${result}`]);

        loaderDialogNode = UiView.showLoader();

        return Remote.executeQuery(queryRequest.toString(), function (status, object) {
            if (loaderDialogNode) {
                ReactDOM.unmountComponentAtNode(loaderDialogNode);
            }
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.getDataPromise').info('Current object:', object);
                    if (ObjectHelper.isObject(object)) {
                        resolve(object);
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
    });
};

export function getMultyDataPromise(queryNames: Array<string>, objectId: string, fields: Array<Array<FieldsInfo>>) {
    let loaderDialogNode = undefined;
    Logger.of('App.getMultyDataPromise').info('Current queryNames:', queryNames);
    return new Promise((resolve) => {
        let queryParts = [];
        loaderDialogNode = UiView.showLoader();
        queryNames.forEach(function (queryName, index) {
            let result = Remote.collectResult(fields[index]);
            let queryRequest = new QueryRequest(queryName);
            queryRequest.filter(ObjectHelper.isObject(objectId) ? objectId[index] : {id: objectId});
            queryRequest.find([`${result}`]);
            queryParts.push(queryRequest.toString());
        }, this);

        return Remote.executeMultiQuery(queryParts, function (status, objects) {
            if (loaderDialogNode) {
                ReactDOM.unmountComponentAtNode(loaderDialogNode);
            }
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.getDataPromise').info('Current objects:', objects);
                    if (ObjectHelper.isObject(objects)) {
                        resolve(objects);
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
    });
}

const getData = (queryName: string, objectId: string, fields: Array<string>) => {
    return function (dispatch) {
        if (ObjectHelper.isArray(queryName)) {
            return getMultyDataPromise(queryName, objectId, fields).then(function (object) {
                return object;
            })
        } else {
            return getDataPromise(queryName, objectId, fields).then(function (object) {
                return object;
            });
        }
    };
}

export default getData;