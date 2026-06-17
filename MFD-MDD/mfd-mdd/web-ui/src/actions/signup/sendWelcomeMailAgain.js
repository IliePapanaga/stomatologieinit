import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS, REST_API_PREFIX_PUBLIC} from '../../utils/Constants';
import {Logger} from 'react-logger-lib';

const sendWelcomeMailAgain = (userId, recaptchaHash) => {
    Logger.of('App.sendWelcomeMailAgain').info('Current userId:', userId);
    return function (dispatch) {
        let query = `sendWelcomeMailAgain(userId:"${userId}")`;

        return Remote.executeMutation(query, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.getData').info('Was sended:', response);
                    return true;
                default:
            }
            return false;
        }, function (response) {
            Error.showRemoteErrors(response);
            return false;
        },
            REST_API_PREFIX_PUBLIC,
            recaptchaHash);
    };
}

export default sendWelcomeMailAgain;