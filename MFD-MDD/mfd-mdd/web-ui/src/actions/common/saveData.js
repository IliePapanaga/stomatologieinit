/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import ReactDOM from 'react-dom';
import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {EVENT_CHANGED_RECAPTCHA_HASH, RESPONSE_OK_STATUS} from '../../utils/Constants';
import ObjectHelper from '../../utils/Object';
import {Logger} from 'react-logger-lib';
import QueryRequest from '../../utils/GraphQl';
import UiView from '../../utils/UiView';
import axios from "axios/index";
import Cookies from "universal-cookie";

const cookies = new Cookies();

export const saveDataPromise = (actionInfo, data, recaptchaHash, dispatch) => {
    Logger.of('App.saveData').info('Current objects:', actionInfo, data);
    let loaderDialogNode = undefined;
    return new Promise((resolve) => {
        let queryRequest = new QueryRequest(actionInfo.queryName);

        queryRequest.filter(data);

        if (actionInfo.fields) {
            queryRequest.find(Remote.collectResult(actionInfo.fields));
        }

        let uri = actionInfo.uri;

        if (actionInfo.showLoader) {
            loaderDialogNode = UiView.showLoader();
        }

        return Remote.executeMutation(queryRequest.toString(), function (status, object) {
                if (loaderDialogNode) {
                    ReactDOM.unmountComponentAtNode(loaderDialogNode);
                }

                switch (status) {
                    case RESPONSE_OK_STATUS:
                        Logger.of('App.getData').info('Current object:', object);
                        if (ObjectHelper.isObject(object)) {
                            resolve(object);
                        }
                        break;
                    default:
                }
                resolve(undefined);
            }, function (response) {
                if (dispatch) {
                    dispatch({type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined});
                }
                if (loaderDialogNode) {
                    ReactDOM.unmountComponentAtNode(loaderDialogNode);
                }
                Error.showRemoteErrors(response);
                throw new Error(response);
            },
            uri,
            recaptchaHash);
    });
}

const saveData = (actionInfo, data, recaptchaHash) => {
    Logger.of('App.saveData').info('Current objects:', actionInfo, data);
    return function (dispatch) {
        return saveDataPromise(actionInfo, data, recaptchaHash, dispatch).then(function (object) {
            return object;
        });
    }
};

export default saveData;

export function simpleSave(url, data) {
    return axios({
        method: 'post',
        url: `${url}`,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        },
        data: data
    })
}