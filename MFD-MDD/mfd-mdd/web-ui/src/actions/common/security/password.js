import {RESPONSE_OK_STATUS, REST_API_PREFIX_PUBLIC} from '../../../utils/Constants';
import Remote from '../../../utils/Remote';
import Error from '../../../utils/Error';
import QueryRequest from '../../../utils/GraphQl';
import {Logger} from 'react-logger-lib';

export function requestResetPassword(email, recapchaHash) {
    return function (dispatch) {

        let queryRequest = new QueryRequest('requestResetPassword');

        queryRequest.filter({ arg0: email });

        return Remote.executeMutation(queryRequest.toString(), function (status, result) {
            return true;
        }, function (response) {
            Error.showRemoteErrors(response);
            return false;
        },
            REST_API_PREFIX_PUBLIC,
            recapchaHash);
    }
}

const resetPassword = (token, password) => {
    Logger.of('App.resetPassword').info('Current token:', token);
    return function (dispatch) {
        let query = `resetPassword(newPassword:{token:"${token}",password:"${password}"})`;

        return Remote.executeMutation(query, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.resetPassword').info('Was activated:', response);
                    return true;
                default:
            }
            return false;
        }, function (response) {
            Error.showRemoteErrors(response);
            return false;
        },
            REST_API_PREFIX_PUBLIC);
    };
}

export default resetPassword;

export function changePassword(oldPassword, newPassword) {
    Logger.of('App.changePassword').info(`Old Password: ${oldPassword}, NewPassword: ${newPassword}`);
    return new Promise((resolve) => {
        let query = `changePassword(changePassword:{oldPassword:"${oldPassword}",newPassword:"${newPassword}"})`;

        return Remote.executeMutation(query, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.getData').info('Was activated:', response);
                    resolve(true);
                    break;
                default:
                    break;
            }
            resolve(false);
        }, function (response) {
            resolve(false);
        });
    });
};