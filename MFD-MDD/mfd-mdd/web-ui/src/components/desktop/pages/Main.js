import React from 'react';
import {connect} from 'react-redux';
import {toastr} from 'react-redux-toastr';
import Footer from '../common/page/Footer';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../common/BasePage';
//import BaseGrid from '../common/BaseGrid';
import {exitFromImpersonate, logout} from '../../../actions/common/authorization';

import {EVENT_AUTH_LOGOUT, EVENT_AUTH_USER_LOADED, ROOT_PATH} from '../../../utils/Constants';
import UiView from '../../../utils/UiView';

/**
 * Main application component. Containts base navigation panels and base view
 */
class Main extends BasePage {
    footerSelector = "div.board__footer span";

    logout() {
        let me = this;
        const toastrConfirmOptions = {
            onOk: function () {
                me.props.onLogout(me.state, me.props.hasImpersonalization, me);
            },
            onCancel: () => console.log('CANCEL: clicked')
        };
        toastr.confirm(
            <div class="modal57">
                <div class="header deactivate"><h2>logout</h2></div>
            <div class="body">Are you sure you want to logout from the application?</div>
            </div>

            , toastrConfirmOptions);
    }

    render() {
        let me = this;
        let viewSetConfig = UiView.getViewSet(this.props.functionalArea, this.props.viewSetKey);
        let viewKey = this.props.viewKey || this.props.viewSetKey;
        let viewConfig = UiView.getView(viewSetConfig, viewKey)
        const viewLib = require(`../views/${this.props.functionalArea.urlKey}/${this.props.viewSetKey}/${viewConfig.className}`);
        const View = viewLib.default;
        const viewSetKey = this.props.viewSetKey;
        const functionalAreaKey = this.props.functionalAreaKey;
        const fullName = this.props.fullName;
        const helpFileName = functionalAreaKey === 'systemadministration' ? 'MDD System Office Management Manual.pdf' : 'index.html';

        return ([
            <div className="container" key="content">
                <div class="main">
                    <div class="main__header">
                        <div class="main__header-logo">
                            <img src="img/logo-abs1.png" alt="logo" />
                        </div>

                        <div class="main__header-bar">
                            <div class="main__header-logout">
                                <div class="logout__text">Hello,</div>
                                <div class="logout__name">{fullName}</div>
                                <div class="logout__img"><a onClick={this.logout.bind(this)}>
                                    <svg class="log__icon">
                                        <use xlinkHref="#Logout"></use>
                                    </svg>
                                </a></div>
                            </div>
                            <div class="main__header-help">
                                <a onClick = {function () {UiView.showResource(`/docs/${functionalAreaKey}/${helpFileName}`, 'Help');}} >
                                    <img src="img/icons/help.png" alt="help"/>
                                    <svg class="help__icon">
                                        <use xlinHref="#help"></use>
                                    </svg>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="main__content sys__content">
                        <div class="main__bar">
                            {this.props.functionalArea.viewSets.map((viewSet, index) => (
                                <div
                                    class={'main__bar-item' + (viewSet.views.length > 1 ? ' main__bar-item-list' : '') + (viewSet.urlKey === viewSetKey ? ' main__bar-item-active' : '')}
                                    key={`viewSet-${viewSet.urlKey}`}>
                                    <button
                                        class={"main__bar-item-btn" + (viewSet.views.length > 1 && viewSet.urlKey === viewSetKey ? ' main__bar-item-list-open' : '')}
                                        onClick={function (e) {
                                            if (viewSet.views.length <= 1) {
                                                me.navigateTo(functionalAreaKey, viewSet.urlKey);
                                            }
                                        }} key={`viewSet-btn-${index}`}>
                                        <div>
                                            <svg class="bar__icon">
                                                <use xlinkHref={viewSet.iconXlinkHref}></use>
                                            </svg>
                                        </div>
                                        <div>
                                            <svg class="bar__iconHover">
                                                <use xlinkHref={viewSet.iconHoverXlinkHref}></use>
                                            </svg>
                                        </div>
                                        <p>{viewSet.title}</p>
                                    </button>
                                    {viewSet.views.length > 1 &&
                                    <div class="main__bar-item-hide"
                                         style={{display: (viewSet.urlKey === viewSetKey ? 'block' : 'none')}}>
                                        {viewSet.views.map((view, viewIndex) => (
                                            <div
                                                class={'main__bar-item-hide-item' + (view.urlKey === viewKey ? ' main__bar-item-hide-item-active' : '')}
                                                onClick={function () {
                                                    me.navigateTo(functionalAreaKey, viewSet.urlKey, view.urlKey)
                                                }}>{view.title}</div>
                                        ))}
                                    </div>
                                    }
                                </div>
                            ))}
                        </div>

                        <div class="main__board">
                            <div class="menu">
                                <span class="menu-global menu-top menu-top-click"></span>
                                <span class="menu-global menu-middle menu-middle-click"></span>
                                <span class="menu-global menu-bottom menu-bottom-click"></span>
                            </div>
                            <div class="board">
                                <View/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>,
            <Footer key="footer"/>
        ]);
    }
}


const MainConnector = connect(function (state, ownProps) {
        return Object.assign(basePageStateMap(state, ownProps), {
            functionalAreaKey: ownProps.functionalAreaKey,
            viewSetKey: ownProps.viewSetKey,
            viewKey: ownProps.viewKey,
            views: state.context.views,
            hasImpersonalization: UiView.hasImpersonalization(state.context.currentUser),
            fullName: UiView.generateHeaderUsername(state.context.currentUser),
            functionalArea: state.context.views.functionalAreas.filter((functionalArea) => functionalArea.urlKey === ownProps.functionalAreaKey)[0]
        });
    },
    function (dispatch) {
        return Object.assign(basePageDispatcherMap(dispatch), {
            onLogout: (state, hasImpersonalization, dialog) => {

                if (hasImpersonalization) {
                    dispatch(exitFromImpersonate()).then(function (result) {
                        window.location.reload();
                    });
                } else {
                    dispatch(logout()).then(function (result) {
                        dispatch({type: EVENT_AUTH_LOGOUT});
                        let currentUser = null;
                        //loadCurrentUser().then(currentUser => {
                            dispatch({type: EVENT_AUTH_USER_LOADED, currentUser: currentUser});
                            dialog.props.router.navigateToUrl(ROOT_PATH);
                        //});
                    });
                }

            }
        });
    })(Main);

export default MainConnector;