import Error from '../../utils/Error';
import {Logger} from 'react-logger-lib';
import axios from 'axios';
import {GOOGLE_API_KEY, RESPONSE_OK_STATUS} from '../../utils/Constants';


const COUNTRY = "country";
const ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
const ADMINISTRATIVE_STREET_NUMBER = "street_number";
const ADMINISTRATIVE_ROUTE = "route";
const LOCALITY = "locality";

const findLocationByZipCode = (zipCode) => {
    Logger.of('App.completeRegistration').info('Current zip code:', zipCode);
    return new Promise((resolve) => {
        return axios({
            method: 'get',
            url: `https://maps.googleapis.com/maps/api/geocode/json?language=en&address=${zipCode}+USA&key=${GOOGLE_API_KEY}`,
            params: {}
        }).then(function (response) {
            let gLocation = undefined;
            Logger.of('App.findLocationByZipCode').info('Response:', response);

            switch (response.status) {
                case RESPONSE_OK_STATUS:
                    gLocation = {city: "", state: "", country: "", route: "", streetNumber: "", location: undefined};

                    for (let result of response.data.results) {
                        for (let component of result.address_components) {
                            if (component.types.indexOf(LOCALITY) !== -1) {
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
                        if (result.geometry && result.geometry.location) {
                            gLocation.location = result.geometry.location;
                        }
                    }

                    break;
                default:
            }
            if (!gLocation.city || !gLocation.state) {
                gLocation.location = {};
            }
            resolve(gLocation);

        }).catch(error => { Error.showRemoteErrors(error); resolve(null) });
    });
}

export default findLocationByZipCode;