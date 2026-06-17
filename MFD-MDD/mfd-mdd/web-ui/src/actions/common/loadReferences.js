import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS} from '../../utils/Constants';
import ObjectHelper from '../../utils/Object';
import {QueryResult} from '../../models/core/QueryInfo';
import QueryRequest from '../../utils/GraphQl';
import {Logger} from 'react-logger-lib';

export function loadReferencesPromise(refs) {
    Logger.of('App.loadReferencesPromise').info('Current refs:', refs);
    return new Promise((resolve) => {
        let refParts = [];
        refs.forEach(function (ref) {
            let queryName = ref.queryName;
            let queryFilter = ref.queryFilter;
            let fields = ref.fields;
            let result = Remote.collectResult(fields);

            let queryRequest = new QueryRequest(queryName);
            if (queryFilter) {
                queryRequest.filter(queryFilter);
            }
            queryRequest.find([`${result}`]);
            refParts.push(queryRequest.toString());
        }, this);

        return Remote.executeMultiQuery(refParts, function (status, response) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    if (ObjectHelper.isObject(response)) {
                        let queryResult = new QueryResult(response);
                        resolve(queryResult);
                    }
                    break;

                default:
            }

            resolve(undefined);
        }, function (response) {
            Error.showRemoteErrors(response);
            resolve(undefined);
        });
    });
}

const loadReferences = (refs) => {
    return function (dispatch) {
        return loadReferencesPromise(refs).then(function (object) {
            return object;
        })
    };
}

export default loadReferences;