import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {PASSWORD_PATTERN_REGEX} from '../../../../utils/Constants';

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {validateEmail} from '../../../../utils/Validators';
import PasswordField from "../../common/form/PasswordField";

const renderUserNameField = (field) => (
    <div class="createPass__main-input">
        <input type="email"  {...field.input} class={field.meta.error ? `input__false` : `input__true`} {...field} />
        <div>
            <svg class="input__icon">
                <use xlinkHref="#email"></use>
            </svg>
        </div>
    </div>
)

let Form = props => {
    const { handleSubmit, handleCancel } = props;

    return (
        <form onSubmit={handleSubmit}>


            <div class="createPass__main">
                <div class="createPass__main-img">
                    <svg class="change__email-img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs>
                    </defs><title>PasswordAsset 1</title><g id="Layer_2" data-name="Layer 2"><g id="Layer_1-2" data-name="Layer 1"><circle class="cls-1" cx="50" cy="50" r="50"/><rect class="cls-2" x="11.45" y="41.48" width="76.98" height="52.08"/><polygon class="cls-3" points="49.92 6.4 11.45 41.48 49.92 76.65 88.41 41.48 49.92 6.4"/><polygon class="cls-4" points="20.36 49.61 49.06 75.84 50.8 75.84 79.5 49.61 79.5 17.05 20.36 17.05 20.36 49.61"/><polygon class="cls-5" points="56.38 70.52 43.48 70.52 11.45 93.62 88.41 93.62 56.38 70.52"/><path class="cls-6" d="M72.7,41.54A12.27,12.27,0,0,0,49.49,36H29.36l-2.2,3.47v3.15h0l4.56,4.55,2.84-1.73,2.85,1.73,3.85-2.73,3.85,2.73h4.38A12.27,12.27,0,0,0,72.7,41.54Zm-11.78,0a3.26,3.26,0,1,1,3.26,3.26A3.25,3.25,0,0,1,60.92,41.52Z"/><path class="cls-7" d="M60.39,41.52a3.79,3.79,0,1,1,3.79,3.79A3.8,3.8,0,0,1,60.39,41.52Zm6.54,0a2.75,2.75,0,1,0-2.75,2.75A2.75,2.75,0,0,0,66.93,41.52Z"/><rect class="cls-7" x="55.6" y="36.49" width="1.57" height="10.05"/><rect class="cls-7" x="27.16" y="39.41" width="21.8" height="1.37"/></g></g></svg>
                </div>

                <div class="email__text">
                    <p>Please enter your new email address:</p>
                </div>

                <Field name="oldpassword" type="password" placeholder="Сurrent Password*" autocomplete="off" component={PasswordField} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />

                <Field name="username" placeholder="New Email Address*" component={renderUserNameField} validate={validateEmail} required={true} minLength={6} maxLength={254} />


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
    form: 'changeUserName'
})(Form);

class ChangeUserName extends BaseDialog {
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
                        open={true} contentStyle={{ borderRadius: 5, width: 360 }} bodyStyle={{ borderRadius: 5, 'paddingTop': 0, paddingLeft: 0, paddingRight: 0, minHeight: 300 }}>

                        <div class="modal__header">
                            <div class="modal__header-icon" onClick={this.close.bind(this)}>
                                <div>
                                    <svg class="close__icon">
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2>Change Email</h2>
                        </div>

                        <Form page={this} onSubmit={this.reset} handleCancel={this.close} dialog={this} />

                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const ChangeUserNameDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
        });
    })(ChangeUserName);

export default ChangeUserNameDialogConnector;
