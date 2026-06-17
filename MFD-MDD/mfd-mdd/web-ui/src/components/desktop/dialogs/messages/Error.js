import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../common/BaseDialog';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

// import { login, loadCurrentUser } from '../../../actions/common/authorization';
// import { EVENT_AUTH_USER_LOADED } from '../../../utils/Constants';
/**
 * A modal dialog can only be closed by selecting one of the actions.
 */
class Error extends BaseDialog {

    render() {
        let {managedObject}=this.state;
        var VisibilitySensor = require('react-visibility-sensor');

        const actions = [
            <FlatButton
                label="Save"
                primary={true}
                disabled={false}
                onClick={this.onSave.bind(this)}
                />,
            <FlatButton
                label="Close"
                primary={true}
                disabled={false}
                onClick={this.onClose.bind(this)}
                />
        ];
        return (

            <div ref={this.props.uid}>
                <MuiThemeProvider>
                    <Dialog
                        title="Office"
                        actions={actions}
                        modal={true}
                        open={true}>
                        OrderNumber: <input type="text" bind-field="OrderNumber" />
                        <VisibilitySensor onChange={this.onShow.bind(this)} />
                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const AddEditOfficeDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
        });
    })(AddEditOffice);

export default AddEditOfficeDialogConnector;
