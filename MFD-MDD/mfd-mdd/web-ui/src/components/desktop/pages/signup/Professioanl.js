import React from 'react';
import {connect} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import BrowseDialogButton from '../../common/BrowseDialogButton';
import TextField from '../../common/form/TextField';
import SelectField from '../../common/form/SelectField';
import BaseRecaptcha from '../../common/security/BaseRecaptcha';
import {Field as QueryField} from '../../../../models/core/QueryInfo';
import Footer from '../../common/page/Footer';
import {phone} from '../../../../utils/Normalizers';
import Chooser from '../../dialogs/signup/Chooser';

import {
    EVENT_CHANGED_RECAPTCHA_HASH,
    PASSWORD_PATTERN_REGEX,
    REST_API_PREFIX_PUBLIC,
    TEXTFIELD_PATTERN_REGEX,
    TITLES
} from '../../../../utils/Constants';
import {STATES} from '../../../../data/States';
import UiView from '../../../../utils/UiView';
import ObjectHelper from '../../../../utils/Object';
import FindLocation from '../../dialogs/filters/FindLocation';
import findLocationByCoordinates from '../../../../actions/signup/findLocationByCoordinates';


import {matchPassword, validateEmail} from '../../../../utils/Validators';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import saveData from '../../../../actions/common/saveData';
import checkUserName from '../../../../actions/signup/checkUserName';
import {FieldsInfo} from "../../../../models/core/FieldsInfo";
import {Logger} from 'react-logger-lib';
import ca from '../../../../resources/docs/ca.pdf';
import PasswordField from "../../common/form/PasswordField";
/*import TimeField from "../../common/form/TimeField";
import {defaultStartTime} from "../../../../utils/DateHelper";*/

let SignupForm = props => {
    const {handleSubmit, handleCancel, change, page, showAddressWarning, invalid} = props;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, undefined, false, true);
    return (
        <form className="register__form" onSubmit={handleSubmit}>
            <div class="signup__practice signup__practice-prof">

                <div class="modal__header">
                    <h2>Create a Mayday Dental Staffing Account</h2>
                </div>

                <div class="signup__practice-prof-wrapper">
                <div class="signup__practice-cred">
                    <div class="signup__practice-cred-header">
                        <h3 class="cred">Credentials</h3>
                    </div>

                    <div class="signup__practice-cred-content">
                        {/*<Field readOnly={false} name={`startTime`}*/}
                               {/*component={TimeField}*/}
                               {/*required={true} step={15} time={'15:15'} disabled={false}/>*/}

                        <div class="signup__practice-cred-content-form">
                            <div class="item__box">
                                <div class="text">
                                    <p>Email Address<span class="star">*</span>:</p>
                                </div>
                                <Field name="username" autocomplete="off" component={TextField} validate={validateEmail}
                                       required={true} minLength={6} maxLength={254}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Password<span class="star">*</span>:</p>
                                </div>
                                <Field classField="for__pass-btn" name="password" type="password" autocomplete="off" component={PasswordField}
                                       validate={matchPassword} required={true} minLength={10} maxLength={60}
                                       regexPattern={PASSWORD_PATTERN_REGEX}  hasInputIcon={false} hasValidation={true} fieldWrapperClass="input"/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Re-type Password<span class="star">*</span>:</p>
                                </div>
                                <Field classField="for__pass-btn" name="repassword" type="password" autocomplete="off" component={PasswordField}
                                       validate={matchPassword} required={true} minLength={10} maxLength={60}
                                       regexPattern={PASSWORD_PATTERN_REGEX}  hasInputIcon={false} hasValidation={true} fieldWrapperClass="input"/>
                            </div>
                        </div>

                        <div class="signup__practice-cred-content-help">
                            <div class="signup__practice-cred-content-help-info">

                                <div class="help">
                                    <p>Password must be at least 10 characters long with characters from 3 of these 4
                                        categories:</p>
                                    <ul>
                                        <li>upper case letters</li>
                                        <li>lower case letters</li>
                                        <li>numbers</li>
                                        <li>special characters, i.e "#,!,$,%" etc.</li>
                                    </ul>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>


                <div class="signup__practice-personal">
                    <div class="signup__practice-personal-header">
                        <h3 class="personal">Contact Details</h3>
                    </div>

                    <div class="signup__practice-personal-form">

                        <div class="signup__practice-personal-form-left">


                            <div class="item__box">
                                <div class="text">
                                    <p>Title:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.name.title" component={SelectField} menuItems={TITLES}
                                           required={false}/>
                                </div>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>First Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.name.first" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}
                                       regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Last Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.name.last" autocomplete="off" component={TextField} required={true}
                                       minLength={2} maxLength={66} regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Contact Phone<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.phone" autocomplete="off" component={TextField} required={true}
                                       mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>
                        </div>

                        <div class="signup__practice-personal-form-right">
                            <div class="item__box zip">
                                <div class="text">
                                    <p>ZIP<span class="star">*</span>:</p>
                                </div>

                                {showAddressWarning && [<p class="zip__text">or</p>,
                                    <BrowseDialogButton title="Find My Location" dialog={FindLocation} actions={{
                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                            const {lat, lng} = updatedManagedObject;
                                            findLocationByCoordinates({lat: lat, lng: lng}).then(function (location) {
                                                Logger.of('App.findLocationByCoordinates').info('Location:', location);
                                                if (location) {
                                                    change(`contact.address.zipCode`, location.zipCode);
                                                    change(`contact.address.city`, location.city);
                                                    if (location.route && location.streetNumber) {
                                                        change(`contact.address.street`, `${location.streetNumber} ${location.route}`);
                                                    }
                                                    if (STATES.findIndex(function (item) {
                                                            return item.code === location.state
                                                        }) >= 0) {
                                                        change(`contact.address.state`, location.state);
                                                    } else {
                                                        change(`contact.address.state`, '');
                                                    }
                                                }

                                                change(`contact.address.longitude`, lat);
                                                change(`contact.address.latitude`, lng);
                                                editor.close();
                                            });
                                        }
                                    }} browseProps={{showDistance: false}}/>]}
                                <Field name="contact.address.zipCode" autocomplete="off" component={TextField}
                                       required={true} mask="99999" maskChar="_" onChange={changedZipCode}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Street<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.address.street" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>City<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.address.city" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>State<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.address.state" component={SelectField} menuItems={STATES}
                                           required={true}/>
                                </div>
                            </div>
                            <div class="item__box" style={{display: 'none'}}>
                                <div class="text">
                                    <p>Latitude <span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.address.latitude" component={TextField} required={true}/>
                                </div>
                            </div>
                            <div class="item__box" style={{display: 'none'}}>
                                <div class="text">
                                    <p>Longitude<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.address.longitude" component={TextField} required={true}/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


                {/*-------------------------------------------*/}

                {showAddressWarning && <div class="location__box">
                    <div class="location__box-text">
                        <p>Unfortunately, we cannot find your address on the map. Please, make sure the address is
                            correct, and if it is, please, use "FIND MY LOCATION" button and find your location on the map for
                            us.</p>
                    </div>
                </div>}

                {/*--------------------------------------------*/}

                <div class="signup__practice-company">


                    <div class="signup__practice-help signup__practice-help-professional">
                        {/*  <p class="signup__practice-help-red">Fields marked * are mandatory</p>*/}
                        <p>By creating an Mayday Dental Staffing Account, you agree to Mayday Dental Staffing's <a onClick={function(){
                            UiView.showResource(ca, 'Terms And Conditions');
                        }}>Terms
                            And Conditions</a> and agree to be contacted by employers via Mayday Dental Staffing</p>

                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>


                    <div class="signup__practice-signature">
                        <div class="signup__practice-signature-content">
                            <BaseRecaptcha/>
                        </div>

                    </div>
                </div>

                </div>
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Back</button>
                    <button disabled={invalid || !page.props.recaptchaHash} class="blue">register</button>
                </div>
            </div>
        </form>
    )
}

// {false&&<SignatureCanvas penColor='green'
//                                     canvasProps={{ width: 200, height: 70, className: 'sigCanvas' }} ref={(ref) => { page.sigCanvas = ref } } />
//                                 }

SignupForm = reduxForm({
    form: 'signup',
    asyncValidate: checkUserName,
    asyncBlurFields: ['username']
})(SignupForm);

const selector = formValueSelector('signup');

SignupForm = connect(state => {
    const longitude = selector(state, 'contact.address.longitude');
    const latitude = selector(state, 'contact.address.latitude');
    const zipCode = selector(state, 'contact.address.zipCode');
    return {
        showAddressWarning: (!longitude && !latitude) && zipCode && zipCode.indexOf('_') < 0
    }
})(SignupForm)


/**
 * Main application component. Containts base navigation panels and base view
 */
class Professioanl extends BasePage {
    submit(data) {
        let me = this;
        let registrationData = ObjectHelper.copyObject(data);
        // let signature = this.sigCanvas.getTrimmedCanvas()
        //     .toDataURL('image/png');
        delete registrationData.agree;
        delete registrationData.repassword;
        // registrationData.signature = signature.substr(signature.indexOf(',') + 1);
        Logger.of('App.Signup.submit').info('Signup data:', registrationData);

        me.props.onSignup(registrationData, me.props.router, me.props.recaptchaHash);
    }

    cancel() {
        let router = this.props.router;
        router.navigateToUrl("/");

        UiView.showDialog(<Chooser store={UiView.createDialogStore()} managedObject={{}} actions={{
            goTo: function (url) {
                router.navigateToUrl(url);
            }
        }}/>);
    }

    clearSignatureCanvas() {
        this.sigCanvas.clear();
    }

    render() {
        return ([
            <SignupForm onSubmit={this.submit.bind(this)} handleCancel={this.cancel.bind(this)} page={this}
                        clearSignatureCanvas={this.clearSignatureCanvas.bind(this)} key="mainForm"/>,
            <div class="logo__absolute" key="logo">
                <img src="img/logo-abs.png" alt="logo"/>
            </div>,
            <Footer key="footer"/>
        ]);
    }
}

const ProfessioanlConnector = connect(function (state, ownProps) {

        return Object.assign(basePageStateMap(state, ownProps), {
            recaptchaHash: state.context.recaptchaHash
        });
    },
    function (dispatch) {
        return Object.assign(basePageDispatcherMap(dispatch), {
            onSignup: (signupData, router, recaptchaHash) => {
                dispatch(saveData({
                    queryName: 'registerProfessional',
                    uri: REST_API_PREFIX_PUBLIC,
                    fields: [new FieldsInfo({select: [new QueryField('id')]})]
                }, {professional: signupData}, recaptchaHash)).then(function (result) {
                    if (result && result.id) {
                        dispatch({type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined});
                        localStorage.setItem('email', signupData.username);
                        localStorage.setItem('id', result.id);
                        localStorage.setItem('professional', 1);
                        router.navigateToUrl("/signup/complete");
                    }
                });
            }
        });
    })(Professioanl);

export default ProfessioanlConnector;