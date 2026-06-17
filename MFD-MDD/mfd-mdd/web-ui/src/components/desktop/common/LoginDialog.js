import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import BaseDialog, {baseDispatcherMap, baseStateMap} from './BaseDialog';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import {loadCurrentUser, login} from '../../../actions/common/authorization';

/**
 * A modal dialog can only be closed by selecting one of the actions.
 */
class LoginDialog extends BaseDialog {

    login() {
        this.props.onLogin(this.state, this);
    }

    render() {
        var VisibilitySensor = require('react-visibility-sensor');

        const actions = [
            <FlatButton
                label="Login"
                primary={true}
                disabled={false}
                onClick={this.login.bind(this)}
                />
        ];
        return (

            <div ref={this.props.uid}>
                <MuiThemeProvider>
                    <Dialog
                        title="Login"
                        actions={actions}
                        modal={true}
                        open={true}>
                        login: <input type="text" bind-field="login" />
                        Password: <input type="password" bind-field="password" />
                        <VisibilitySensor onChange={this.onShow.bind(this)} />
                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const LoginDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onLogin: (state, dialog) => {
                dispatch(login(state.managedObject.login, state.managedObject.password)).then(function (result) {
                    loadCurrentUser().then(currentUser => {                        
                        dialog.props.actions.onLogged(currentUser);
                        dialog.close();
                    });
                });
            }
        });
    })(LoginDialog);

export default LoginDialogConnector;
