/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import ReactDOM from 'react-dom';
import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS} from '../../utils/Constants';
import {Logger} from 'react-logger-lib';
import QueryRequest from '../../utils/GraphQl';
import UiView from '../../utils/UiView';
import ObjectHelper from '../../utils/Object';

const deleteData = (actionInfo, id, recaptchaHash) => {
    let loaderDialogNode = undefined;
    Logger.of('App.deleteData').info('Current objects:', actionInfo, id);
    return function (dispatch) {
        let queryRequest = new QueryRequest(actionInfo.queryName);

        queryRequest.filter({id: id});

        //queryRequest.find(Remote.collectResult(actionInfo.fields));     

        let uri = actionInfo.uri;

        loaderDialogNode = UiView.showLoader();

        return Remote.executeMutation(queryRequest.toString(), function (status, object) {
                if (loaderDialogNode) {
                    ReactDOM.unmountComponentAtNode(loaderDialogNode);
                }
                switch (status) {
                    case RESPONSE_OK_STATUS:
                        Logger.of('App.deleteData').info('Current object:', object);
                        return ObjectHelper.isObject(object) ? object : true;
                    default:
                }
                return undefined;
            }, function (response) {
                if (loaderDialogNode) {
                    ReactDOM.unmountComponentAtNode(loaderDialogNode);
                }
                Error.showRemoteErrors(response);
                return undefined;
            },
            uri,
            recaptchaHash);
    };
}

export default deleteData;