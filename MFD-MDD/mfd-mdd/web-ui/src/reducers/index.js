import {combineReducers} from 'redux';

import context from './Context';
import view from './View';
import references from './References';
import version from './Version';
import {routerReducer} from 'react-router-redux';
import {reducer as toastrReducer} from 'react-redux-toastr';
import {reducer as reduxFormReducer} from 'redux-form';

export default combineReducers({
    routing: routerReducer,
    context: context,
    view,
    references,
    version:version,
    toastr:toastrReducer,
    form: reduxFormReducer
})