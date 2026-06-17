import React from 'react';
import ReactDOM from 'react-dom';
import {applyMiddleware, combineReducers, createStore} from 'redux';
import {composeWithDevTools} from 'redux-devtools-extension';
import thunkMiddleware from 'redux-thunk';
import {createLogger} from 'redux-logger';
import intersection from 'array-intersection';
import MenuItem from 'material-ui/MenuItem';
import BaseDialogReducer from '../reducers/BaseDialogReducer';
import context from '../reducers/Context';
import findLocationByZipCode from '../actions/signup/findLocationByZipCode';
import findTimeZoneByLocation from '../actions/location/findTimeZoneByLocation';
import {Logger} from 'react-logger-lib';
import {STATES} from '../data/States';
import {reducer as reduxFormReducer} from 'redux-form';
import ObjectHelper from "./Object";

export default class UiView {

    static showLoader() {
        var node = document.createElement("div");
        node.setAttribute('dlg', '1');
        document.getElementById('root').appendChild(node)
        ReactDOM.render(
            <div class="loader ">

                <img src="img/loading.gif" alt="loading"/>

                <div class="loader-overlay"></div>
            </div>, node);
        return node;
    }

    static showDialog(jsxString: any) {
        var node = document.createElement("div");
        node.setAttribute('dlg', '1');
        document.getElementById('root').appendChild(node)
        ReactDOM.render(jsxString, node);
        return node;
    }

    static closeDialog() {
        let mountNodes = document.querySelectorAll('div[dlg="1"]');
        mountNodes.forEach(function (element) {
            ReactDOM.unmountComponentAtNode(element);
        }, this);

    }

    static createDialogStore(dialogReducer) {
        const loggerMiddleware = createLogger();
        let store = createStore(dialogReducer || combineReducers({
            dialog: BaseDialogReducer, form: reduxFormReducer, context
        }), composeWithDevTools(applyMiddleware(thunkMiddleware, loggerMiddleware)));
        return store;
    }

    static getViewSet(functionalArea, viewSetKey) {
        let viewSets = functionalArea.viewSets.filter((viewSet) => viewSet.urlKey === viewSetKey);
        return viewSets.length > 0 ? viewSets[0] : undefined;
    }

    static getView(viewSet, viewKey) {
        let views = viewSet.views.filter((view) => view.urlKey === viewKey);
        return views.length > 0 ? views[0] : undefined;
    }

    static getSelectedObjects(state) {
        return state.view.selection;
    }

    static getDefaultFunctionalAreaUrl(functionalAreas, funcArea, viewSet = undefined) {
        let functionalAreaArray = functionalAreas.filter((functionalArea) => functionalArea.urlKey === funcArea);
        let viewSetUrl = undefined,
            viewUrl = undefined;

        if (functionalAreaArray.length > 0) {
            let viewSets = functionalAreaArray[0].viewSets;
            if (viewSets.length > 0) {
                viewSetUrl = viewSet || viewSets[0].urlKey;
                let views = viewSets[0].views;
                if (viewSet) {
                    views = viewSets.filter((viewSetObj) => viewSetObj.urlKey === viewSet)[0].views;
                    ;
                }

                if (views.length > 0) {
                    viewUrl = views[0].urlKey;
                }
            }
        }
        return `/${funcArea}/${viewSetUrl}/${viewUrl}`;
    }

    static getDefaultUserUrl(functionalAreas, userInfo, viewSet = undefined) {
        let functionalAreaArray = functionalAreas.filter((functionalArea) => intersection(userInfo.roles, functionalArea.roles).length > 0);
        let funcArea = undefined,
            viewSetUrl = undefined,
            viewUrl = undefined;

        if (functionalAreaArray.length > 0) {
            funcArea = functionalAreaArray[0].urlKey;
            let viewSets = functionalAreaArray[0].viewSets;
            if (viewSets.length > 0) {
                viewSetUrl = viewSet || viewSets[0].urlKey;
                let views = viewSets[0].views;
                if (views.length > 0) {
                    viewUrl = views[0].urlKey;
                }
            }
        }
        return `/${funcArea}/${viewSetUrl}/${viewUrl}`;
    }

    static getMenuItems(collection) {
        return collection.map((element) => (
            <MenuItem
                key={element.value}
                insetChildren={true}
                /*checked={this.state.values.indexOf(person.value) > -1}*/
                value={element.value}
                primaryText={element.name}
            />
        ));
    }

    static getMultipleComboSelectionRenderer = (values) => {
        switch (values.length) {
            case 0:
                return '';
            //case 1:
            //  return persons[values[0]].name;
            default:
                return `${values.length}names selected`;
        }
    }

    static initFooter(footerContentSelector = 'div.footer', version = {}) {

        var footer = document.querySelector(footerContentSelector);

        if (footer) {
            var baseFooter = document.querySelector('.footer__wrapper');
            baseFooter.style.display = footerContentSelector !== 'div.footer' ? "none" : "visible";

            var routerPage = document.querySelector('.router-page');
            routerPage.style.width = footerContentSelector !== 'div.footer' ? "100%" : undefined;

            footer.innerHTML = '<div class="footer__text">If you have any questions, please contact us: <a href="contact@Mayday Dental staffing.com" class="footer__link">contact@Mayday Dental staffing.com</a> or <a class="footer__link" href="tel:(888)899-4386">(888)899-4386</a>.<br>' +
                ' Mayday Dental Staffing is a registered trademark. Copyright &copy; Mayday Dental Staffing.com. All rights reserved. Patent pending.</div>';
            document.title = ('MDD ' + (version.server === version.ui && version.ui === 'n/a' ? '[UNVERSIONED]' : `[${version.server}/${version.ui}]`));
            /*  footer.innerHTML = `${year}Copyright © Mayday Dental Staffing.com All rights reserved [server version: ${version.server}, ui version: ${version.ui}]`;*/
        }

    }

    static getFindByZipCodeHandler(change, locationPrefix = 'contact.address.', timeZonePath = null, defineCoordinates = false) {
        return function (e, newValue, oldValue) {
            if (newValue.indexOf('_') < 0) {
                Logger.of('App.getFindByZipCodeHandler').info('ZipCode:', newValue);
                findLocationByZipCode(newValue).then(function (location) {
                    Logger.of('App.getFindByZipCodeHandler').info('Location:', location);
                    if (location) {
                        let changeLocation = function (locationPrefix, location) {
                            change(`${locationPrefix}city`, location.city);
                            if (location.route && location.streetNumber) {
                                change(`${locationPrefix}street`, `${location.streetNumber} ${location.route}`);
                            }
                            if (STATES.findIndex(function (item) {
                                    return item.code === location.state
                                }) >= 0) {
                                change(`${locationPrefix}state`, location.state);
                            } else {
                                change(`${locationPrefix}state`, '');
                            }
                            if (defineCoordinates && location.location) {
                                change(`${locationPrefix}latitude`, location.location.lat || '');
                                change(`${locationPrefix}longitude`, location.location.lng || '');
                            }
                        };
                        if (timeZonePath && location.location) {
                            findTimeZoneByLocation(location.location).then(function (timeZone) {
                                Logger.of('App.getFindByZipCodeHandler').info('Time Zone:', timeZone);
                                changeLocation(locationPrefix, location);
                                if (timeZone) {
                                    change(timeZonePath, timeZone);
                                }
                            });
                        } else {
                            changeLocation(locationPrefix, location);
                        }
                    }
                });
            }
        }
    }

    static checkClass(anchor, checkAnchor, baseClass, activeSuffix) {
        return anchor === checkAnchor ? `${baseClass} ${activeSuffix}` : baseClass;
    }

    static showResource(resource, title) {
        var pageWidth = window.innerWidth;
        var pageHeight = window.innerHeight;
        var width = pageWidth * 0.7,
            height = pageHeight * 0.7,
            left = (pageWidth / 2) - (width / 2),
            top = (pageHeight / 2) - (height / 2);


        window.open(resource,
            title,
            "centerscreen=yes,width=" + Math.round(width) + ",height=" + Math.round(height) + ",top=" + Math.round(top) + ",left=" + Math.round(left) + ",titlebar=no,menubar=no,toolbar=no,personalbar=no,directories=no,location=no,resizable=yes,scrollbars=yes,status=no");


    }

    static generateHeaderUsername(currentUser) {
        if (currentUser) {
            if (currentUser.realUser&&currentUser.realUser.id!==currentUser.id) {
                return `${currentUser.realUser.name.first} ${currentUser.realUser.name.last} as ${currentUser.name.first} ${currentUser.name.last}`;
            } else {
                return `${currentUser.name.first} ${currentUser.name.last}`;
            }
        }
        return ``;
    }

    static hasImpersonalization(currentUser){
        if (currentUser && currentUser.realUser) {
            return currentUser.realUser.id !== currentUser.id;
        }
        return false;
    }

    static getYesNo(owner, path, yes = "Yes", no = "No") {
        return ObjectHelper.getValue(owner, path) ? yes : no;
    }


   /* static getYesNo(owner, path, yes = "Yes", no = "No") {
        return ObjectHelper.getValue(owner, path) ? yes : no;
    }*/

    static getAsPercent(owner, path) {
        let value= ObjectHelper.getValue(owner, path)||0;
        return `${value}%`
    }
}

