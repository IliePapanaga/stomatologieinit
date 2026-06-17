import Error from '../../utils/Error';
import {Logger} from 'react-logger-lib';
import axios from 'axios';
import {GOOGLE_API_KEY, RESPONSE_OK_STATUS} from '../../utils/Constants';


const COUNTRY = "country";
const ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
const ADMINISTRATIVE_STREET_NUMBER = "street_number";
const ADMINISTRATIVE_ROUTE = "route";
const LOCALITY = "locality";
const POSTAL_CODE = "postal_code";

const findLocationByCoordinates = (latlng) => {
    Logger.of('App.completeRegistration').info('Current latlng:', latlng);
    return new Promise((resolve) => {
        return axios({
            method: 'get',
            url: `https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=${latlng.lat},${latlng.lng}&key=${GOOGLE_API_KEY}`,
            params: {}
        }).then(function (response) {
            let gLocation = undefined;
            Logger.of('App.findLocationByCoordinates').info('Response:', response);

            switch (response.status) {
                case RESPONSE_OK_STATUS:
                    gLocation = { zipCode: "", city: "", state: "", country: "", route: "", streetNumber: "", location: {lat: latlng.lat, lng: latlng.lng} };
                    for (let result of response.data.results) {
                        for (let component of result.address_components) {
                            if (component.types.indexOf(POSTAL_CODE) !== -1) {
                                gLocation.zipCode = component.long_name;
                            } else if (component.types.indexOf(LOCALITY) !== -1) {
                                gLocation.city = component.long_name;
                            } else if (component.types.indexOf(ADMINISTRATIVE_AREA_LEVEL_1) !== -1) {
                                gLocation.state = component.short_name;
                            } else if (component.types.indexOf(ADMINISTRATIVE_STREET_NUMBER) !== -1) {
                                gLocation.streetNumber = component.short_name;
                            } else if (component.types.indexOf(ADMINISTRATIVE_ROUTE) !== -1) {
                                gLocation.route = component.short_name;
                            } else if (component.types.indexOf(COUNTRY) !== -1) {
                                gLocation.country = component.long_name;
                            }
                        }
                    }

                    break;
                default:
            }
            resolve(gLocation);
        }).catch(error => { Error.showRemoteErrors(error); resolve(null) });
    });
}

export default findLocationByCoordinates;