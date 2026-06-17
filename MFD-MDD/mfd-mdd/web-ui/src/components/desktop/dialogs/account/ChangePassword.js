import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {PASSWORD_PATTERN_REGEX} from '../../../../utils/Constants';

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {matchPassword} from '../../../../utils/Validators';
import PasswordField from "../../common/form/PasswordField";


let Form = props => {
    const { handleSubmit, handleCancel } = props;

    return (
        <form onSubmit={handleSubmit}>


            <div class="createPass__main">
                <div class="createPass__main-img">
                    <svg class="change__pass-img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs>
                    </defs><title>change_passwordAsset 1</title><g id="Layer_2" data-name="Layer 2"><g id="Layer_1-2" data-name="Layer 1"><circle class="cls-1" cx="50" cy="50" r="50"/><path class="cls-2" d="M93.42,61.33A23.44,23.44,0,0,0,49.1,50.69H10.64l-4.2,6.64v6h0l8.7,8.7,5.44-3.29L26,72l7.37-5.2L40.75,72h8.37a23.43,23.43,0,0,0,44.3-10.7Zm-22.5,0a6.23,6.23,0,1,1,6.22,6.23A6.21,6.21,0,0,1,70.92,61.29Z"/><rect class="cls-3" x="60.74" y="51.69" width="3" height="19.21"/><rect class="cls-3" x="6.44" y="57.29" width="41.63" height="2.63"/><path class="cls-4" d="M29,19.72,25,25v.1l6.3-.85v2.85L25,26.34v.09l4,5L26.36,33,23.84,27.1h-.1L21.05,33l-2.39-1.51,4-5.11v-.09l-6.2.8V24.23l6.16.81v-.1l-4-5.1,2.55-1.46,2.61,5.81h.1l2.55-5.85Z"/><path class="cls-4" d="M46.35,19.72l-4,5.26v.1l6.3-.85v2.85l-6.3-.74v.09l4,5L43.74,33,41.22,27.1h-.1L38.43,33,36,31.48l4-5.11v-.09l-6.2.8V24.23L40,25v-.1l-4-5.1,2.55-1.46,2.61,5.81h.1l2.55-5.85Z"/><path class="cls-4" d="M63.73,19.72l-4,5.26v.1l6.3-.85v2.85l-6.3-.74v.09l4,5L61.12,33,58.6,27.1h-.1L55.81,33l-2.39-1.51,4-5.11v-.09l-6.2.8V24.23l6.16.81v-.1l-4-5.1,2.55-1.46,2.61,5.81h.1l2.55-5.85Z"/><path class="cls-4" d="M81.13,19.72l-4,5.26v.1l6.3-.85v2.85l-6.3-.74v.09l4,5L78.52,33,76,27.1h-.1L73.21,33l-2.39-1.51,4-5.11v-.09l-6.2.8V24.23l6.16.81v-.1l-4-5.1,2.55-1.46,2.61,5.81H76l2.55-5.85Z"/></g></g></svg>
                </div>


                <Field name="oldpassword" type="password" placeholder="Сurrent Password*" autocomplete="off" component={PasswordField} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />




                <div class="createPass__main-text">
                    <div class="help">
                        <p>Password must be at least 10 characters long with characters from 3 of these 4 categories:</p>
                        <ul>
                            <li>upper case letters</li>
                            <li>lower case letters</li>
                            <li>numbers</li>
                            <li>special characters, i.e "#,!,$,%" etc.</li>
                        </ul>
                    </div>
                </div>

                <Field name="password" type="password" placeholder="New Password*" autocomplete="off" component={PasswordField} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />
                <Field name="repassword" type="password" placeholder="Confirm Password*" autocomplete="off" component={PasswordField} validate={matchPassword} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />


                <div class="createPass__main-help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>

            </div>
            <div class="footer__btn">
                <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                <button class="blue" type="button" onClick={handleSubmit}>Ok</button>
            </div>
        </form >)
}

Form = reduxForm({
    form: 'changePassword'
})(Form);

class ChangePassword extends BaseDialog {
    reset(data, d, form) {
        form.page.props.actions.reset(data, form.dialog);
    }
    render() {
        const paperStyles = {
            borderRadius: 5,
        };

        return (
            <div ref={this.props.uid} >
                <MuiThemeProvider>
                    <Dialog className="modal__static-change"
                        modal={true} paperProps={{ style: paperStyles }} bodyClassName="modal createPass"
                        open={true} contentStyle={{ borderRadius: 5, width: 360 }} bodyStyle={{ borderRadius: 5, 'paddingTop': 0, paddingLeft: 0, paddingRight: 0, minHeight: 600 }}>

                        <div class="modal__header">
                            <div class="modal__header-icon" onClick={this.close.bind(this)}>
                                <div>
                                    <svg class="close__icon">
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2>Change Password</h2>
                        </div>

                        <Form page={this} onSubmit={this.reset} handleCancel={this.close} dialog={this} />

                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const ChangePasswordDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
        });
    })(ChangePassword);

export default ChangePasswordDialogConnector;
