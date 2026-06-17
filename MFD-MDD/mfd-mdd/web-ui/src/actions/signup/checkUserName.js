import Remote from '../../utils/Remote';
import {RESPONSE_OK_STATUS, REST_API_PREFIX_PUBLIC} from '../../utils/Constants';
import {Logger} from 'react-logger-lib';

const checkUserName = (values) => {
    let email = values.username;
    Logger.of('App.checkUserName').info('Checking email:', email);

    let query = `validateUserMail(model:{email:"${email}"})`;
    return Remote.executeMutation(query, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    Logger.of('App.checkUserName').info(`Checking email: ${email} is available`);
                    //resolve(true);
                    break;
                default:
                    break;
            }
            //resolve(false);
        }, function (response) {
            throw{username: 'That username is taken'};
        },
        REST_API_PREFIX_PUBLIC);


}

export default checkUserName;