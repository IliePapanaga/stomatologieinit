import Remote from '../../../utils/Remote';
import {RESPONSE_OK_STATUS, REST_API_PREFIX_PUBLIC} from '../../../utils/Constants';
import {Logger} from 'react-logger-lib';

const confirmChangeUserName = (token) => {
    Logger.of('App.confirmChangeUserName').info('Current token:', token);
    return new Promise((resolve) => {
        let query = `confirmUsernameChange(token:"${token}")`;

        return Remote.executeMutation(query, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.confirmChangeUserName').info('Was activated:', response);
                    resolve(true);
                    break;
                default:
                    break;
            }
            resolve(false);
        }, function (response) {
            resolve(false);
        },
            REST_API_PREFIX_PUBLIC);
    });
}

export default confirmChangeUserName;