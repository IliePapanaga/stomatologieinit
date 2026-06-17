import {RESPONSE_OK_STATUS} from '../../../utils/Constants';
import Remote from '../../../utils/Remote';
import {Logger} from 'react-logger-lib';


export default function requestChangeUserName(oldPassword, newUserName) {
    Logger.of('App.requestChangeUsername').info(`Old Password: ${oldPassword}, newUserName: ${newUserName}`);
    return new Promise((resolve) => {
        let query = `requestChangeUsername(request:{password:"${oldPassword}",newUsername:"${newUserName}"})`;

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