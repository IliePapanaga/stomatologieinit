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

const renderPhoneField = (field) => (
    <div class="createPass__main-input">
        <input type="phone"  {...field.input} class={field.meta.error ? `input__false` : `input__true`} {...field} />
        <div>
            <svg class="input__icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 44 44">
                <defs>
                    <style>.cls-1</style>
                </defs>
                <title>phoneAsset 8</title>
                <g id="Layer_2" data-name="Layer 2">
                    <g id="General">
                        <g id="Phone_1_2x.png" data-name="Phone 1@2x.png">
                            <path class="cls-1"
                                  d="M44,35h0a3,3,0,0,0-.87-2.12,3.07,3.07,0,0,0-.54-.41l-8.46-6.59A3,3,0,0,0,32,25a3,3,0,0,0-1.79.62L27.82,28h0a2,2,0,0,1-3.12-.18l0,.06a38,38,0,0,1-8.53-8.49l.06-.05A2,2,0,0,1,16,16.13h0l2.35-2.33A3,3,0,0,0,19,12a3,3,0,0,0-.88-2.12L11.54,1.41a3.06,3.06,0,0,0-.42-.53A3,3,0,0,0,9,0C5,0,0,4.7,0,10.5A10.34,10.34,0,0,0,1,15l0,0A63.59,63.59,0,0,0,29,43l0,0a10.34,10.34,0,0,0,4.48,1C39.3,44,44,39,44,35ZM33.5,42a8.55,8.55,0,0,1-3.63-.82l-.33-.12A61.31,61.31,0,0,1,3,14.47a1.58,1.58,0,0,0-.13-.34A8.55,8.55,0,0,1,2,10.5C2,5.65,6.25,2,9,2a1,1,0,0,1,.71.29,1.33,1.33,0,0,1,.13.19l.12.16,6.58,8.47.17.18A1,1,0,0,1,17,12a.91.91,0,0,1-.15.5l-2.22,2.21,0,0a4,4,0,0,0-.17,5.67l.07.12a40.3,40.3,0,0,0,9,8.94l.08.06a4,4,0,0,0,5.57,0,.52.52,0,0,0,.11-.1l2.24-2.26A.94.94,0,0,1,32,27a1,1,0,0,1,.71.3l.19.16L41.34,34l.17.11a.67.67,0,0,1,.18.14A1,1,0,0,1,42,35c0,.07,0,.14,0,.21C41.84,37.94,38.24,42,33.5,42Z"/>
                        </g>
                    </g>
                </g>
            </svg>
        </div>
    </div>
)

let Form = props => {
    const {handleSubmit, handleCancel} = props;

    return (
        <form onSubmit={handleSubmit}>


            <div class="createPass__main">
                <div class="createPass__main-img">

                    <svg class="change__phone-img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
                        <defs>
                        </defs>
                        <title>phoneAsset 2</title>
                        <g id="Layer_2" data-name="Layer 2">
                            <g id="Layer_1-2" data-name="Layer 1">
                                <g id="Layer_1-3" data-name="Layer 1">
                                    <circle class="cls-1" cx="50" cy="50" r="50"/>
                                </g>
                                <g id="Isolation_Mode" data-name="Isolation Mode">
                                    <path class="cls-2"
                                          d="M67.79,88H32.07a4.24,4.24,0,0,1-4.24-4.24V77.57H72v6.15A4.25,4.25,0,0,1,67.79,88Z"/>
                                    <path class="cls-2"
                                          d="M32.07,12.57H67.79A4.24,4.24,0,0,1,72,16.81V23H27.83V16.81A4.24,4.24,0,0,1,32.07,12.57Z"/>
                                    <path class="cls-3"
                                          d="M56.44,19.07h-13a1.3,1.3,0,1,1,0-2.59h13a1.3,1.3,0,0,1,1.3,1.29A1.31,1.31,0,0,1,56.44,19.07Z"/>
                                    <rect class="cls-4" x="27.83" y="22.98" width="44.21" height="54.62"/>
                                    <g class="cls-5">
                                        <path class="cls-6"
                                              d="M78.53,60.7a1.35,1.35,0,0,1-.37-.06A1.29,1.29,0,0,1,77.28,59a31.46,31.46,0,0,0,0-17.49,1.3,1.3,0,0,1,2.49-.73,33.84,33.84,0,0,1,1.34,9.46,33.3,33.3,0,0,1-1.36,9.49A1.29,1.29,0,0,1,78.53,60.7Z"/>
                                        <path class="cls-6"
                                              d="M21.33,60.7a1.28,1.28,0,0,1-1.24-.94,33.64,33.64,0,0,1-1.35-9.49,34.21,34.21,0,0,1,1.33-9.46,1.3,1.3,0,0,1,2.49.73,31.31,31.31,0,0,0-1.23,8.73A30.67,30.67,0,0,0,22.58,59a1.29,1.29,0,0,1-.88,1.61A1.19,1.19,0,0,1,21.33,60.7Z"/>
                                        <path class="cls-6"
                                              d="M14.83,65.89a1.36,1.36,0,0,1-1.2-.77,37.58,37.58,0,0,1-2.71-14.85,37.39,37.39,0,0,1,2.71-14.82A1.29,1.29,0,0,1,16,36.51a34.73,34.73,0,0,0-2.5,13.76A34.68,34.68,0,0,0,16,64a1.29,1.29,0,0,1-.65,1.71A1,1,0,0,1,14.83,65.89Z"/>
                                        <path class="cls-6"
                                              d="M85,65.89a1.24,1.24,0,0,1-.53-.12,1.29,1.29,0,0,1-.65-1.71,34.76,34.76,0,0,0,2.49-13.77,34.67,34.67,0,0,0-2.49-13.76,1.29,1.29,0,1,1,2.35-1.06,37.46,37.46,0,0,1,2.71,14.82,37.48,37.48,0,0,1-2.71,14.83A1.28,1.28,0,0,1,85,65.89Z"/>
                                    </g>
                                    <circle class="cls-7" cx="49.92" cy="50" r="14.77"/>
                                    <path class="cls-2"
                                          d="M48.41,56.07a1.58,1.58,0,0,1-1.08-.45l-4.54-4.54a1.51,1.51,0,0,1,2.14-2.14l4.54,4.54a1.51,1.51,0,0,1,0,2.14A1.48,1.48,0,0,1,48.41,56.07Z"/>
                                    <path class="cls-2"
                                          d="M48.41,56.07a1.47,1.47,0,0,1-1-.36,1.52,1.52,0,0,1-.2-2.14l7.56-9.09a1.52,1.52,0,0,1,2.34,2l-7.56,9.09A1.62,1.62,0,0,1,48.41,56.07Z"/>
                                    <circle class="cls-3" cx="49.92" cy="82.74" r="3.48"/>
                                </g>
                            </g>
                        </g>
                    </svg>
                </div>

                <div class="email__text">
                    <p>Please enter your new phone:</p>
                </div>

                <Field name="oldpassword" type="password" placeholder="Сurrent Password*" autocomplete="off"
                       component={PasswordField} required={true} minLength={10} maxLength={60}
                       regexPattern={PASSWORD_PATTERN_REGEX}/>

                <Field name="username" placeholder="New Phone*" component={renderPhoneField} validate={validateEmail}
                       required={true} minLength={6} maxLength={254}/>


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
    form: 'changePhone'
})(Form);

class ChangePhone extends BaseDialog {
    reset(data, d, form) {
        form.page.props.actions.reset(data, form.dialog);
    }

    render() {
        const paperStyles = {
            borderRadius: 5,
        };

        return (
            <div ref={this.props.uid}>
                <MuiThemeProvider>
                    <Dialog className="modal__static-change"
                            modal={true} paperProps={{style: paperStyles}} bodyClassName="modal createPass"
                            open={true} contentStyle={{borderRadius: 5, width: 360}} bodyStyle={{
                        borderRadius: 5,
                        'paddingTop': 0,
                        paddingLeft: 0,
                        paddingRight: 0,
                        minHeight: 300
                    }}>

                        <div class="modal__header">
                            <div class="modal__header-icon" onClick={this.close.bind(this)}>
                                <div>
                                    <svg class="close__icon">
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2>Change Phone</h2>
                        </div>

                        <Form page={this} onSubmit={this.reset} handleCancel={this.close} dialog={this}/>

                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const ChangePhoneDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ChangePhone);

export default ChangePhoneDialogConnector;
