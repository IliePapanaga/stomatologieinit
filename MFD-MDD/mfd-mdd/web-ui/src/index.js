import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import {composeWithDevTools} from 'redux-devtools-extension';
import thunkMiddleware from 'redux-thunk'
import {createLogger} from 'redux-logger'
import {hashHistory} from 'react-router';
import {syncHistoryWithStore} from 'react-router-redux';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import './resources/css/main.css';
import './resources/css/mdd.css';
import './resources/css/flatpickr.css';
import './resources/css/toastr/index.css';


import AppEndpoint from './components/AppEndpoint';
//import registerServiceWorker from './registerServiceWorker';
import reducer from './reducers';
/*eslint-disable no-unused-vars*/

//import './resources/sass/main.scss';
/*eslint-enable no-unused-vars*/

require(`./utils/Override`);

const loggerMiddleware = createLogger();
const store = createStore(reducer, composeWithDevTools(applyMiddleware(thunkMiddleware, loggerMiddleware)));

syncHistoryWithStore(hashHistory, store);
ReactDOM.render(
    <Provider store={store}>
        <MuiThemeProvider>
            <AppEndpoint />           
        </MuiThemeProvider>
    </Provider>,
    document.getElementById('root'));

//registerServiceWorker();
