import Error from '../../utils/Error';
import {Logger} from 'react-logger-lib';
import axios from 'axios';
import {GOOGLE_API_KEY, RESPONSE_OK_STATUS} from '../../utils/Constants';

const findTimeZoneByLocation = (location) => {
    let timestamp = Date.now() / 1000;
    Logger.of('App.findTimeZoneByLocation').info('Current location:', location);
    return new Promise((resolve) => {
        return axios({
            method: 'get',
            url: `https://maps.googleapis.com/maps/api/timezone/json?location=${location.lat},${location.lng}&timestamp=${timestamp}&language=en&key=${GOOGLE_API_KEY}`,
            params: {}
        }).then(function (response) {
            Logger.of('App.completeRegistration').info('Response:', response);

            switch (response.status) {
                case RESPONSE_OK_STATUS:
                    if (response.data.timeZoneId) {
                        resolve(response.data.timeZoneId);
                    }
                    break;
                default:
            }
            resolve(null);
        }).catch(error => { Error.showRemoteErrors(error); resolve(null) });
    });
}

export default findTimeZoneByLocation;