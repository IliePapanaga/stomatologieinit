import {toastr} from 'react-redux-toastr';

export default class Error {
    static showErrors(ex) {
        const toastrConfirmOptions = {
            onOk: () => console.log('CANCEL: clicked')
        };
        toastr.error(ex.message, ex.stack, toastrConfirmOptions);
    }

    static showRemoteErrors(error, url) {
        //auth and validation errors
        if (error.response && error.response.status) {
            var resultError = undefined;
            try {
                resultError = JSON.parse(error.response.statusText);
            } catch (ex) {
                resultError = {
                    message: error.response.statusText,
                    errorCode: error.response.status
                };
            }

            switch (error.response.status) {
                case -1:
                    // Operation was aborted do nothing
                    break;
                case 0:
                    toastr.error('Connection failed', 'Connection failed. Server unavailable.');
                    break;
                case 400:
                    // Validation error
                    if (resultError.fieldErrors) {
                        for (var errorKey in resultError.fieldErrors) {
                            for (var messageKey in resultError.fieldErrors[errorKey].messages) {
                                toastr.error(resultError.fieldErrors[errorKey].messages[messageKey], resultError.message);
                            }
                        }
                    }
                    break;
                case 401:
                    if (window.endpoint && window.endpoint.router) {
                        window.endpoint.props.resetUser(window.endpoint.router);
                    }
                    if (url === 'login') {
                        toastr.error('Error', 'User credentials are invalid or any other reason.');
                    }
                    break;
                case 403:
                    toastr.error('Error', 'User is inactive and should contact mdd administration in order to activate it.');
                    break;
                default:
                    toastr.error('Error', resultError.message);
            }
            //GraphQl errors
        } else if (Error.hasGraphQlErrors(error)) {
            error.errors.forEach(function (er) {

                toastr.error(er.errorType, er.message);

            }, this);
        }
    }

    static hasGraphQlErrors(data) {
        if (data.errors !== undefined) {
            return true;
        }
        return false;
    }
}