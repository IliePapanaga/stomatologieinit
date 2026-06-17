import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Router} from 'react-spa-router';
//import injectTapEventPlugin from 'react-tap-event-plugin';
import String from '../utils/String';
import {EVENT_AUTH_LOGOUT, EVENT_AUTH_USER_LOADED, EVENT_LOADED_VERSION, ROOT_PATH} from '../utils/Constants';

import {loadCurrentUser, logout} from '../actions/common/authorization';
import getEndpointMetaInfo from '../actions/common/getEndpointMetaInfo';
import {BaseRouterView} from './BaseRouterView';

import UiView from '../utils/UiView';
import {Logger} from 'react-logger-lib';

import ReduxToastr from 'react-redux-toastr';
import axios from 'axios';

//injectTapEventPlugin();

/**
 * Base application component. It loads current user and checks that page should be next
 */
class AppEndpoint extends Component {

    async componentDidMount() {
        let me = this,
            metaInfo = me.props.context.metaInfo;

        let versionServer = await me.getVersion("version-server.json");
        let versionUi = await me.getVersion("version.json");

        me.props.initVersion(Object.assign(versionUi, versionServer));

        const currentUser = await loadCurrentUser();

        if (!metaInfo && currentUser) {
            metaInfo = await getEndpointMetaInfo();
        }

        let routeKey = String.capitalizeFirst(this.props.context.appType);

        let RouteLib = require(`../configs/${routeKey}Route`);

        const routes = RouteLib.routes;

        for (let key in routes) {
            routes[key].data = {endpoint: me};
        }

        Logger.of('App.AppEndpoint.componentDidMount').info('Current user:', currentUser);

        me.props.initCurrentUser(currentUser, metaInfo);
        //Init router
        me.router = new Router({
            //mode: 'history',
            routes: routes
        });
        //Start router
        me.router.run();
        window.endpoint = me;
        Logger.of('App.AppEndpoint.componentDidMount').info('Router started');
        me.componentDidUpdate();

    }


    /**
     * If define current user, application is checking if path before transitions root
     * and redirects to default user path
     */
    async componentDidUpdate() {
        let me = this;

        if (me.router) {
            if (me.props.context.currentUser) {

                if (!me.props.context.metaInfo) {
                    let metaInfo = await getEndpointMetaInfo();
                    me.props.initCurrentUser(me.props.context.currentUser, metaInfo);
                }

                let pathBeforeTransitions = me.props.routing.locationBeforeTransitions.pathname;
                Logger.of('App.AppEndpoint.componentDidUpdate').info('location before transitions:', pathBeforeTransitions);
                if (ROOT_PATH === pathBeforeTransitions) {
                    let functionalAreas = me.props.context.views.functionalAreas;
                    let defaultUrl = UiView.getDefaultUserUrl(functionalAreas, me.props.context.currentUser)
                    Logger.of('App.AppEndpoint.componentDidUpdate').info('Defined current user path:', defaultUrl);
                    me.router.navigateToUrl(defaultUrl);
                }

            }
            /*else {
                           Logger.of('App.AppEndpoint.componentDidUpdate').info('Current user does not define: going to home page');
                           me.router.navigateToUrl(ROOT_PATH);
                       }*/
        }


        // if (!this.props.context.currentUser) {
        //     //const appType = this.props.context.appType;
        //     //const loginDialogLib = require(`./${appType}/common/LoginDialog`);
        //     //UiView.showDialog(<loginDialogLib.default store={UiView.createDialogStore()} managedObject={{}} actions={{ onLogged: this.props.initCurrentUser }} />);
        // }
    }

    getVersion(url) {
        return axios({
            url: url,
            method: 'get'
        }).then(function (response) {
            return response.data;

        }).catch(function (response) {
            return {};
        });
    }

    render() {
        return ([<BaseRouterView key="app-endpoint-router-view"/>, <ReduxToastr
            timeOut={10000}
            newestOnTop={false}
            preventDuplicates
            position="top-center"
            transitionIn="fadeIn"
            transitionOut="fadeOut"
            progressBar key="app-endpoint-toastr"/>]);
    }
}

const AppEndpointConnector = connect(
    (state, ownProps) => ({
        ownProps,
        context: state.context,
        routing: state.routing
    }),
    dispatch => ({
        initCurrentUser: (currentUser, metaInfo) => {
            dispatch({type: EVENT_AUTH_USER_LOADED, currentUser: currentUser, metaInfo});
        },

        initVersion: (version) => {
            dispatch({type: EVENT_LOADED_VERSION, version: version});
        },

        resetUser: (router) => {
            dispatch(logout()).then(function (result) {
                dispatch({type: EVENT_AUTH_LOGOUT});
                let currentUser = null;
                dispatch({type: EVENT_AUTH_USER_LOADED, currentUser: currentUser});
                router.navigateToUrl("/");
            });
        }
    })
)(AppEndpoint);

export default AppEndpointConnector;