import {Logger} from 'react-logger-lib';
import intersection from 'array-intersection';
import UiView from '../utils/UiView';
import {ROOT_PATH} from '../utils/Constants';

/**
 * Check if user defined: true -> cheked next; false -> go to root
 * Check if valid path: true -> check next; false -> stop
 * Check if user has permissions for selected path: true -> success; false -> go by default url
 */
export default class AppGuard {

    canActivate(route, next) {
        let currentUser = route.matched.data.endpoint.props.context.currentUser;

        Logger.of('App.AppGuard.canActivate').info('Current user:', currentUser);

        if (!currentUser) {
            next(ROOT_PATH);
        } else {
            let functionalAreaKey = route.params.functionalAreaKey,
                viewSetKey = route.params.viewSetKey,
                viewKey = route.params.viewKey,
                functionalAreas = route.matched.data.endpoint.props.context.views.functionalAreas,
                valid = false;

            Logger.of('App.AppGuard.canActivate').info('functionalAreaKey:', functionalAreaKey, 'viewSetKey:', viewSetKey, 'viewKey:', viewKey);

            let functionalAreaArr = functionalAreas.filter((funcArea) => funcArea.urlKey === functionalAreaKey);
            if (functionalAreaArr.length > 0) {
                let viewSetArr = functionalAreaArr[0].viewSets.filter((viewSet) => viewSet.urlKey === viewSetKey);
                if (viewSetArr.length > 0) {

                    if (viewSetArr[0].views.length === 1 && !viewKey) {
                        viewKey = viewSetArr[0].views[0].urlKey;
                    }

                    let viewArr = viewSetArr[0].views.filter((view) => view.urlKey === viewKey);
                    if (viewArr.length > 0) {
                        Logger.of('App.AppGuard.canActivate').info('Current path is valid');

                        valid = intersection(currentUser.roles, functionalAreaArr[0].roles).length > 0;

                        Logger.of('App.AppGuard.canActivate').info('Current user has credentials for this path');
                    }
                }
            }
            if (!valid) {
                valid = UiView.getDefaultUserUrl(functionalAreas, currentUser);
                Logger.of('App.AppGuard.canActivate').info('Current path is not valid');
                Logger.of('App.AppGuard.canActivate').info('Defined current user path:', valid);
            }
            next(valid);
        }
    }
}