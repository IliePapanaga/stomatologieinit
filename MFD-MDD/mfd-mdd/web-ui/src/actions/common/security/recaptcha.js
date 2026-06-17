import {REST_API_PREFIX_PUBLIC} from '../../../utils/Constants';
import Remote from '../../../utils/Remote';
import Error from '../../../utils/Error';

export function getSiteKey() {
    return new Promise((resolve) => {
        return Remote.executeQuery('reCaptchaClientKey', function (status, clientKey) {
            resolve(clientKey)
        }, function (response) {
            Error.showRemoteErrors(response);
            resolve(undefined);
        }, false, REST_API_PREFIX_PUBLIC);
    });
}
