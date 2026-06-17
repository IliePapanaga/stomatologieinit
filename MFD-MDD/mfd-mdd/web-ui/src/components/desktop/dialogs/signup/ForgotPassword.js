import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import BaseRecaptcha from '../../common/security/BaseRecaptcha';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import {validateEmail} from '../../../../utils/Validators';
import {requestResetPassword} from '../../../../actions/common/security/password';
import {EVENT_CHANGED_RECAPTCHA_HASH} from '../../../../utils/Constants';
import {toastr} from 'react-redux-toastr';

const renderField = (field) => (
    <div class="forgotPass__main-input">
        <input type="email"  {...field.input} placeholder="Email Address" />
        <div>
            <svg class="input__icon">
                <use xlinkHref="#email"></use>
            </svg>
        </div>
    </div>
)


let ForgotPasswordForm = props => {
    const { handleSubmit, handleCancel, recaptchaHash, invalid } = props;

    return (
        <form onSubmit={handleSubmit}>

            <div class="forgotPass__main">
                <div class="forgotPass__main-img">
                    <img src="img/letter.png" alt="letter" />
                </div>
                <div class="forgotPass__main-text">
                    <p>Please enter your email address associated with your System account</p>
                </div>                
                <Field name="email" placeholder="Email Address" component={renderField} validate={validateEmail} required={true} minLength={6} maxLength={254} />

                <div className="forgotPass__main-text check__email">
                    <p>If you did not receive the email, please check your spam or bulk email folder.</p>
                </div>

                <div class="forgotPass__main-text">
                    <BaseRecaptcha />
                </div>
            </div>

            <div class="footer__btn">

                <button class="blue white" onClick={handleCancel}>Cancel</button>
                <button class="blue" disabled={invalid || !recaptchaHash}>Ok</button>
            </div>
        </form >)
}

ForgotPasswordForm = reduxForm({
    form: 'forgotPassword'
})(ForgotPasswordForm);


class ForgotPassword extends BaseDialog {
    submit(data) {}
    render() {
        const paperStyles = {
            borderRadius: 5,
        };
        return (
            <div ref={this.props.uid}>
                <MuiThemeProvider>
                    <Dialog paperProps={{ style: paperStyles }} bodyClassName={"modal forgotPass"}
                        modal={true}
                        open={true} contentStyle={{ borderRadius: 5, width: 360 }} bodyStyle={{ borderRadius: 5, 'paddingTop': 0, paddingLeft: 0, paddingRight: 0, minHeight: 540 }}>
                        <div class="modal__header">
                            <div class="modal__header-icon">
                                <div>
                                    <svg class="close__icon" onClick={this.close.bind(this)}>
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2>Forgot your password</h2>
                        </div>
                        <ForgotPasswordForm onSubmit={this.props.onSend} recaptchaHash={this.props.recaptchaHash} handleCancel={this.close} />
                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const ForgotPasswordDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
            recaptchaHash: state.context.recaptchaHash
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => {
                dispatch(requestResetPassword(sendData.email, form.recaptchaHash)).then(function (result) {
                    if (result) {
                        toastr.success("Resset Password", "Please, check your email, we sent you request for reset your password.");
                        dispatch({ type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined });
                        form.handleCancel();
                    }
                });
            }
        });
    })(ForgotPassword);

export default ForgotPasswordDialogConnector;
